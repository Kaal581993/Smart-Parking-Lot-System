package com.smart.parking.lot.smart.parking.lot.service;

import com.smart.parking.lot.smart.parking.lot.entity.ParkingSlot;
import com.smart.parking.lot.smart.parking.lot.entity.VehicleType;
import com.smart.parking.lot.smart.parking.lot.strategy.slot.BestFitByTypeStrategy;
import com.smart.parking.lot.smart.parking.lot.strategy.slot.NearestSlotStrategy;
import com.smart.parking.lot.smart.parking.lot.strategy.slot.SlotAllocationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SlotAllocationService {

    private final Map<String, SlotAllocationStrategy> strategies = new ConcurrentHashMap<>();

    @Autowired
    private SlotService slotService;

    public SlotAllocationService() {
        strategies.put("NEAREST", new NearestSlotStrategy());
        strategies.put("BEST_FIT", new BestFitByTypeStrategy());
    }

    public ParkingSlot allocate(VehicleType vehicleType, String strategyName) {
        SlotAllocationStrategy strategy = strategies.getOrDefault(
                strategyName == null ? "BEST_FIT" : strategyName.toUpperCase(),
                new NearestSlotStrategy());
        List<ParkingSlot> available = slotService.getAvailableSlots();
        return strategy.allocate(vehicleType, available);
    }
}