package com.emailservice.emailsenders;

import java.util.List;

import com.emailservice.json.Email;
import com.emailservice.json.Result;

/**
 * This is an abstract class (with default methods and common attributes) and any Email Senders should extend this class
 * 
 * @author Anil
 *
 */
public abstract class AbstractEmailSender {
	
	protected static final String THIRDPARTY_SERVICE_FAILURE = "Thirdparty service failure.";
	protected static final String EMAIL_SENDING_SUCCESSFUL = "Email sent successfully via %s.";
	protected static final String EMAIL_SENDING_FAILED = "Failed to send Email via %s. Error: ";
	
	private final String DEFAULT_FROM_EMAIL_ADDRESS = "anilcs0405@gmail.com";
	
	protected Email email;
	
	public AbstractEmailSender (Email email) {
		this.email = email;
		//check and set the FROM email address
		String fromEmail = this.email.getFrom();
		if (fromEmail == null
				|| fromEmail.isEmpty()) {
			this.email.setFrom(DEFAULT_FROM_EMAIL_ADDRESS);
		}
	}
	
	public abstract Result sendEmail ();

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}
	
	//utility method to convert string list to string array
	public String [] toArrays(List<String> strList) {
		if (strList == null
				|| strList.isEmpty()) {
			return null;
		}
		String [] arr = new String [strList.size()];	
		return strList.toArray(arr);
	}
	
	
}
