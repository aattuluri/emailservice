#Introduction
**Email Service** is a cloud service to send emails. This simple service uses two third party email senders (SendGrid and Mandrill) to provide a reliable email sending service. It fails over to the secondary service provider (Mandrill) when the primary (SendGrid) is not available. When both the services are not available simultaneously then the email service will be down for 60 seconds after which the server starts trying to send emails again with the last used (successful) service provider. 

#Frameworks and Libraries used
**Languages:** Java (1.7), HTML, CSS, Javascript  
**Web container:** Tomcat (7.x.x)  
**Web framework:** Spring web framework   
**Build tool:** Maven   
**Libraries:**  Google gson, SendGrid, Mandrill, Apache http, Junit, Jquery

#REST API Documentation
For better tracking purposes only one email message can be sent per request. The REST API is a simple HTTP POST request with a json payload (please see the Request section below) and the response is a standard HTTP status code with optional json payload (please see the Response section below).

##Request
**URL:** http://emailservice.elasticbeanstalk.com/SendEmail  
**Request Type:** POST  
**ContentType:** application/json  
**Payload:**  
```javascript
{
  "email": {
       "from": "anilcs0405@gmail.com",              
       "to": ["abc@gmail.com", "xyz@gmail.com"],   
       "cc": [],                                   
       "bcc": [],                                   
       "subject": "Hello email!",                   
       "message": "Hello\nThis is a hello email from Email Service!\n\nThank You\nEmail Service." 
  }
}
```
**Key - Requirement - Data type**  
_______________________________________   
*from* - mandatory - string  
Specifies the email address of the sender  
*to* - mandatory - array (of strings)  
Specifies the list of email addresses the email should be sent to.  
*cc* - optional - array (of strings)  
Specifies the list of email addresses the email should be CC'ed to.  
*bcc* - optional - array (of strings)  
Specifies the list of email addresses the email should be BCC'ed to.  
*subject* - optional - string  
Specifies subject of the email (utf-8 format)  
*message* - mandatory - string  
Specifies body of the email (utf-8 format)  

##Response
**Response code - Description**  
_______________________________   
**200** - Request is successful. Please look at the response json below for the result of the operation.  
```javascript
{
  "result": {
      "success": [true|false], //mandatory, boolean - a value of 'true' will ensure that email was sent, but delivery is not guranteed.
      "errorMessage": "blah blah blah" //optional, string - warning: this is not a standard error code
  }
}
```
**400** - Bad request (one or more mandatory parameters are missing)  
**405** - Method not allowed  (if the http request type is GET)  
**500** - Internal server error (Unexpected/unknown errors on server side, Ex: no network connectivity)  
**503** - Service unavailable  
  
#Future work
Support batch requests  
Support other email functionalities like attachments etc.  
Implement rate limiting  
Implement database logging for all the RESTAPI transactions  
