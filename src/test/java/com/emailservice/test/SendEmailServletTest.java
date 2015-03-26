package com.emailservice.test;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.emailservice.SendEmailServlet;
import com.emailservice.service.EmailSenderService;

/**
 * This class tests all the possible inputs to SendEmailServlet and the expected response codes
 * 
 * @author Anil
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:emailservice-context-test.xml" })
public class SendEmailServletTest {
		
	private SendEmailServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @Autowired
    EmailSenderService emailSenderService;
    
    @Before
    public void setUp() {
        servlet = new SendEmailServlet();        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    
    @Test
    public void testBadRequests() throws ServletException, IOException {
    	
    	//request type
    	request.setMethod("GET");
        servlet.doGet(request, response);        
        Assert.assertEquals(405, response.getStatus());
    	
    	//empty content
        request.setContent("".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
    	//invalid json
        request.setContent("{invalid input}".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
        //no email
        request.setContent("{\"anil\": {}}".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
        //no TO list
        request.setContent("{\"email\": {}}".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
        //empty TO list
        request.setContent("{\"email\": {\"toList\": []}}".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
        //no Body list
        request.setContent("{\"email\": {\"from\": \"borntechies@gmail.com\", \"toList\": [borntechies@gmail.com]}}".getBytes());
        request.setContentType("application/json");
        request.addParameter("password", "tiger");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
        
        //empty Body list
        request.setContent("{\"email\": {\"from\": \"borntechies@gmail.com\", \"toList\": [borntechies@gmail.com], \"message\": \"\"}}".getBytes());
        request.setContentType("application/json");
        servlet.doPost(request, response);        
        Assert.assertEquals(400, response.getStatus());
       
    }
    
    @Test
    @Ignore
    public void testValidRequests() throws ServletException, IOException {
    	request.setContent("{\"email\": {\"from\": \"borntechies@gmail.com\", \"toList\": [borntechies@gmail.com], \"message\": \"Hello!\"}}".getBytes());
        request.setContentType("application/json");
        servlet.setEmailSenderService(emailSenderService);
        servlet.doPost(request, response);        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(true, response.getContentAsString().contains("false"));
        
        request.setContent("{\"email\": {\"from\": \"borntechies@gmail.com\", \"toList\": [abc@gmail.com], \"message\": \"Hello!\"}}".getBytes());
        request.setContentType("application/json");
        servlet.setEmailSenderService(emailSenderService);
        servlet.doPost(request, response);        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(true, response.getContentAsString().contains("false"));
    }
	
}
