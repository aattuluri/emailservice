package com.emailservice.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.emailservice.emailsenders.AbstractEmailSender;
import com.emailservice.emailsenders.EmailSenderType;
import com.emailservice.emailsenders.MandrillEmailSender;
import com.emailservice.emailsenders.SendGridEmailSender;
import com.emailservice.json.Email;
import com.emailservice.json.Result;

/**
 * This class provides the email sending service (using the two third party email sender services) and abstract out the Fail overs
 * 
 * @author Anil
 *
 */
@SuppressWarnings("restriction")
@Component("emailSenderService")
public class EmailSenderService {
	
	private EmailSenderType lastWorkedSender = null;
	boolean serviceDown = false;
	
	private String sendgridUsername;
	private String sendgridPassword;
	
	private String mandrillApiKey;
	
	private TimerTask tt;
	private Timer timer;

	
	@PostConstruct
	public void init() {
		//this task ensures that the server will be back in service
		tt = new TimerTask() {			
			@Override
			public void run() {
				serviceDown = false;
			}
		};
		timer = new Timer();
		timer.schedule(tt, new Date(), 60 * 1000);
	}
	
	synchronized public Result sendEmail (Email email) {
		
		//if there is no lastWorkedSender or if lastWorkedSender is SENDGRID service then use that
		Result result = null;
		AbstractEmailSender emailSender = null;
		if (lastWorkedSender == null
				|| EmailSenderType.SENDGRID.equals(lastWorkedSender)) {
			emailSender = new SendGridEmailSender(email, sendgridUsername, sendgridPassword);
			result = emailSender.sendEmail();
			if (result.isSuccess()) {
				lastWorkedSender = EmailSenderType.SENDGRID;
			} else {
				//if this was a third party request failure due to specific input, then we dont consider that the service is down
				if (!result.isServiceIsDown()) {
					result.setServiceIsDown(null);
					return result;
				}
				//we failed sending the email using SENDGRID service, we now try MANDRILL service
				emailSender = new MandrillEmailSender(email, mandrillApiKey);
				result = emailSender.sendEmail();
				if (result.isSuccess()) {
					lastWorkedSender = EmailSenderType.MANDRILL;
				} else {
					//we failed to send the email using MANDRILL and SENDGRID, so we mark the service down
					serviceDown = true;
				}
			}
		} else {
			emailSender = new MandrillEmailSender(email, mandrillApiKey);
			result = emailSender.sendEmail();
			if (result.isSuccess()) {
				lastWorkedSender = EmailSenderType.MANDRILL;
			} else {
				//if this was a third party request failure due to specific input, then we dont consider that the service is down
				if (!result.isServiceIsDown()) {
					result.setServiceIsDown(null);
					return result;
				}
				//we failed sending the email using MANDRILL service, we now try SENDGRID service
				emailSender = new SendGridEmailSender(email, sendgridUsername, sendgridPassword);
				result = emailSender.sendEmail();
				if (result.isSuccess()) {
					lastWorkedSender = EmailSenderType.SENDGRID;
				} else {
					//we failed to send the email using MANDRILL and SENDGRID, so we mark the service down
					serviceDown = true;
				}
			}
		}
		return result;
	}
	
	public boolean isServiceDown() {
		return serviceDown;
	}

	public void setServiceDown(boolean serviceDown) {
		this.serviceDown = serviceDown;
	}	

	public EmailSenderType getLastWorkedSender() {
		return lastWorkedSender;
	}
	
	public String getSendgridUsername() {
		return sendgridUsername;
	}

	public void setSendgridUsername(String sendgridUsername) {
		this.sendgridUsername = sendgridUsername;
	}

	public String getSendgridPassword() {
		return sendgridPassword;
	}

	public void setSendgridPassword(String sendgridPassword) {
		this.sendgridPassword = sendgridPassword;
	}

	public String getMandrillApiKey() {
		return mandrillApiKey;
	}

	public void setMandrillApiKey(String mandrillApiKey) {
		this.mandrillApiKey = mandrillApiKey;
	}

	@PreDestroy
	public void onDestroy() {		
		timer.cancel();
	}	
	
}
