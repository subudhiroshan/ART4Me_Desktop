package com.sms;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class handles any exceptions that occur during the creation of a serial connection.
 */
public class SerialConnectionException extends Exception {
	private static final long serialVersionUID = 1L;

	public SerialConnectionException(String str) {
		super(str);
	}

	public SerialConnectionException() {
		super();
	}
}
