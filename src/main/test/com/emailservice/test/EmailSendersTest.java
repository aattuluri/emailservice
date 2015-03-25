package com.emailservice.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:emailservice-context-test.xml" })
public class EmailSendersTest {
	
	@Test
	public void testSendGridEmailSender(){
		
	}
	
	@Test
	public void testMandrillEmailSender(){
		
	}
}
