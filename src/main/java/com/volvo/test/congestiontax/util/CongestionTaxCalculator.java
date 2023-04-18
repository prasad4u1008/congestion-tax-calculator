package com.volvo.test.congestiontax.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.volvo.test.congestiontax.exception.InvalidVehicle;
import com.volvo.test.congestiontax.exception.NotSupportedTaxYear;
import com.volvo.test.congestiontax.exception.TaxConfigDataError;
import com.volvo.test.congestiontax.model.CityConfiguration;
import com.volvo.test.congestiontax.model.Vehicle;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * CongestionTaxCalculator is utility to calculate congestion tax
 */
@Component
public class CongestionTaxCalculator {

    private static final String BASE_PACKAGE = "com.volvo.test.congestiontax";
    private static final int YEAR_2013 = 2013;

    private static final Logger logger = LoggerFactory.getLogger(CongestionTaxCalculator.class);

  
    /**
     * This is the main calculation logic for congestion tax
     * @param vehicle input vehicle
     * @param dates array of Date entries
     * @param config configuration for the rule for each city
     * @return tax amount
     */
    public int getTax(Vehicle vehicle, Date[] dates, CityConfiguration config)
    {
        final int[] totalFee = {0};
        if (dates.length <= 0) {
            return totalFee[0];
        }

        final Date[] intervalStart = {dates[0]};

        Arrays.stream(dates).forEach(date -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (calendar.get(Calendar.YEAR) != YEAR_2013) {
                throw new NotSupportedTaxYear();
            }

            int nextFee = getTollFee(date, vehicle, config);
            int tempFee = getTollFee(intervalStart[0], vehicle, config);

            long diffInMillies = date.getTime() - intervalStart[0].getTime();
            long minutes = diffInMillies / 1000 / 60;

            if (minutes <= config.getSingleChargeInMinutes()) {
                if (totalFee[0] > 0) totalFee[0] -= tempFee;
                if (nextFee >= tempFee) tempFee = nextFee;
                totalFee[0] += tempFee;
            } else {
                intervalStart[0] = date;
                totalFee[0] += nextFee;
            }
        });
      
