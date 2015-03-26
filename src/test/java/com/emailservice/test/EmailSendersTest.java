package com.emailservice.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.emailservice.emailsenders.AbstractEmailSender;
import com.emailservice.emailsenders.EmailSenderType;
import com.emailservice.emailsenders.MandrillEmailSender;
import com.emailservice.emailsenders.SendGridEmailSender;
import com.emailservice.json.Email;
import com.emailservice.json.Result;
import com.emailservice.service.EmailSenderService;

/**
 * 
 * This class has the test cases for email senders, service down and the service providers fail overs
 * 
 * @author Anil
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:emailservice-context-test.xml" })
public class EmailSendersTest {
	
	@Autowired
	private EmailSenderService emailSenderService;
	
	private Email email;
	
	private Email createEmail () {
		Email email = new Email ();
		List<String> toList = new ArrayList<String>();
		toList.add("borntechies@gmail.com");
		email.setToList(toList);
		email.setFrom("borntechies@gmail.com");
		email.setSubject("Empty subject");
		email.setMessage("Hi,\n\nEmpty content\n\n\nThank you,\nEmail Service");
		return email;
	}
	
	@Before
	public void setUp () {
		if (email == null) {
			email = createEmail();
		}
	}
	
	@Test
	public void testSendGridEmailSender () {
		AbstractEmailSender emailSender = new SendGridEmailSender(email, 
				emailSenderService.getSendgridUsername(),
				emailSenderService.getSendgridPassword());
		Result result = emailSender.sendEmail();
		Assert.assertEquals(true, result.isSuccess());
	}
	
	@Test
	public void testMandrillEmailSender () {
		AbstractEmailSender emailSender = new MandrillEmailSender(email, 
				emailSenderService.getMandrillApiKey());
		Result result = emailSender.sendEmail();
		Assert.assertEquals(true, result.isSuccess());
	}
	
	@Test
	public void testServiceDown () {
		//Set the 'from' address to null to fail the both the service providers and bring the service down
		Email email = createEmail();
		email.setToList(null);
		emailSenderService.sendEmail(email);
		Assert.assertEquals(true, emailSenderService.isServiceDown());
	}
	
	@Test
	@Ignore
	public void testFailOvers () {
		String oldUsername = emailSenderService.getSendgridUsername();
		//Set wrong user name for sendgrid
		emailSenderService.setSendgridUsername("garbage");
		emailSenderService.sendEmail(email);
		Assert.assertEquals(EmailSenderType.MANDRILL, emailSenderService.getLastWorkedSender());
		
		emailSenderService.setSendgridUsername(oldUsername);
		
		String apiKey = emailSenderService.getMandrillApiKey();
		//Now set incorrect mandrill api key
		emailSenderService.setMandrillApiKey("garbage");
		emailSenderService.sendEmail(email);
		Assert.assertEquals(EmailSenderType.SENDGRID, emailSenderService.getLastWorkedSender());
		
		emailSenderService.setMandrillApiKey(apiKey);
	}
	
	
}
