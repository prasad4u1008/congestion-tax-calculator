package com.volvo.test.congestiontax.model;

/**
 * Foreigner vehicle model
 */
public class Foreign implements Vehicle {
    @Override
    public String getVehicleType() {
        return "Foreign";
    }
}
