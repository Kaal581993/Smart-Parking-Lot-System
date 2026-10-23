package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.VehicleOwner;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.entity.owner.LicenseNo;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleOwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleOwnerServiceTest {

    @Mock
    private VehicleOwnerRepository vehicleOwnerRepository;

    @InjectMocks
    private VehicleOwnerService vehicleOwnerService;

    @Test
    void createVehicleOwner_throwsOnDuplicateAadhaar() {
        VehicleOwner owner = new VehicleOwner();
        OwnerAadhaar aadhaar = new OwnerAadhaar();
        aadhaar.setAadhaarNo("1234");
        owner.setAadhaar(aadhaar);
        LicenseNo license = new LicenseNo();
        license.setLicenseId("L1");
        owner.setLicenseNo(license);
        when(vehicleOwnerRepository.existsByAadhaarNumber("1234")).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> vehicleOwnerService.createVehicleOwner(owner));
    }
}
