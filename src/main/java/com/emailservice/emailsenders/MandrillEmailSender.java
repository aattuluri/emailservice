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

/**
 * This class implements the email sending using Mandrill REST API service
 * 
 * @author Anil
 *
 */
public class MandrillEmailSender extends AbstractEmailSender {
	
	private static Logger logger = Logger.getLogger(MandrillEmailSender.class);
	
	private static final String MANDRILL = "Mandrill";
	private static final String SENT = "sent";
		
	public MandrillEmailSender(Email email, String apiKey) {
		super(email);
		this.APIKEY = apiKey;
	}
	
	private String APIKEY;
	
	@Override
	public Result sendEmail() {
		
		Result result = null;
		try {
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
			
			MandrillMessageStatus[] messageStatusReports = mandrillApi
			        .messages().send(message, false);	
			MandrillMessageStatus status = messageStatusReports[messageStatusReports.length - 1];
			if (status.getStatus() != null &&
					status.getStatus().equals(SENT)) {
				logger.info(String.format(EMAIL_SENDING_SUCCESSFUL, MANDRILL));
				result = new Result(true, null);
			} else {
				logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL)
						+ status.getRejectReason());
				result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			}
		} catch (MandrillApiError e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			result.setServiceIsDown(true);
		} catch (IOException e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			result.setServiceIsDown(true);
		} catch (Exception e) {
			logger.error(String.format(EMAIL_SENDING_FAILED, MANDRILL), e);
			result = new Result(false, THIRDPARTY_SERVICE_FAILURE);
			result.setServiceIsDown(true);
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

}
