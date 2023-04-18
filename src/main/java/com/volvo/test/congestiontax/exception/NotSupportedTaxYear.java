package com.volvo.test.congestiontax.exception;

/**
 * Invalid Tax Year Exception: year not supported , restricted to 2013 for now
 */
public class NotSupportedTaxYear extends RuntimeException {
    

	private static final long serialVersionUID = 4350275746408578266L;

	public NotSupportedTaxYear(String message) {
        super(message);
    }

    public NotSupportedTaxYear() {
        this("Sorry, only tax year 2013 is supported.");
    }
}
