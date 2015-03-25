#Email Service
'Email Service' is a Cloud service to send emails. This simple service uses two third party email senders (currently SendGrid and Mandrill) to provide a reliable service. It fails over to the secondary service provider (Mandrill) when the primary (SendGrid) is not available. When both the services are not available the email service will be down for 60 seconds after which the server starts trying to send emails again with the last used (successful) service provider. 


#REST API Documentation
For better tracking purposes only one email message can be sent per request. The REST API is a simple HTTP POST request. The details about using the REST API are below:

##Request Format
URL: http://emailservice.elasticbeanstalk.com/SendEmail  
Request Type: POST  
ContentType: application/json  
Payload:  
```javascript
{
  "email": {
       "from": "anilcs0405@gmail.com",              //mandatory
       "to": ["abc@gmail.com", "xyz@gmail.com"],    //mandatory
       "cc": [],                                    //optional
       "bcc": [],                                   //optional
       "subject": "Hello email!",                   //optional
       "message": "Hello\nThis is a hello email from Email Service!\n\nThank You\nEmail Service." //mandatory
  }
}
```

###from - string
Specifies the email address of the sender
###to - array
Specifies the list of email addresses the email should be sent to.
###cc - array
Specifies the list of email addresses the email should be CC'ed to.
###bcc - array
Specifies the list of email addresses the email should be BCC'ed to.
###subject - string
Specifies subject of the email (utf-8 format)
###message - string
Specifies body of the email (utf-8 format)

##Response Format
200 - Request successful, the response has json which tells if the email was sent successfully.
Response body: json, see the examples below.  
Example response when email sending is successful:  
```javascript
{
  "success": true     //email was sent successfully
}
```
Example response when email sending fails:  
```javascript
{
  "success": false,   //failed to send the email
  "errorMessage": "service_is_down" //error message, this is not a code, so shouldn't be used by the client machine to retry
}
```
400 - Bad request (one or more mandatory parameters are missing)  
500 - Internal server error (unknown errors on server side, Ex: no network connectivity)  
