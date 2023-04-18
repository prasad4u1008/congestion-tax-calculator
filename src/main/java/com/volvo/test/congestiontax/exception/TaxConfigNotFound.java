package com.volvo.test.congestiontax.exception;

/**
 * Tax Config Not Found Exception: to indicate the city configuration file not found
 */
public class TaxConfigNotFound extends RuntimeException {
    private static final long serialVersionUID = -6010987317892150966L;

	public TaxConfigNotFound(String message) {
        super(message);
    }
}
