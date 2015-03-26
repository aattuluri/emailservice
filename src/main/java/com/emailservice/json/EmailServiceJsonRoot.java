package com.emailservice.json;

/**
 * The root object that holds other objects for JSON marshalling and unmarshalling
 * 
 * @author Anil
 *
 */
public class EmailServiceJsonRoot {
	
	private Email email;
	private Result result;
	
	public Email getEmail() {
		return email;
	}
	public void setEmail(Email email) {
		this.email = email;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	
}
