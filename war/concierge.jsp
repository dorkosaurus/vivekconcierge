<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
		<title>Vivek's concierge</title>
  </head>

  <body>
	<form action="http://viveksconcierge.appspot.com/concierge" method="GET">
	<H1>
	<select name='calories'><option value=''>CALORIES</option>
	<%for(int i=50;i<3000;i+=50){%>
		<option value="<%=i%>"><%=i%></option>	
	<%}%>
	</select>
	<select name='minutes_cardio'><option value=''>MINUTES CARDIO</option>
	<%for(int i=10;i<180;i+=5){%>
		<option value="<%=i%>"><%=i%></option>	
	<%}%>
	</select>

	<input type='SUBMIT'>
	</H1>
	</form>
  </body>
</html>