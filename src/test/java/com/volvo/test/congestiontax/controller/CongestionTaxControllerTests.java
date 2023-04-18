package com.volvo.test.congestiontax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.test.congestiontax.model.CongestionTaxRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CongestionTaxControllerTests {

    private static final String CITY = "Gothenburg";

    @Autowired
    private MockMvc mvc;

    @Test
    public void calculateEmptyTaxEntry() throws Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", new ArrayList<>());

        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));
    }

    @Test
    public void calculateSingleEntry() throws Exception {
        // Normal day - 0 charges
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2013-01-14 21:00:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));

        // Normal day - Chargable
        CongestionTaxRequest taxRequest2 = new CongestionTaxRequest("Car", Arrays.asList("2013-01-14 09:00:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(8));

        // Public holiday
        CongestionTaxRequest taxRequest3 = new CongestionTaxRequest("Car", Arrays.asList("2013-01-01 21:00:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));

        // Previous day of Public holiday
        CongestionTaxRequest taxRequest4 = new CongestionTaxRequest("Car", Arrays.asList("2013-03-28 09:00:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest4))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));

        // Tax exception vehicle
        CongestionTaxRequest taxRequest5 = new CongestionTaxRequest("Emergency", Arrays.asList("2013-06-27 10:00:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest5))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Emergency"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));

    }

    @Test
    public void calculateMultipleEntryNoMultipleTollingStations() throws Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2013-01-14 09:00:00", "2013-01-14 18:20:00"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(16));

        CongestionTaxRequest taxRequest2 = new CongestionTaxRequest("Car", Arrays.asList("2013-08-01 08:32:01", "2013-08-01 14:59:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(16));

        CongestionTaxRequest taxRequest3 = new CongestionTaxRequest("Car", Arrays.asList("2013-08-01 15:12:01", "2013-08-01 18:00:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(21));
    }

    @Test
    public void calculateMultipleEntryWithMultipleTollingStations() throws Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2013-01-14 06:01:01", "2013-01-14 06:58:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(13));
    }

    @Test
    public void calculateMultipleEntryMaxAmountAday() throws Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2013-01-14 06:01:01", "2013-01-14 06:58:01", "2013-01-14 07:58:01", "2013-01-14 08:05:01", "2013-01-14 15:05:01", "2013-01-14 15:58:01", "2013-01-14 17:58:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(60));
    }

    @Test
    public void calculateMultipleEntrySpecial() throws Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2013-06-06 12:01:01","2013-06-06 14:58:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vehicle").value("Car"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxAmount").value(0));
    }

    @Test
    public void invalidYearCheck() throws  Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2016-06-06 12:01:01","2013-06-06 14:58:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=" + CITY)
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    public void invalidTaxConfigCheck() throws  Exception {
        CongestionTaxRequest taxRequest = new CongestionTaxRequest("Car", Arrays.asList("2016-06-06 12:01:01","2013-06-06 14:58:01"));
        mvc.perform( MockMvcRequestBuilders
                .post("/api/computeTax?city=Curd")
                .content(new ObjectMapper().writeValueAsString(taxRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }
}
