package com.volvo.test.congestiontax.exception;

/**
 * Tax Config Data Error Exception: to indicate the city configuration file contains erroneous data
 */
public class TaxConfigDataError extends RuntimeException {
    private static final long serialVersionUID = -6320426009713195227L;

	public TaxConfigDataError(String message) {
        super(message);
    }
}
