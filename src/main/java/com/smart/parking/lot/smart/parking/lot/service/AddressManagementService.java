package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.owner.Address;
import com.smart.parking.lot.smart.parking.lot.repository.owner.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AddressManagementService {


    List<Address> addressList = new ArrayList<>();

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(Address address) {

        addressRepository.save(address);
        addressList.add(address);
        return address;
    }

    public Address getAddress(String addressId) {
        return addressRepository.findById(addressId).orElse(null);
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public void deleteAddress(String addressId) {
        addressRepository.deleteById(addressId);
    }

    public Address updateAddress(Address address) {
        return addressRepository.save(address);
    }
}
