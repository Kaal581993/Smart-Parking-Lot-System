package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleDetailsService {
    @Autowired
    private VehicleDetailsRepository vehicleDetailsRepository;

    public VehicleDetails createVehicleDetails(VehicleDetails vehicleDetails) {
        if (vehicleDetails.getNumberPlate() != null
                && vehicleDetailsRepository.existsByNumberPlate(vehicleDetails.getNumberPlate())) {
            throw new IllegalStateException("Number plate already registered to another vehicle");
        }
        if (vehicleDetails.getOwner() == null) {
            throw new IllegalArgumentException("Vehicle must be tied to an owner");
        }
        return vehicleDetailsRepository.save(vehicleDetails);
    }

    public VehicleDetails getVehicleDetailsById(String vehicleDetailsId) {
        return vehicleDetailsRepository.findById(vehicleDetailsId).orElse(null);
    }

    public List<VehicleDetails> getVehicleDetailsList() {
        return vehicleDetailsRepository.findAll();
    }

    public void deleteVehicleDetails(String vehicleDetailsId) {
        vehicleDetailsRepository.deleteById(vehicleDetailsId);
    }

    public VehicleDetails updateVehicleDetails(VehicleDetails vehicleDetails) {
        return vehicleDetailsRepository.save(vehicleDetails);
    }
}
