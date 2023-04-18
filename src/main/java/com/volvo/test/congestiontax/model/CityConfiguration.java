package com.volvo.test.congestiontax.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * City Tax Configuration file: to keeps the configuration for later usage
 */
public class CityConfiguration {
    private String cityName;
    private int maxAmountPerDay;
    private int singleChargeInMinutes;
    private Set<String> tollFreeVehicles = new HashSet<>();
    private Map<String, Integer> tollAmount = new HashMap<>();
    private Set<String> publicHolidays = new HashSet<>();
    private Set<Integer> freeMonths = new HashSet<>();

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getMaxAmountPerDay() {
        return maxAmountPerDay;
    }

    public void setMaxAmountPerDay(int maxAmountPerDay) {
        this.maxAmountPerDay = maxAmountPerDay;
    }

    public int getSingleChargeInMinutes() {
        return singleChargeInMinutes;
    }

    public void setSingleChargeInMinutes(int singleChargeInMinutes) {
        this.singleChargeInMinutes = singleChargeInMinutes;
    }

    public Set<String> getTollFreeVehicles() {
        return tollFreeVehicles;
    }

    public void setTollFreeVehicles(Set<String> tollFreeVehicles) {
        this.tollFreeVehicles = tollFreeVehicles;
    }

    public Map<String, Integer> getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(Map<String, Integer> tollAmount) {
        this.tollAmount = tollAmount;
    }

    public Set<String> getPublicHolidays() {
        return publicHolidays;
    }

    public void setPublicHolidays(Set<String> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    public Set<Integer> getFreeMonths() {
        return freeMonths;
    }

    public void setFreeMonths(Set<Integer> freeMonths) {
        this.freeMonths = freeMonths;
    }
}
