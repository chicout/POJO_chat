<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="style/style.css" />
<script type="text/javascript" language="javascript" src="jquery.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var pop_window = $('#pop-up-window');

		var X = ($(window).width() - pop_window.width()) / 2;
		var Y = ($(window).height() - pop_window.height()) / 2;

		pop_window.css('left', X + 'px');
		pop_window.css('top', Y + 'px');

		pop_window.hide();
		$('#button').click(function() {
			document.getElementById("password").value="";
			document.getElementById("repeat").value="";
			document.getElementById("username").value="";
			pop_window.css('display', 'block');
			pop_window.css('top', -pop_window.height() + 'px');
			pop_window.animate({
				top : Y
			}, "slow");
		});
		$('#btnClose').click(function() {
			pop_window.hide(1000);
		});

	});
	function getXMLObject()  
	{
	   var xmlHttp = false;
	   try {
	     xmlHttp = new ActiveXObject("Msxml2.XMLHTTP"); 
	   }
	   catch (e) {
	     try {
	       xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");  
	     }
	     catch (e2) {
	       xmlHttp = false;   
	     }
	   }
	   if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
	     xmlHttp = new XMLHttpRequest();     
	   }
	   return xmlHttp;  
	}
	 
	var xmlhttp = new getXMLObject();
	function registrationServerResponse() {
		   if (xmlhttp.readyState == 4) {
			   
		     if(xmlhttp.status == 200) {
		    	 if(new String(xmlhttp.responseText).substring(0,4)==new String("true")){
		    		 var pop_window = $('#pop-up-window');
		    		 pop_window.hide(1000);
		    	 }
		    	 else
		    		 alert("Name exists");
		     }
		     else {
		        alert("Error during AJAX call.");
		     }
		   }
		}
	function loginServerResponse() {
		  
			var response = xmlhttp.responseText.split(",");
		   if (xmlhttp.readyState == 4) {
			   
		     if(xmlhttp.status == 200) {
		    	 
		    	 if(new String(response[0]).substring(0,4)==new String("true")){
						location.href="GreetingServlet?username="+response[1];
		    	 }
		    	 else
		    		 alert("Wrong credentials");
		     }
		     else {
		        alert("Error during AJAX call.");
		     }
		   }
		}
	function checkRegistration() {
		var username =document.getElementById("username").value;
		var password =document.getElementById("password").value;
		var repeat = document.getElementById("repeat").value;
		if(username.length==0){
			alert("Enter username");
			return false;
		}
		if(password.length==0){
			alert("Enter password");
			return false;
		}
		if(password==repeat){
			  if(xmlhttp) {
				   var params="username="+username+"&password="+password;
				   xmlhttp.open("POST",document.forms[0].action,true); //gettime will be the servlet name
				   xmlhttp.onreadystatechange  = registrationServerResponse;
				   xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				   xmlhttp.send(params);
				  }
		}
		else{
			alert("Repeat the password correctly");
			return false;
		}
	}
	function checkLogin() {
		
		var username =document.getElementById("user").value;
		var password =document.getElementById("pass").value;
		if(username.length==0){
			alert("Enter username");
			return false;
		}
		if(password.length==0){
			alert("Enter password");
			return false;
		}
		
			  if(xmlhttp) {
				   var params="username="+username+"&password="+password;
				   
				   xmlhttp.open("POST",document.forms[1].action,true); //gettime will be the servlet name
				   xmlhttp.onreadystatechange  = loginServerResponse;
				   xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				   xmlhttp.send(params);
				  }
		else{
			alert("Repeat the password correctly");
			return false;
		}
	}	
</script>
<title>Insert title here</title>
</head>
<body>
<div id="floater"><img src="images/welcome.gif" width="30%"
	height="60%" /></div>
<div id="pop-up-window">
<div id="windowHeader"><img src="images/window_close.jpg"
	id="btnClose" /></div>
<div id="windowRightSide">
<div id="windowLeftSide"></div>
</div>
<div id="windowContent">

<form action="RegistrationController" method="post">
<div class="cred">
<table>
	<tr>
		<td>Username:</td>
		<td><input type="text" id="username" name="username"></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="text" id="password" name="password"></td>
	</tr>
	<tr>
		<td>Repeat password:</td>
		<td><input type="text" id="repeat" name="repeat"></td>
	</tr>
</table>
</div>
<table align="right">
	<tr>
		<td><input type="button" onclick="return checkRegistration()"
			value="Ok"></td>
	</tr>
</table>
</form>

</div>
</div>
<form action="LoginController" method="post">
<div id="child"><input type="text" id="user" name="username"><br>
<input type="text" id="pass" name="password"><br>
</div>
<div style="margin-right: 200px;"><input type="button" id="button"
	value="Registration"> <input type="button" id="button"
	onclick="return checkLogin()" value="Go"></div>
</form>

</body>
</html>