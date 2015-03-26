package com.emailservice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.emailservice.json.Email;
import com.emailservice.json.EmailServiceJsonRoot;
import com.emailservice.json.Result;
import com.emailservice.service.EmailSenderService;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * This is the servlet that handles SendEmail requests
 * 
 * @author Anil
 *
 */
@WebServlet("/SendEmail")
public class SendEmailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private EmailSenderService emailSenderService;

	private final Gson gson = new Gson();
	private final static Logger logger = Logger.getLogger(SendEmailServlet.class);

	public SendEmailServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(getServletContext());
		emailSenderService = (EmailSenderService) context.getBean("emailSenderService");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);

	}
	
	/**
	 * This method handles the REST API request to send emails and also the requests from web page 
	 */
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		EmailServiceJsonRoot responseObj = new EmailServiceJsonRoot();
		OutputStream out = response.getOutputStream();
		try {
			if (request.getInputStream() == null) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			String inputJson = IOUtils.toString(request.getInputStream());			
			EmailServiceJsonRoot jsonObj = null;
			// check if the request body is not empty
			if (inputJson == null || inputJson.length() == 0) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			} else {
				try {
					jsonObj = gson.fromJson(inputJson, EmailServiceJsonRoot.class);
				} catch (JsonParseException jpe) {
					//we have json parsing failure, so the its not a valid json
					logger.error(jpe.getMessage());
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					return;
				}
			}
			
			Email email = jsonObj.getEmail();
			//check if we have email
			if (email == null) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			
			//check if the TO email address list is emtpy
			List<String> toList = email.getToList();
			if (toList == null
					|| toList.isEmpty()) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			
			//check if the email message is emtpy
			String message = email.getMessage();
			if (message == null
					|| message.isEmpty()) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			//check if the service is down
			if (emailSenderService.isServiceDown()) {
				response.setStatus(503);
				return;
			}
			//all validations passed, now send the email
			Result result = emailSenderService.sendEmail(email);
			responseObj.setResult(result);
			response.setStatus(HttpStatus.SC_OK);
			response.setContentType("application/json");
			//write the json response back to the client
			out.write(gson.toJson(responseObj).getBytes());
		} catch (Exception ex) {
			logger.error(ex.fillInStackTrace());
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		} finally {
			out.close();
		}
		
	}

	public EmailSenderService getEmailSenderService() {
		return emailSenderService;
	}

	public void setEmailSenderService(EmailSenderService emailSenderService) {
		this.emailSenderService = emailSenderService;
	}
	
}