        if (totalFee[0] > config.getMaxAmountPerDay()){
            totalFee[0] = config.getMaxAmountPerDay();
        }
        return totalFee[0];
    }

    /**
     * To check if it's toll free vehicle
     * @param vehicle input vehicle
     * @param config city config
     * @return true if it's toll free vehicle, false otherwise
     */
    private boolean isTollFreeVehicle(Vehicle vehicle, CityConfiguration config) {
        if (vehicle == null) return false;
        String vehicleType = vehicle.getVehicleType();
        return config.getTollFreeVehicles().contains(vehicleType);
    }

    /**
     * To get vehicle from input string based on reflection scan thru the model package
     * @param vehicleString input vehicle
     * @return instance of the selected vehicle
     */
    public Vehicle getVehicle(String vehicleString) {
        try {
            return (Vehicle) (Class.forName(BASE_PACKAGE + ".model." + vehicleString).getDeclaredConstructor().newInstance());
        } catch (Exception ex) {
            throw new InvalidVehicle("Invalid vehicle");
        }
    }

    /**
     * To get toll fee for input date and vehicle and city config
     * @param date input date
     * @param vehicle input vehicle
     * @param config city config
     * @return current toll fee
     */
    public int getTollFee(Date date, Vehicle vehicle, CityConfiguration config)
    {
        if (isTollFreeDate(date, config) || isTollFreeVehicle(vehicle, config)) return 0;

        int hour = date.getHours();
        int minute = date.getMinutes();
        int second = date.getSeconds();

        final int[] tollFee = {0};
        config.getTollAmount().forEach((key,value) -> {
            try {
                String[] arr = key.split("@");
                int startHour = Integer.parseInt(arr[0].split("-")[0].split(":")[0]);
                int startMinute = Integer.parseInt(arr[0].split("-")[0].split(":")[1]);
                int endHour = Integer.parseInt(arr[0].split("-")[1].split(":")[0]);
                int endMinute = Integer.parseInt(arr[0].split("-")[1].split(":")[1]);
                logger.debug("Check: startHour: {}, startMinute: {}, endHour: {}, endMinute: {}", startHour, startMinute, endHour, endMinute);

                if (isTimeInBetween(hour,minute,second,startHour,startMinute,endHour,endMinute)) {
                    tollFee[0] = value;
                    logger.debug("Toll fee: {}", tollFee[0]);
                }
            } catch (NumberFormatException ex) {
                logger.error("Congestion Tax Config error for city: " + config.getCityName(), ex);
                throw new TaxConfigDataError("Congestion Tax Config error for city: " + config.getCityName());
            }
        });
        return tollFee[0];
    }

    /**
     * To check if the input time is fall in between start and end time
     * @param hour input hour
     * @param minute input minute
     * @param second input second
     * @param startHour start hour
     * @param startMinute start minute
     * @param endHour end hour
     * @param endMinute end minute
     * @return if the input time is fall in between start and end time, false otherwise
     */
    private boolean isTimeInBetween(int hour, int minute, int second, int startHour, int startMinute, int endHour, int endMinute) {
        int startSecond = 0;
        int endSecond = 59;
        if (startHour <= endHour) {
            // handle the entry fall in the configured timeslot
            return (startHour < hour || (startHour == hour && (startMinute < minute || (startMinute == minute && startSecond <= second)))) &&
                    (endHour > hour || (endHour == hour && (endMinute > minute || (endMinute == minute && endSecond >= second))));
        } else {
            // handle the timeslot between today evening to tomorrow morning
            int midNightEndHour = 23;
            int midNightEndMinute = 59;
            int midNightStartHour = 0;
            int midNightStartMinute = 0;
            return isTimeInBetween(hour, minute, second, startHour, startMinute, midNightEndHour, midNightEndMinute) ||
                    isTimeInBetween(hour, minute, second, midNightStartHour, midNightStartMinute, endHour, endMinute);
        }
    }

    /**
     * To check if it's toll free date
     * @param date input date
     * @param config city config
     * @return true if it's toll free date, false otherwise
     */
    private boolean isTollFreeDate(Date date, CityConfiguration config)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_WEEK) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // only support YR 2013
        if (year != YEAR_2013) {
            throw new NotSupportedTaxYear();
        }

        // handle toll free for Sat and Sun
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return true;
        }

        // handle toll free for free month
        if (config.getFreeMonths().contains(month)) {
            return true;
        }

        return config.getPublicHolidays().stream().map(entry -> {
            try {
                int phMonth = Integer.parseInt(entry.split("-")[0]);
                int phDay = Integer.parseInt(entry.split("-")[1]);

                Calendar phCalendar = Calendar.getInstance();
                phCalendar.set(Calendar.YEAR, year);
                phCalendar.set(Calendar.MONTH, phMonth - 1);
                phCalendar.set(Calendar.DAY_OF_MONTH, phDay);
                int phDayOfMonth = phCalendar.get(Calendar.DAY_OF_MONTH);

                // Calculate previous day
                Calendar previousDayPhCalendar = Calendar.getInstance();
                previousDayPhCalendar.setTime(phCalendar.getTime());
                previousDayPhCalendar.add(Calendar.DAY_OF_MONTH, -1);

                int previousPhMonth = previousDayPhCalendar.get(Calendar.MONTH) + 1;
                int previousPhDayOfMonth = previousDayPhCalendar.get(Calendar.DAY_OF_MONTH);
                if ((month == phMonth && dayOfMonth == phDayOfMonth)
                        || (month == previousPhMonth && dayOfMonth == previousPhDayOfMonth)) {
                    return true;
                }
            } catch (NumberFormatException ex) {
                logger.error("Congestion Tax Config error for city: " + config.getCityName(), ex);
                throw new TaxConfigDataError("Congestion Tax Config error for city: " + config.getCityName());
            }
            return false;
        }).filter(f -> f).findFirst().orElse(false);
    }
}





