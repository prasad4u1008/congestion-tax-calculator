package com.volvo.test.congestiontax.model;

/**
 * Tax Response: tax response model that return from rest api
 */
public class CongentionTaxResponse {
    private String vehicle;
    private int taxAmount;

    public CongentionTaxResponse(String vehicle, int taxAmount) {
        this.vehicle = vehicle;
        this.taxAmount = taxAmount;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }
}
