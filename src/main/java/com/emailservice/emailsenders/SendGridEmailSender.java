package com.emailservice.emailsenders;

import org.apache.log4j.Logger;

import com.emailservice.json.Email;
import com.emailservice.json.Result;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * This class implements the email sending using Sendgrid REST API service
 * 
 * @author Anil
 *
 */
public class SendGridEmailSender extends AbstractEmailSender {
	
	private static Logger logger = Logger.getLogger(SendGridEmailSender.class);
	
	private static final String SENDGRID = "SendGrid";
	

	
	private String USERNAME;
	private String PASSWORD;
		
	public SendGridEmailSender(Email email, String username, String password) {
		super(email);
		this.USERNAME = username;
		this.PASSWORD = password;
	}
	
	@Override
	public Result sendEmail() {
		
		Result result = null;
		try {
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
			SendGrid.Response response = sendgrid.send(sgEmail);
			if (response.getStatus()) {
				logger.info(String.format(EMAIL_SENDING_SUCCESSFUL, SENDGRID));
				result = new Result(true, null);
			} else {
				logger.error(String.format(EMAIL_SENDING_FAILED, SENDGRID)
						+ response.getMessage());
				result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			}
		} catch (SendGridException e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, SENDGRID), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			result.setServiceIsDown(true);
		} catch (Exception e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, SENDGRID), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			result.setServiceIsDown(true);
		}
		
		return result;
	}

}
