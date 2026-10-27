package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AadharRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AadhaarManagementService {


    @Autowired
    private AadharRepository aadharRepository;
    @Autowired
    private AddressRepository addressRepository;

    public OwnerAadhaar createOwnerAadhaar(OwnerAadhaar ownerAadhaar) {
        resolveAddress(ownerAadhaar);
        aadharRepository.save(ownerAadhaar);
        return ownerAadhaar;
    }

    public List<OwnerAadhaar> getOwnerAadhaarList() {
        return aadharRepository.findAll();
    }

    public OwnerAadhaar getOwnerAadhaarID(String ownerAadhaarId) {
        return aadharRepository.findById(ownerAadhaarId).orElse(null);
//        ownerAadhaarList.stream()
//                .filter(ownerAadhaar -> ownerAadhaar.getAadharNo().equals(ownerAadhaarId))
//                .findFirst()
//                .orElse(null);
    }

    public void deleteOwnerAadhaar(String ownerAadhaarId) {
//        ownerAadhaarList.removeIf(ownerAadhaar -> ownerAadhaar.getAadharNo().equals(ownerAadhaarId));
        aadharRepository.deleteById(ownerAadhaarId);
    }

    public OwnerAadhaar updateOwnerAadhaar(OwnerAadhaar ownerAadhaar) {
        resolveAddress(ownerAadhaar);
        return aadharRepository.save(ownerAadhaar);
    }

    private void resolveAddress(OwnerAadhaar ownerAadhaar) {
        String addressId = ownerAadhaar.getAddressId();
        if (addressId == null && ownerAadhaar.getAddress() != null) addressId = ownerAadhaar.getAddress().getAddressId();
        if (addressId == null || addressId.isBlank()) {
            throw new IllegalArgumentException("addressId is required");
        }

        String resolvedAddressId = addressId;
        Address address = addressRepository.findById(resolvedAddressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found: " + resolvedAddressId));

        // addressId is a transient request/response field. Keep it in sync with
        // the managed relationship so both supported request shapes work:
        // {"addressId":"..."} and {"address":{"addressId":"..."}}.
        ownerAadhaar.setAddressId(resolvedAddressId);
        ownerAadhaar.setAddress(address);
    }
}
