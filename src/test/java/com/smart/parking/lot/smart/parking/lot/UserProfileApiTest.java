package com.smart.parking.lot.smart.parking.lot;

import com.smart.parking.lot.smart.parking.lot.entity.auth.AppUser;
import com.smart.parking.lot.smart.parking.lot.repository.auth.AppUserRepository;
import com.smart.parking.lot.smart.parking.lot.repository.auth.UserProfileRepository;
import com.smart.parking.lot.smart.parking.lot.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserProfileApiTest {
    @LocalServerPort private int port;
    @Autowired private AuthService auth;
    @Autowired private AppUserRepository users;
    @Autowired private UserProfileRepository profiles;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JsonMapper json;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String OLD_PASSWORD = "original-pass-123";
    private AppUser owner;
    private AppUser other;
    private String token;
    private String otherToken;

    @BeforeEach
    void signInTwoOwners() {
        owner = auth.registerOwner("profile-" + UUID.randomUUID(), OLD_PASSWORD, "990000009001");
        other = auth.registerOwner("profile-" + UUID.randomUUID(), OLD_PASSWORD, "990000009002");
        token = auth.login(owner.getUsername(), OLD_PASSWORD);
        otherToken = auth.login(other.getUsername(), OLD_PASSWORD);
    }

    @Test
    void profileAndImageRequireAuthentication() throws Exception {
        assertEquals(401, request("GET", "/auth/profile", null, null).statusCode());
        assertEquals(401, request("GET", "/auth/profile/image", null, null).statusCode());
        assertEquals(401, request("PUT", "/auth/profile/password", null,
                passwordRequest(OLD_PASSWORD, "new-password-123", "new-password-123")).statusCode());
    }

    @Test
    void ownerCanChangeOnlyTheirOwnDisplayNameWithoutAdministrativePrivileges() throws Exception {
        var before = json.readTree(request("GET", "/auth/profile", token, null).body());
        assertEquals(owner.getUsername(), before.at("/displayName").asString());
        assertEquals("OWNER", before.at("/role").asString());
        assertFalse(before.has("password"));
        var updated = request("PUT", "/auth/profile", token,
                Map.of("displayName", "  Demo Owner  ", "id", other.getId(), "role", "ADMIN"));
        assertEquals(200, updated.statusCode(), updated.body());
        var body = json.readTree(updated.body());
        assertEquals("Demo Owner", body.at("/displayName").asString());
        assertEquals(owner.getId().longValue(), body.at("/id").asLong());
        assertEquals("OWNER", body.at("/role").asString());
        var unaffected = json.readTree(request("GET", "/auth/profile", otherToken, null).body());
        assertEquals(other.getUsername(), unaffected.at("/displayName").asString());
        assertEquals(400, request("PUT", "/auth/profile", token, Map.of("displayName", " ")).statusCode());
        assertEquals(404, request("GET", "/auth/profile/" + other.getId(), token, null).statusCode());
    }

    @Test
    void imageIsPersistedNormalizedPrivateAndRemovable() throws Exception {
        var uploaded = upload(png(), "image/png");
        assertEquals(200, uploaded.statusCode(), uploaded.body());
        assertTrue(json.readTree(uploaded.body()).at("/hasProfileImage").asBoolean());
        assertNotNull(profiles.findById(owner.getId()).orElseThrow().getImageData());
        var imageRequest = HttpRequest.newBuilder(uri("/auth/profile/image"))
                .header("Authorization", "Bearer " + token).GET().build();
        var image = client.send(imageRequest, HttpResponse.BodyHandlers.ofByteArray());
        assertEquals(200, image.statusCode());
        assertEquals("image/png", image.headers().firstValue("Content-Type").orElseThrow());
        assertTrue(image.headers().firstValue("Cache-Control").orElseThrow().contains("no-store"));
        var decoded = ImageIO.read(new ByteArrayInputStream(image.body()));
        assertEquals(512, decoded.getWidth());
        assertEquals(512, decoded.getHeight());
        assertEquals(404, request("GET", "/auth/profile/image", otherToken, null).statusCode());
        assertEquals(204, request("DELETE", "/auth/profile/image", token, null).statusCode());
        assertEquals(404, request("GET", "/auth/profile/image", token, null).statusCode());
        assertFalse(json.readTree(request("GET", "/auth/profile", token, null).body()).at("/hasProfileImage").asBoolean());
    }

    @Test
    void invalidAndOversizedUploadsAreRejectedWithoutLosingTheExistingImage() throws Exception {
        assertEquals(200, upload(png(), "image/png").statusCode());
        byte[] original = profiles.findById(owner.getId()).orElseThrow().getImageData();
        assertEquals(400, upload("<svg><script>alert(1)</script></svg>".getBytes(StandardCharsets.UTF_8), "image/png").statusCode());
        assertEquals(400, upload(png(), "image/svg+xml").statusCode());
        var oversized = upload(new byte[2 * 1024 * 1024 + 1], "image/png");
        assertEquals(413, oversized.statusCode(), oversized.body());
        assertTrue(oversized.body().contains("2 MB"), oversized.body());
        assertArrayEquals(original, profiles.findById(owner.getId()).orElseThrow().getImageData());
        assertEquals(200, request("GET", "/auth/me", token, null).statusCode());
    }

    @Test
    void invalidPasswordChangesLeaveCredentialsAndSessionsUsable() throws Exception {
        for (var payload : java.util.List.of(
                passwordRequest("incorrect", "new-password-123", "new-password-123"),
                passwordRequest(OLD_PASSWORD, "new-password-123", "different-password"),
                passwordRequest(OLD_PASSWORD, "short", "short"),
                passwordRequest(OLD_PASSWORD, OLD_PASSWORD, OLD_PASSWORD),
                passwordRequest(OLD_PASSWORD, "é".repeat(40), "é".repeat(40)))) {
            var response = request("PUT", "/auth/profile/password", token, payload);
            assertEquals(400, response.statusCode(), response.body());
            assertTrue(response.body().contains("error"));
        }
        assertNotNull(auth.login(owner.getUsername(), OLD_PASSWORD));
        assertEquals(200, request("GET", "/auth/me", token, null).statusCode());
        assertEquals(200, request("GET", "/auth/me", otherToken, null).statusCode());
    }

    @Test
    void passwordChangeKeepsThisSessionAndRevokesOnlyTheSameUsersOtherSessions() throws Exception {
        String oldSession = auth.login(owner.getUsername(), OLD_PASSWORD);
        var response = request("PUT", "/auth/profile/password", token,
                passwordRequest(OLD_PASSWORD, "new-password-123", "new-password-123"));
        assertEquals(204, response.statusCode(), response.body());
        assertEquals(200, request("GET", "/auth/me", token, null).statusCode());
        assertEquals(401, request("GET", "/auth/me", oldSession, null).statusCode());
        assertEquals(200, request("GET", "/auth/me", otherToken, null).statusCode());
        assertThrows(IllegalArgumentException.class, () -> auth.login(owner.getUsername(), OLD_PASSWORD));
        assertNotNull(auth.login(owner.getUsername(), "new-password-123"));
        assertTrue(encoder.matches("new-password-123", users.findById(owner.getId()).orElseThrow().getPassword()));
        assertFalse(request("GET", "/auth/profile", token, null).body().contains("password"));
    }

    @Test
    void deletingAnAccountAlsoDeletesItsProfile() throws Exception {
        assertEquals(200, request("PUT", "/auth/profile", token, Map.of("displayName", "Disposable Owner")).statusCode());
        assertTrue(profiles.existsById(owner.getId()));
        auth.deleteUser(owner.getId());
        assertFalse(profiles.existsById(owner.getId()));
    }

    private Map<String, String> passwordRequest(String current, String next, String confirm) {
        return Map.of("currentPassword", current, "newPassword", next, "confirmPassword", confirm);
    }

    private URI uri(String path) { return URI.create("http://localhost:" + port + path); }

    private HttpResponse<String> request(String method, String path, String bearer, Object payload) throws Exception {
        var request = HttpRequest.newBuilder(uri(path));
        if (bearer != null) request.header("Authorization", "Bearer " + bearer);
        if (payload != null) request.header("Content-Type", "application/json");
        request.method(method, payload == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private byte[] png() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(16, 12, BufferedImage.TYPE_INT_RGB), "png", output);
        return output.toByteArray();
    }

    private HttpResponse<String> upload(byte[] image, String type) throws Exception {
        String boundary = "ProfileTestBoundary";
        var body = new ByteArrayOutputStream();
        body.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"image\"; filename=\"avatar.png\"\r\n"
                + "Content-Type: " + type + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        body.write(image);
        body.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        var request = HttpRequest.newBuilder(uri("/auth/profile/image"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray())).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
