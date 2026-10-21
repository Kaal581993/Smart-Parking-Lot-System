package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.owner.OwnerAadhaar;
import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AadharRepository;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AddressRepository;
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
        if (ownerAadhaar.getAddress() == null && ownerAadhaar.getAddressId() != null) {
            addressRepository.findById(ownerAadhaar.getAddressId()).ifPresent(ownerAadhaar::setAddress);
        }
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
}
