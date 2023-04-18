package com.volvo.test.congestiontax.exception;

/**
 * Invalid Vehicle Exception: to indicate the input vehicle is invalid
 */
public class InvalidVehicle extends RuntimeException {
    private static final long serialVersionUID = 8154269026338636154L;

	public InvalidVehicle(String message) {
        super(message);
    }
}
