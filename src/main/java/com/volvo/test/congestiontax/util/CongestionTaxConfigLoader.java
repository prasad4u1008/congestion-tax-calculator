package com.volvo.test.congestiontax.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.volvo.test.congestiontax.exception.TaxConfigDataError;
import com.volvo.test.congestiontax.exception.TaxConfigNotFound;
import com.volvo.test.congestiontax.model.CityConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public class CongestionTaxConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(CongestionTaxConfigLoader.class);

    /**
     * To load config from properties for the city
     * @param name city name should be the same with properties file
     * @return tax config object for the city
     */
    public CityConfiguration getConfig(String name) {
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile("classpath:" + name +".properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException ex) {
            logger.error("Congestion Tax Config not found for city: " + name, ex);
            throw new TaxConfigNotFound("Congestion Tax Config not found for city: " + name);
        }

        CityConfiguration cityTaxConfiguration = new CityConfiguration();
        try {
            cityTaxConfiguration.setCityName(name);
            cityTaxConfiguration.setMaxAmountPerDay(Integer.parseInt(properties.getProperty("max-amount-per-day")));
            cityTaxConfiguration.setSingleChargeInMinutes(Integer.parseInt(properties.getProperty("single-charge-rule-in-mins")));

            String listOfTollFreeVehicles = properties.getProperty("toll-free-vehicles");
            cityTaxConfiguration.setTollFreeVehicles(Arrays.stream(listOfTollFreeVehicles.split(","))
                    .collect(Collectors.toSet()));

            String listOfHourAndAmountCongestionTax = properties.getProperty("hour-and-amount-congestion-tax");
            for (String ha : listOfHourAndAmountCongestionTax.split(",")) {
                String[] arr = ha.split("@");
                cityTaxConfiguration.getTollAmount().put(arr[0], Integer.parseInt(arr[1]));
            }

            if (properties.getProperty("public-holidays") != null) {
                String listOfPublicHolidays = properties.getProperty("public-holidays");
                cityTaxConfiguration.setPublicHolidays(Arrays.stream(listOfPublicHolidays.split(","))
                        .collect(Collectors.toSet()));
            }

            if (properties.getProperty("free-months") != null) {
                String listOfFreeMonths = properties.getProperty("free-months");
                cityTaxConfiguration.setFreeMonths(Arrays.stream(listOfFreeMonths.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet()));
            }
        } catch (Exception ex) {
            logger.error("Congestion Tax Config error for city: " + name, ex);
            throw new TaxConfigDataError("Congestion Tax Config error for city: " + name);
        }

        return cityTaxConfiguration;
    }
}
