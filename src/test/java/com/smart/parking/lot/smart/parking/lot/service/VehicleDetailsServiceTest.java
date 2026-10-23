package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.dto.BatchVehicleRequest;
import com.smart.parking.lot.smart.parking.lot.dto.BatchVehicleResponse;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleDetailsServiceTest {

    @Mock
    private VehicleDetailsRepository vehicleDetailsRepository;

    @InjectMocks
    private VehicleDetailsService vehicleDetailsService;

    @Test
    void batchCreateVehicles_reportsDuplicatesAndInvalid() {
        VehicleOwner owner = new VehicleOwner();
        VehicleDetails valid = new VehicleDetails();
        valid.setNumberPlate("MH04AB1234");
        valid.setOwner(owner);
        VehicleDetails duplicate = new VehicleDetails();
        duplicate.setNumberPlate("MH04AB9999");
        duplicate.setOwner(owner);
        VehicleDetails invalid = new VehicleDetails();
        invalid.setNumberPlate(null);
        invalid.setOwner(null);

        when(vehicleDetailsRepository.existsByNumberPlate("MH04AB1234")).thenReturn(false);
        when(vehicleDetailsRepository.existsByNumberPlate("MH04AB9999")).thenReturn(true);
        when(vehicleDetailsRepository.save(any())).thenReturn(valid);

        BatchVehicleResponse resp = vehicleDetailsService.batchCreateVehicles(
                new BatchVehicleRequest(List.of(valid, duplicate, invalid)));

        assertEquals(List.of("MH04AB1234"), resp.created());
        assertEquals(List.of("MH04AB9999"), resp.duplicate());
        assertEquals(1, resp.invalid().size());
    }
}
