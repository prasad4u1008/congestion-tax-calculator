package com.volvo.test.congestiontax.model;

/**
 * Military vehicle model
 */
public class Military implements Vehicle {
    @Override
    public String getVehicleType() {
        return "Military";
    }
}
