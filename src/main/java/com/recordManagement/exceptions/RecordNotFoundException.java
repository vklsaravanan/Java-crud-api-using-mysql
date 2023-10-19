package com.recordManagement.exceptions;

public class RecordNotFoundException extends Exception {
	
	public RecordNotFoundException() {
		super("Record is not found in the database");
	}
	
	public RecordNotFoundException(String message) {
		super(message);
	}
}
