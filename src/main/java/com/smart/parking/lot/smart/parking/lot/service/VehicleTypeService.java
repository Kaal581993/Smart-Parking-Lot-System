package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleTypeService {

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public VehicleType createVehicleType(VehicleType vehicleType) {
        return vehicleTypeRepository.save(vehicleType);
    }

    public VehicleType getVehicleTypeById(String vehicleTypeId) {
        return vehicleTypeRepository.findById(Long.valueOf(vehicleTypeId)).orElse(null);
    }

    public List<VehicleType> getVehicleTypeList() {
        return vehicleTypeRepository.findAll();
    }

    public void deleteVehicleType(String vehicleTypeId) {
        vehicleTypeRepository.deleteById(Long.valueOf(vehicleTypeId));
    }

    public VehicleType updateVehicleType(VehicleType vehicleType) {
        return vehicleTypeRepository.save(vehicleType);
    }

    public VehicleType updateVehicleTypeById(String vehicleTypeId, VehicleType vehicleType) {
        VehicleType existingVehicleType = getVehicleTypeById(vehicleTypeId);
        existingVehicleType.setDailyRate(vehicleType.getDailyRate());
        existingVehicleType.setHourlyRate(vehicleType.getHourlyRate());
        existingVehicleType.setTypeName(vehicleType.getTypeName());
        return vehicleTypeRepository.save(existingVehicleType);
    }
}
