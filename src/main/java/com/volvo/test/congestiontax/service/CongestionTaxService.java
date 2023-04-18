package com.volvo.test.congestiontax.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volvo.test.congestiontax.model.CityConfiguration;
import com.volvo.test.congestiontax.model.CongestionTaxRequest;
import com.volvo.test.congestiontax.model.CongentionTaxResponse;
import com.volvo.test.congestiontax.model.Vehicle;
import com.volvo.test.congestiontax.util.CongestionTaxCalculator;
import com.volvo.test.congestiontax.util.CongestionTaxConfigLoader;

/**
 * CongestionTaxService: service to handle tax calculation from REST API
 */
@Service
public class CongestionTaxService {

    @Autowired
    CongestionTaxConfigLoader congestionTaxConfigLoader;

    @Autowired
    CongestionTaxCalculator congestionTaxCalculator;

    /**
     * Handle tax computation
     * @param taxRequest taxRequest from api
     * @param name city name
     * @return taxResponse
     */
    public CongentionTaxResponse computeTax(CongestionTaxRequest request, String config)  {
        CityConfiguration cityTaxConfiguration = congestionTaxConfigLoader.getConfig(config);
        Vehicle vehicle = congestionTaxCalculator.getVehicle(request.getVehicle());
        return new CongentionTaxResponse(request.getVehicle(), congestionTaxCalculator.getTax(vehicle, request.getDateEntries() , cityTaxConfiguration));
    }
}
