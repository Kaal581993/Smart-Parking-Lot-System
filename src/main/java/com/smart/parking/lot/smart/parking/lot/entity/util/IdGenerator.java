package com.smart.parking.lot.smart.parking.lot.entity.util;

import java.util.Random;

public enum IdGenerator {
    INSTANCE;

    private final Random random = new Random();

    /**
     * Generates an ID with the specified prefix and number of digits.
     * Example: generateId("VN", 8) -> VN12345678
     *
     * @param prefix the prefix for the ID
     * @param digits the number of digits after the prefix
     * @return generated ID string
     */
    public String generateId(String prefix, int digits) {
        if (digits <= 0) {
            throw new IllegalArgumentException("Digits must be greater than 0");
        }
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits) - 1;
        int randomNumber = random.nextInt(max - min + 1) + min;
        return prefix + randomNumber;
    }

    /**
     * Generates a vehicle ID with prefix "VN" and 8 digits.
     * Example: VN12345678
     *
     * @return generated vehicle ID
     */
    public String generateVehicleId() {
        return generateId("VN", 8);
    }

    /**
     * Generates a parking slot ID with prefix "PS" and 4 digits.
     * Example: PS1234
     *
     * @return generated parking slot ID
     */
    public String generateParkingSlotId() {
        return generateId("PS", 4);
    }

    /**
     * Generates a booking ID with prefix "BK" and 10 digits.
     * Example: BK1234567890
     *
     * @return generated booking ID
     */
    public String generateBookingId() {
        return generateId("BK", 10);
    }

    /**
     * Generates a custom ID with specified prefix and default 8 digits.
     *
     * @param prefix the prefix for the ID
     * @return generated ID string
     */
    public String generateId(String prefix) {
        return generateId(prefix, 8);
    }
}
