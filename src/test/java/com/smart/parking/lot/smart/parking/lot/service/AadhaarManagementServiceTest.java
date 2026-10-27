package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AadharRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AadhaarManagementServiceTest {

    @Mock
    private AadharRepository aadharRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AadhaarManagementService aadhaarManagementService;

    @Test
    void createOwnerAadhaarResolvesNestedAddressAndSynchronizesAddressId() {
        Address requestAddress = new Address();
        requestAddress.setAddressId("ADDR-1");
        Address managedAddress = new Address();
        managedAddress.setAddressId("ADDR-1");

        OwnerAadhaar ownerAadhaar = new OwnerAadhaar();
        ownerAadhaar.setAddress(requestAddress);

        when(addressRepository.findById("ADDR-1")).thenReturn(Optional.of(managedAddress));
        when(aadharRepository.save(ownerAadhaar)).thenReturn(ownerAadhaar);

        aadhaarManagementService.createOwnerAadhaar(ownerAadhaar);

        assertEquals("ADDR-1", ownerAadhaar.getAddressId());
        assertSame(managedAddress, ownerAadhaar.getAddress());
        verify(aadharRepository).save(ownerAadhaar);
    }

    @Test
    void createOwnerAadhaarRejectsUnknownAddressBeforeSave() {
        OwnerAadhaar ownerAadhaar = new OwnerAadhaar();
        ownerAadhaar.setAddressId("MISSING");
        when(addressRepository.findById("MISSING")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> aadhaarManagementService.createOwnerAadhaar(ownerAadhaar));
        verifyNoInteractions(aadharRepository);
    }
}
