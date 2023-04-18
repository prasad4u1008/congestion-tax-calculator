package com.volvo.test.congestiontax.model;

/**
 * Emergency vehicle model
 */
public class Emergency implements Vehicle {

    @Override
    public String getVehicleType() {
        return "Emergency";
    }
}
