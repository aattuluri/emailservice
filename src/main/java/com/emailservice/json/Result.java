package com.emailservice.json;

/**
 * This is for HTTP result object for JSON marshalling and unmarshalling
 * 
 * @author Anil
 *
 */
public class Result {
	
	private boolean success;
	private String errorMessage;
	private Boolean serviceIsDown = false;
	
	public Result(boolean success, String errorMessage) {
		super();
		this.success = success;
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isServiceIsDown() {
		return serviceIsDown;
	}

	public void setServiceIsDown(Boolean serviceIsDown) {
		this.serviceIsDown = serviceIsDown;
	}
	
}
