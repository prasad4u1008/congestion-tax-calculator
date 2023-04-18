package com.volvo.test.congestiontax.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.volvo.test.congestiontax.exception.InvalidVehicle;
import com.volvo.test.congestiontax.model.Car;
import com.volvo.test.congestiontax.model.CityConfiguration;
import com.volvo.test.congestiontax.model.Emergency;
import com.volvo.test.congestiontax.model.Vehicle;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class CongestionTaxCalTests {

    private static final String CITY = "Gothenburg";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testGetVehicleSuccess() {
        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        Vehicle vehicle1 = congestionTaxCalculator.getVehicle("Car");
        assertEquals(vehicle1.getVehicleType(), "Car");

        Vehicle vehicle2 = congestionTaxCalculator.getVehicle("Motorcycles");
        assertEquals(vehicle2.getVehicleType(), "Motorcycles");

        Vehicle vehicle3 = congestionTaxCalculator.getVehicle("Bus");
        assertEquals(vehicle3.getVehicleType(), "Bus");

        Vehicle vehicle4 = congestionTaxCalculator.getVehicle("Foreign");
        assertEquals(vehicle4.getVehicleType(), "Foreign");

        Vehicle vehicle5 = congestionTaxCalculator.getVehicle("Diplomat");
        assertEquals(vehicle5.getVehicleType(), "Diplomat");

        Vehicle vehicle6 = congestionTaxCalculator.getVehicle("Emergency");
        assertEquals(vehicle6.getVehicleType(), "Emergency");

        Vehicle vehicle7 = congestionTaxCalculator.getVehicle("Military");
        assertEquals(vehicle7.getVehicleType(), "Military");
    }

    @Test
    public void testGetVehicleReturnInvalidVehicle() {
        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        assertThrows(InvalidVehicle.class, () -> congestionTaxCalculator.getVehicle("NoName"));
    }

    @Test
    public void calculateEmptyTaxEntry() throws Exception {
        CongestionTaxConfigLoader congestionTaxConfigLoader = new CongestionTaxConfigLoader();
        CityConfiguration config = congestionTaxConfigLoader.getConfig(CITY);

        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        int taxAmount = congestionTaxCalculator.getTax(new Car(), new Date[]{}, config);
        assertEquals(taxAmount, 0);
    }

    @Test
    public void calculateSingleEntry() throws Exception {
        // Normal day - 0 charges
        CongestionTaxConfigLoader congestionTaxConfigLoader = new CongestionTaxConfigLoader();
        CityConfiguration config = congestionTaxConfigLoader.getConfig(CITY);

        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        int taxAmount1 = congestionTaxCalculator.getTax(new Car(), new Date[]{simpleDateFormat.parse("2013-01-14 21:00:01")}, config);
        assertEquals(taxAmount1, 0);

        // Normal day - Chargable
        int taxAmount2 = congestionTaxCalculator.getTax(new Car(), new Date[]{simpleDateFormat.parse("2013-01-14 09:00:01")}, config);
        assertEquals(taxAmount2, 8);

        // Public holiday
        int taxAmount3 = congestionTaxCalculator.getTax(new Emergency(), new Date[]{simpleDateFormat.parse("2013-01-01 21:00:01")}, config);
        assertEquals(taxAmount3, 0);

        // Previous day of Public holiday
        int taxAmount4 = congestionTaxCalculator.getTax(new Emergency(), new Date[]{simpleDateFormat.parse("2013-03-28 09:00:01")}, config);
        assertEquals(taxAmount4, 0);

        // Tax exception vehicle
        int taxAmount5 = congestionTaxCalculator.getTax(new Emergency(), new Date[]{simpleDateFormat.parse("2013-01-14 09:00:01")}, config);
        assertEquals(taxAmount5, 0);

    }

    @Test
    public void calculateMultipleEntryNoMultipleTollingStations() throws Exception {
        CongestionTaxConfigLoader congestionTaxConfigLoader = new CongestionTaxConfigLoader();
        CityConfiguration config = congestionTaxConfigLoader.getConfig(CITY);

        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        int taxAmount = congestionTaxCalculator.getTax(new Car(), new Date[]{simpleDateFormat.parse("2013-01-14 21:10:01"), simpleDateFormat.parse("2013-01-14 18:20:01")}, config);
        assertEquals(taxAmount, 8);
    }

    @Test
    public void calculateMultipleEntryWithMultipleTollingStations() throws Exception {
        CongestionTaxConfigLoader congestionTaxConfigLoader = new CongestionTaxConfigLoader();
        CityConfiguration config = congestionTaxConfigLoader.getConfig(CITY);

        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        int taxAmount = congestionTaxCalculator.getTax(new Car(), new Date[]{simpleDateFormat.parse("2013-01-14 06:01:01"), simpleDateFormat.parse("2013-01-14 06:58:01")}, config);
        assertEquals(taxAmount, 13);
    }

    @Test
    public void calculateMultipleEntryMaxAmountAday() throws Exception {
        CongestionTaxConfigLoader congestionTaxConfigLoader = new CongestionTaxConfigLoader();
        CityConfiguration config = congestionTaxConfigLoader.getConfig(CITY);

        CongestionTaxCalculator congestionTaxCalculator = new CongestionTaxCalculator();
        int taxAmount = congestionTaxCalculator.getTax(new Car(), new Date[]{simpleDateFormat.parse("2013-01-14 06:01:01"), simpleDateFormat.parse("2013-01-14 07:01:01"), simpleDateFormat.parse("2013-01-14 18:58:59")
                , simpleDateFormat.parse("2013-01-14 07:58:01"), simpleDateFormat.parse("2013-01-14 08:05:01"), simpleDateFormat.parse("2013-01-14 15:05:01")
                , simpleDateFormat.parse("2013-01-14 15:58:01"), simpleDateFormat.parse("2013-01-14 17:58:01")}, config);
        assertEquals(taxAmount, 60);
    }

}
