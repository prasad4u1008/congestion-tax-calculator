package com.volvo.test.congestiontax.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.volvo.test.congestiontax.model.CongentionTaxResponse;
import com.volvo.test.congestiontax.model.CongestionTaxRequest;
import com.volvo.test.congestiontax.service.CongestionTaxService;

/**
 * CongestionTaxController : Api end point for Congestion Tax
 */
@RestController
@CrossOrigin ("*")
public class CongestionTaxController {

    private static final Logger logger = LoggerFactory.getLogger(CongestionTaxController.class);

    @Autowired
    CongestionTaxService congestionTaxService;

    /**
     * Post /api/computeTax
     * @param taxRequest taxRequest
     * @param city as config city name
     * @return taxResponse
     */
    @PostMapping(value = "/api/computeTax", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CongentionTaxResponse> computeTax(@RequestBody CongestionTaxRequest taxRequest, @RequestParam String city) {
        long start = new Date().getTime();
        logger.debug("Request started at: {}", start);
        CongentionTaxResponse result = congestionTaxService.computeTax(taxRequest, city);
        long end = new Date().getTime();
        logger.debug("Request ended at: {}", end);
        logger.info("Processing time {} ms", end - start);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
   
    /**
     * GET Method with default Welcome Page
     * @return String
     * @throws IOException 
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> welcome() throws IOException {
        long start = new Date().getTime();
        StringBuilder welcomeNote = new StringBuilder();
        welcomeNote.append("Welcome to Volvo Congenstion Tax Computation : " +new Date(start));
        welcomeNote.append("\n");
        welcomeNote.append("\n");
        welcomeNote.append("\n");
        welcomeNote.append(new String(this.getClass().getClassLoader().getResourceAsStream("readMe.txt").readAllBytes()));
        return new ResponseEntity<>(welcomeNote.toString(), HttpStatus.OK);
    }
    
//    private String readFile(String fileName) {
//    	String content = "";
//    	try {
//    	      File file = new File(fileName);
//    	      Scanner scanner = new Scanner(file);
//    	      while (scanner.hasNextLine()) {
//    	        String line = scanner.nextLine();
//    	        content += line;
//    	      }
//    	      scanner.close();
//    	    } catch (FileNotFoundException e) {
//    	      e.printStackTrace();
//    	    }
//    	return content;
//    }
}
