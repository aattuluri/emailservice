package com.emailservice.emailsenders;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.emailservice.json.Email;
import com.emailservice.json.Result;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

public class SendGridEmailSender extends AbstractEmailSender {
	
	private static Logger logger = Logger.getLogger(SendGridEmailSender.class);
	
	private static final String SENDGRID = "SendGrid";
		
	public SendGridEmailSender(Email email) {
		super(email);
	}
	
	private final String USERNAME = "anilcs0405";
	private final String PASSWORD = "gaBazab9";
	
	@Override
	public Result sendEmail() {
		
		Result result = null;
		SendGrid sendgrid = new SendGrid(USERNAME, PASSWORD);
		SendGrid.Email sgEmail = new SendGrid.Email();
		
		sgEmail.setFrom(email.getFrom());
		sgEmail.addTo(toArrays(email.getToList()));
		
		String [] cc = toArrays(email.getCcList());
		if (cc != null) {
			sgEmail.addCc(cc);
		}
		
		String [] bcc = toArrays(email.getBccList());
		if (bcc != null) {
			sgEmail.addBcc(bcc);
		}
		
		sgEmail.setSubject(email.getSubject());
		sgEmail.setText(email.getMessage());
		
		try {
			SendGrid.Response response = sendgrid.send(sgEmail);
			if (response.getStatus()) {
				logger.info(String.format(EMAIL_SENDING_SUCCESSFUL, SENDGRID));
				result = new Result(true, null);
			} else {
				logger.error(String.format(EMAIL_SENDING_FAILED, SENDGRID)
						+ response.getMessage());
				result = new Result(true, THIRDPARTY_SERVICE_FAILURE);
			}
		} catch (SendGridException e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, SENDGRID), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
		}
		
		return result;
	}	
	
	public static void main (String [] args) {
		Email email = new Email ();
		List<String> toList = new ArrayList<String>();
		toList.add("anilcs0405@gmail.com");
		email.setToList(toList);
		email.setFrom("anilcs0405@gmail.com");
		email.setSubject("Empty subject");
		email.setMessage("Hi,\n\nEmpty content\n\n\nThank you,\nEmail Service");
		AbstractEmailSender emailSender = new SendGridEmailSender(email);
		Result result = emailSender.sendEmail();
		if (result.isSuccess()) {
			System.out.println("Success");
		} else {
			System.out.println("Failed: " + result.getErrorMessage());
		}
	}

}
