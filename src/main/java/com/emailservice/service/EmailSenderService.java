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

@Component("emailSenderService")
public class EmailSenderService {
	
	private EmailSenderType lastWorkedSender = null;
	boolean serviceDown = false;
	
	private TimerTask tt;
	private Timer timer;

	@PostConstruct
	public void init() {
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
		//if the service is down currently, then return service down message
		if (serviceDown) {
			return new Result(false, "Service is down. Try again later.");
		}
		
		//if there is no lastWorkedSender or if lastWorkedSender is SENDGRID service then use that
		Result result = null;
		AbstractEmailSender emailSender = null;
		if (lastWorkedSender == null
				|| EmailSenderType.SENDGRID.equals(lastWorkedSender)) {
			emailSender = new SendGridEmailSender(email);
			result = emailSender.sendEmail();
			if (result.isSuccess()) {
				lastWorkedSender = EmailSenderType.SENDGRID;
			} else {
				emailSender = new MandrillEmailSender(email);
				result = emailSender.sendEmail();
				if (result.isSuccess()) {
					lastWorkedSender = EmailSenderType.MANDRILL;
				} else {
					serviceDown = true;
				}
			}
		} else {
			emailSender = new MandrillEmailSender(email);
			result = emailSender.sendEmail();
			if (result.isSuccess()) {
				lastWorkedSender = EmailSenderType.MANDRILL;
			} else {
				emailSender = new SendGridEmailSender(email);
				result = emailSender.sendEmail();
				if (result.isSuccess()) {
					lastWorkedSender = EmailSenderType.SENDGRID;
				} else {
					serviceDown = true;
				}
			}
		}
		return result;
	}
	
	@PreDestroy
	public void onDestroy() {		
		timer.cancel();
	}	
	
}
