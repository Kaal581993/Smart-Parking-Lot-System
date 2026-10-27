package com.smart.parking.lot.smart.parking.lot.controller.auth;

import com.smart.parking.lot.smart.parking.lot.dto.PasswordChangeRequest;
import com.smart.parking.lot.smart.parking.lot.dto.ProfileUpdateRequest;
import com.smart.parking.lot.smart.parking.lot.dto.UserProfileResponse;
import com.smart.parking.lot.smart.parking.lot.service.auth.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth/profile")
public class UserProfileController {
    private final UserProfileService profiles;

    public UserProfileController(UserProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public UserProfileResponse getProfile(Authentication authentication) {
        return profiles.getProfile(authentication.getName());
    }

    @PutMapping
    public UserProfileResponse updateProfile(Authentication authentication, @Valid @RequestBody ProfileUpdateRequest request) {
        return profiles.updateProfile(authentication.getName(), request.displayName());
    }

    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponse uploadImage(Authentication authentication, @RequestPart("image") MultipartFile image) {
        return profiles.uploadImage(authentication.getName(), image);
    }

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(Authentication authentication) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff").body(profiles.getImage(authentication.getName()));
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> removeImage(Authentication authentication) {
        profiles.removeImage(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                              @RequestHeader("Authorization") String authorization,
                                              @Valid @RequestBody PasswordChangeRequest request) {
        profiles.changePassword(authentication.getName(), request, authorization.substring(7));
        return ResponseEntity.noContent().build();
    }
}
