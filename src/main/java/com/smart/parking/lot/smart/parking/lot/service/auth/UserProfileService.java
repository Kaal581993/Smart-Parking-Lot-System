package com.smart.parking.lot.smart.parking.lot.service.auth;

import com.smart.parking.lot.smart.parking.lot.dto.PasswordChangeRequest;
import com.smart.parking.lot.smart.parking.lot.dto.UserProfileResponse;
import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import com.smart.parking.lot.smart.parking.lot.entity.auth.UserProfile;
import com.smart.parking.lot.smart.parking.lot.repository.auth.AppUserRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserProfileService {
    private static final int MAX_IMAGE_BYTES = 2 * 1024 * 1024;
    private final AppUserRepository users;
    private final UserProfileRepository profiles;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserProfileService(AppUserRepository users, UserProfileRepository profiles,
                              PasswordEncoder passwordEncoder, AuthService authService) {
        this.users = users;
        this.profiles = profiles;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public UserProfileResponse getProfile(String username) {
        AppUser user = user(username);
        return response(user, profiles.findById(user.getId()).orElse(null));
    }

    @Transactional
    public UserProfileResponse updateProfile(String username, String displayName) {
        AppUser user = user(username);
        UserProfile profile = profile(user);
        profile.setDisplayName(displayName.strip());
        return response(user, profiles.save(profile));
    }

    @Transactional
    public UserProfileResponse uploadImage(String username, MultipartFile image) {
        byte[] normalized = normalizeImage(image);
        AppUser user = user(username);
        UserProfile profile = profile(user);
        profile.setImageData(normalized);
        profile.setImageUpdatedAt(Instant.now());
        return response(user, profiles.save(profile));
    }

    public byte[] getImage(String username) {
        UserProfile profile = profiles.findById(user(username).getId()).orElse(null);
        if (profile == null || profile.getImageData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No profile image has been uploaded.");
        }
        return profile.getImageData();
    }

    @Transactional
    public void removeImage(String username) {
        profiles.findById(user(username).getId()).ifPresent(profile -> {
            profile.setImageData(null);
            profile.setImageUpdatedAt(Instant.now());
            profiles.save(profile);
        });
    }

    @Transactional
    public void changePassword(String username, PasswordChangeRequest request, String currentToken) {
        AppUser user = user(username);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match.");
        }
        if (request.newPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("New password is too long. Please use fewer characters.");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Choose a password different from your current password.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        users.saveAndFlush(user);
        authService.invalidateOtherSessions(username, currentToken);
    }

    private AppUser user(String username) {
        return users.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in again to continue."));
    }

    private UserProfile profile(AppUser user) {
        return profiles.findById(user.getId()).orElseGet(() -> {
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            return profile;
        });
    }

    private UserProfileResponse response(AppUser user, UserProfile profile) {
        String displayName = profile == null || profile.getDisplayName() == null
                || profile.getDisplayName().isBlank() ? user.getUsername() : profile.getDisplayName();
        return new UserProfileResponse(user.getId(), user.getUsername(), displayName,
                user.getRole() == null ? "" : user.getRole().getName(), user.getOwnerId(),
                profile != null && profile.getImageData() != null,
                profile == null ? null : profile.getImageUpdatedAt());
    }

    private byte[] normalizeImage(MultipartFile image) {
        if (image.isEmpty()) throw new IllegalArgumentException("Choose an image to upload.");
        if (image.getSize() > MAX_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Profile image must be 2 MB or smaller.");
        }
        if (!Set.of("image/jpeg", "image/png").contains(image.getContentType() == null ? "" : image.getContentType())) {
            throw new IllegalArgumentException("Choose a JPG or PNG image.");
        }
        try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(image.getBytes()))) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("The file is not a valid JPG or PNG image.");
            ImageReader reader = readers.next();
            try {
                if (!Set.of("jpeg", "png").contains(reader.getFormatName().toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("Choose a JPG or PNG image.");
                }
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width < 1 || height < 1 || width > 4096 || height > 4096) {
                    throw new IllegalArgumentException("Image dimensions must be no larger than 4096 × 4096 pixels.");
                }
                BufferedImage source = reader.read(0);
                BufferedImage result = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = result.createGraphics();
                try {
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    int side = Math.min(width, height);
                    int x = (width - side) / 2;
                    int y = (height - side) / 2;
                    graphics.drawImage(source, 0, 0, 512, 512, x, y, x + side, y + side, null);
                } finally {
                    graphics.dispose();
                }
                var output = new ByteArrayOutputStream();
                ImageIO.write(result, "png", output);
                return output.toByteArray();
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("The image could not be read. Choose another JPG or PNG image.");
        }
    }
}
