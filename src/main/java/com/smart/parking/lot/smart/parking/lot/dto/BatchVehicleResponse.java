package com.smart.parking.lot.smart.parking.lot.dto;

import java.util.List;

public record BatchVehicleResponse(
        List<String> created,
        List<String> duplicate,
        List<String> invalid
) {}
