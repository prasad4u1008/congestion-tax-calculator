package com.volvo.test.congestiontax.exception;

/**
 * Invalid Date Exception: to indicate the input date is invalid
 */
public class InvalidDate extends RuntimeException {
    private static final long serialVersionUID = 6939338492635928532L;

	public InvalidDate(String message) {
        super(message);
    }
}
