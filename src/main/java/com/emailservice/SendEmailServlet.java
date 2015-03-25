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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpStatus.SC_METHOD_NOT_ALLOWED);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		EmailServiceJsonRoot responseObj = new EmailServiceJsonRoot();
		OutputStream out = response.getOutputStream();
		try {
			String inputJson = IOUtils.toString(request.getInputStream());			
			EmailServiceJsonRoot jsonObj = null;
			// check if have valid JSON
			if (inputJson == null || inputJson.length() == 0) {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return;
			} else {
				jsonObj = gson.fromJson(inputJson, EmailServiceJsonRoot.class);
			}
			Email email = jsonObj.getEmail();
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
			
			Result result = emailSenderService.sendEmail(email);
			logger.info(result.isSuccess());
			logger.info(result.getErrorMessage());
			responseObj.setResult(result);
			response.setStatus(HttpStatus.SC_OK);

		} catch (Exception ex) {
			logger.error(ex.fillInStackTrace());
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			responseObj.setResult(new Result(false, "Unknown Server Error"));
		}
		response.setContentType("text/json");
		out.write(gson.toJson(responseObj).getBytes());
		out.close();
	}
}
