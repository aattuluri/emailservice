package com.emailservice.emailsenders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.emailservice.json.Email;
import com.emailservice.json.Result;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient.Type;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;

public class MandrillEmailSender extends AbstractEmailSender {
	
	private static Logger logger = Logger.getLogger(MandrillEmailSender.class);
	
	private static final String MANDRILL = "Mandrill";
		
	public MandrillEmailSender(Email email) {
		super(email);
	}
	
	private final String APIKEY = "8QewPGciRLYLELgSP6jPXg";
	
	@Override
	public Result sendEmail() {
		
		Result result = null;
		MandrillApi mandrillApi = new MandrillApi(APIKEY);

		// create your message
		MandrillMessage message = new MandrillMessage();
		
		message.setFromEmail(email.getFrom());
		
		// add recipients
		ArrayList<Recipient> recipients = new ArrayList<Recipient>();
		List<String> toList = email.getToList();
		addEmailRecipients (recipients, toList, Type.TO);
		List<String> ccList = email.getCcList();
		addEmailRecipients (recipients, ccList, Type.CC);
		List<String> bccList = email.getBccList();
		addEmailRecipients (recipients, bccList, Type.BCC);				
		message.setTo(recipients);
		
		message.setSubject(email.getSubject());
		message.setHtml(email.getMessage());
			
		try {
			MandrillMessageStatus[] messageStatusReports = mandrillApi
			        .messages().send(message, false);	
			MandrillMessageStatus status = messageStatusReports[messageStatusReports.length - 1];
			if (status.getStatus() != null) {
				logger.info(String.format(EMAIL_SENDING_SUCCESSFUL, MANDRILL));
				result = new Result(true, null);
			} else {
				logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL)
						+ status.getRejectReason());
				result = new Result(true, THIRDPARTY_SERVICE_FAILURE);
			}
		} catch (MandrillApiError e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
		} catch (IOException e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
		}
		
		return result;
	}
	
	private void addEmailRecipients (List<Recipient> recipients, List<String> emailAddresses, Type type) {
		if (emailAddresses != null) {
			for (String emailAddr: emailAddresses) {
				Recipient recipient = new Recipient();
				recipient.setEmail(emailAddr);
				recipient.setType(type);
				recipients.add(recipient);
			}
		}
	}
	
	public static void main (String [] args) {
		Email email = new Email ();
		List<String> toList = new ArrayList<String>();
		toList.add("anilcs0405@gmail.com");
		email.setToList(toList);
		email.setFrom("anilcs0405@gmail.com");
		email.setSubject("Empty subject");
		email.setMessage("Hi,\n\nEmpty content\n\n\nThank you,\nEmail Service");
		AbstractEmailSender emailSender = new MandrillEmailSender(email);
		Result result = emailSender.sendEmail();
		if (result.isSuccess()) {
			System.out.println("Success");
		} else {
			System.out.println("Failed: " + result.getErrorMessage());
		}
	}

}
