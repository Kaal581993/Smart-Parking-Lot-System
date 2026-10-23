package com.smart.parking.lot.smart.parking.lot.service;


import com.smart.parking.lot.smart.parking.lot.entity.ParkingTicket;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleDetails;
import com.smart.parking.lot.smart.parking.lot.repository.VehicleDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class VehicleDetailsService {
    @Autowired
    private VehicleDetailsRepository vehicleDetailsRepository;

    @Autowired
    private TicketService ticketService;

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
        return vehicleDetailsRepository.findById(vehicleDetailsId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("VehicleDetails not found: " + vehicleDetailsId));
    }

    public List<VehicleDetails> getVehiclesByOwnerAadhaar(String aadhaarNumber) {
        return vehicleDetailsRepository.findByOwnerAadhaarNumber(aadhaarNumber);
    }

    public ParkingTicket getActiveTicketByVehicle(String vehicleId) {
        return ticketService.getActiveTicketByVehicle(vehicleId);
    }

    public org.springframework.data.domain.Page<VehicleDetails> getVehicleDetailsList(org.springframework.data.domain.Pageable pageable) {
        return vehicleDetailsRepository.findAll(pageable);
    }

    public void deleteVehicleDetails(String vehicleDetailsId) {
        vehicleDetailsRepository.deleteById(vehicleDetailsId);
    }

    public VehicleDetails updateVehicleDetails(VehicleDetails vehicleDetails) {
        return vehicleDetailsRepository.save(vehicleDetails);
    }
}
