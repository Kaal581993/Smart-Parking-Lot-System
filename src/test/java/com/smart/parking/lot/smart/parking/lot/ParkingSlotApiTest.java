package com.smart.parking.lot.smart.parking.lot;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.repository.ParkingSlotRepository;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleTypeRepository;
import com.smart.parking.lot.smart.parking.lot.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ParkingSlotApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    private static final String PAYLOAD = """
            {"slotNumber":"P001","floorNo":1,"floorLimit":55,"floorSlotCapacity":200,
             "costPerHour":30,"allocationDuration":"PT10H","available":true,"active":true,
             "vehicleTypeAllowed":{"typeId":"VT1433"}}
            """;

    @Test
    void availabilityFlagsUsePublicPropertyNamesAndRetainDefaultsWhenOmitted() throws Exception {
        ParkingSlot disabled = jsonMapper.readValue(PAYLOAD
                .replace("\"available\":true", "\"available\":false")
                .replace("\"active\":true", "\"active\":false"), ParkingSlot.class);
        assertFalse(disabled.isAvailable());
        assertFalse(disabled.isActive());

        ParkingSlot defaults = jsonMapper.readValue(PAYLOAD
                .replace("\"available\":true,\"active\":true,", ""), ParkingSlot.class);
        assertTrue(defaults.isAvailable());
        assertTrue(defaults.isActive());
        assertFalse(defaults.isDeleted());
    }

    @Test
    void createSlotAcceptsIsoDurationAndVehicleTypeReference() throws Exception {
        // Exercise the actual application mapper so binding failures identify
        // their cause instead of only exposing the API's generic 400 message.
        ParkingSlot parsed = jsonMapper.readValue(PAYLOAD, ParkingSlot.class);
        assertEquals(Duration.ofHours(10), parsed.getAllocationDuration());
        assertEquals("VT1433", parsed.getVehicleTypeAllowed().getTypeId());

        vehicleTypeRepository.save(VehicleType.builder()
                .typeId("VT1433").typeName("Test Sedan").hourlyRate(30).dailyRate(150).build());
        String token = authService.login("admin", "admin123");
        try {
            var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/parking_slots"))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(PAYLOAD)).build();
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(201, response.statusCode(), response.body());
            var result = jsonMapper.readTree(response.body());
            String slotId = result.at("/slotId").asString();
            assertFalse(slotId.isBlank());
            ParkingSlot saved = parkingSlotRepository.findById(slotId).orElseThrow();
            assertNotNull(saved.getVersion());
            assertEquals("P001", saved.getSlotNumber());
            assertEquals(1, saved.getFloorNo());
            assertEquals(55, saved.getFloorLimit());
            assertEquals(200, saved.getFloorSlotCapacity());
            assertEquals(30.0, saved.getCostPerHour());
            assertEquals(Duration.ofHours(10), saved.getAllocationDuration());
            assertEquals("VT1433", saved.getVehicleTypeAllowed().getTypeId());
            assertEquals("Test Sedan", saved.getVehicleTypeAllowed().getTypeName());
            assertTrue(saved.isAvailable());
            assertTrue(saved.isActive());
        } finally {
            authService.logout(token);
        }
    }
}
