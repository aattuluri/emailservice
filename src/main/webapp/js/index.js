$( document ).ready(function() {
    $("#sendemail").submit(function(event) {
    	  event.preventDefault();
    	  sendEmailRequest();
    });
});

function sendEmailRequest () {
	var jsonObj = {};
	var email = {};
	
	var from =  $('#from').val();
	var to = $('#to').val();
	var cc = $('#cc').val();
	var bcc = $('#bcc').val();
	var subject = $('#subject').val();
	var message = $('#message').val();
	
	email["from"] = from;
	var toArr = splitToArray(to);
	
	if (toArr !== null) {
		email["toList"] = toArr;
	}
	
	var ccArr = splitToArray(cc);
	if (ccArr !== null) {
		email["ccList"] = ccArr;
	}
	
	var bccArr = splitToArray(bcc);
	if (bccArr !== null) {
		email["bccList"] = bccArr;
	}
	email["message"] = message;
	email["subject"] = subject;
	jsonObj["email"] = email;
	
	//do the ajax request here
	
	$.ajax({
		type: "POST",
		url: "http://emailservice.elasticbeanstalk.com/SendEmail",
		data: JSON.stringify(jsonObj),
		success: function(data, textStatus) {
			$("#status").html("Email was sent successfully.");
			$("#status").removeClass("error").addClass("success");
		},
		error: function(xhr) {
			$("#status").html("Failed to send the email. Server status code: " + xhr.status);
			$("#status").removeClass("success").addClass("error");
		},
		contentType: "application/json",
		dataType: "json"
	});
	
}

function splitToArray (str) {
	if (str !== undefined && str !== null) {
		var splitStr = str.split(",");
		if (splitStr != null
				&& splitStr.length > 0) {
			var toArr = new Array();
			for (var i = 0; i < splitStr.length; i++) {
				if (splitStr[i] !== null
						&& splitStr[i] !== "") {
					toArr.push(splitStr[i].trim());
				}
			}
			return toArr;
		}
	}
	return null;
}