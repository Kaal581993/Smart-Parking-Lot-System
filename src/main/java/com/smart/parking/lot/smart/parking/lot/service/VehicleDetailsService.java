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
        return vehicleDetailsRepository.save(vehicleDetails);
    }

    public VehicleDetails getVehicleDetailsById(String vehicleDetailsId) {
        return vehicleDetailsRepository.findById(Long.valueOf(vehicleDetailsId)).orElse(null);
    }

    public List<VehicleDetails> getVehicleDetailsList() {
        return vehicleDetailsRepository.findAll();
    }

    public void deleteVehicleDetails(String vehicleDetailsId) {
        vehicleDetailsRepository.deleteById(Long.valueOf(vehicleDetailsId));
    }

    public VehicleDetails updateVehicleDetails(VehicleDetails vehicleDetails) {
        return vehicleDetailsRepository.save(vehicleDetails);
    }
}
