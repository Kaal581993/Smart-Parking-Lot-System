package com.smart.parking.lot.smart.parking.lot;

import com.smart.parking.lot.smart.parking.lot.service.VehicleDetailsService;
import com.smart.parking.lot.smart.parking.lot.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

// Real HTTP is intentional: MockMvc does not perform Tomcat's error redispatch.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VehicleApiSecurityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @MockitoBean
    private VehicleDetailsService vehicleDetailsService;

    private final HttpClient client = HttpClient.newHttpClient();

    private static final String VALID_PAYLOAD = """
            {"numberPlate":"MH10AB1234","vehicleType":{"typeId":"VT3928"},
             "owner":{"aadhaar":{"aadhaarNo":"592478644964"}},
             "vehicleBrand":"Maruti","vehicleModel":"Celerio 2014"}
            """;

    @Test
    void unexpectedVehicleFailurePreservesServerErrorInsteadOfReturning401() throws Exception {
        when(vehicleDetailsService.createVehicleDetails(any()))
                .thenThrow(new UnsupportedOperationException("Simulated persistence failure"));

        HttpResponse<String> response = post(VALID_PAYLOAD, authService.login("admin", "admin123"));

        assertEquals(500, response.statusCode(), response.body());
        assertTrue(response.body().contains("error"), response.body());
    }

    @Test
    void invalidPlateReturnsAReadable400ForAuthenticatedAdmin() throws Exception {
        HttpResponse<String> response = post(VALID_PAYLOAD.replace("MH10AB1234", "MH101234"),
                authService.login("admin", "admin123"));

        assertEquals(400, response.statusCode(), response.body());
        assertTrue(response.body().contains("numberPlate"), response.body());
        verifyNoInteractions(vehicleDetailsService);
    }

    @Test
    void adminCanCreateVehicleWithoutProvidingGeneratedId() throws Exception {
        when(vehicleDetailsService.createVehicleDetails(any())).thenAnswer(call -> call.getArgument(0));

        assertEquals(201, post(VALID_PAYLOAD, authService.login("admin", "admin123")).statusCode());
    }

    @Test
    void slotAllocatorWithCreatePrivilegeCanCreateVehicle() throws Exception {
        var privilege = authService.createPrivilege("CREATE_VEHICLES");
        authService.createRole("Slot Allocator Test", java.util.Set.of(privilege.getId()));
        authService.createUser("slot-test", "test-password", "Slot Allocator Test", null);
        when(vehicleDetailsService.createVehicleDetails(any())).thenAnswer(call -> call.getArgument(0));

        String token = authService.login("slot-test", "test-password");
        assertEquals(201, post(VALID_PAYLOAD, token).statusCode());
        var session = JsonMapper.builder().build().readTree(get("/auth/me", token).body());
        assertTrue(session.at("/permissions/vehicles/create").asBoolean());
    }

    @Test
    void authenticatedReaderCannotCreateVehicle() throws Exception {
        var privilege = authService.createPrivilege("READ_VEHICLES");
        authService.createRole("Reader Test", java.util.Set.of(privilege.getId()));
        authService.createUser("reader-test", "test-password", "Reader Test", null);

        String token = authService.login("reader-test", "test-password");
        HttpResponse<String> response = post(VALID_PAYLOAD, token);

        assertEquals(403, response.statusCode(), response.body());
        assertTrue(response.body().contains("error"), response.body());
        var session = JsonMapper.builder().build().readTree(get("/auth/me", token).body());
        assertTrue(session.at("/permissions/vehicles/read").asBoolean());
        assertEquals(false, session.at("/permissions/vehicles/create").asBoolean());
    }

    @Test
    void missingTokenReturnsReadable401() throws Exception {
        HttpResponse<String> response = post(VALID_PAYLOAD, null);

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("error"), response.body());
    }

    @Test
    void duplicateVehicleReturns409InsteadOf401() throws Exception {
        when(vehicleDetailsService.createVehicleDetails(any()))
                .thenThrow(new IllegalStateException("Number plate already registered to another vehicle"));

        HttpResponse<String> response = post(VALID_PAYLOAD, authService.login("admin", "admin123"));

        assertEquals(409, response.statusCode(), response.body());
        assertTrue(response.body().contains("already registered"), response.body());
    }

    @Test
    void legacyVehicleCreatePrivilegeStillAllowsCreation() throws Exception {
        var privilege = authService.createPrivilege("VEHICLE_CREATE");
        authService.createRole("Legacy Allocator Test", java.util.Set.of(privilege.getId()));
        authService.createUser("legacy-slot-test", "test-password", "Legacy Allocator Test", null);
        when(vehicleDetailsService.createVehicleDetails(any())).thenAnswer(call -> call.getArgument(0));

        String token = authService.login("legacy-slot-test", "test-password");
        assertEquals(201, post(VALID_PAYLOAD, token).statusCode());
        var session = JsonMapper.builder().build().readTree(get("/auth/me", token).body());
        assertTrue(session.at("/permissions/vehicles/create").asBoolean());
    }

    @Test
    void errorDispatchPreserves404ForAuthenticatedRequest() throws Exception {
        assertEquals(404, get("/nonexistent-test-route", authService.login("admin", "admin123")).statusCode());
    }

    @Test
    void invalidTokenReturnsReadable401() throws Exception {
        var response = post(VALID_PAYLOAD, "invalid-token");
        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("error"));
    }

    private HttpResponse<String> get(String path, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Authorization", "Bearer " + token).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String body, String token) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/vehicles"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) request.header("Authorization", "Bearer " + token);
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
