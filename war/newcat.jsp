<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="partable.webdb.server.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%

if (request.getParameter("name") != null) {
	Category cat = new  Category();
	
	cat.name = request.getParameter("name");
	PersistenceManager pm = PMF.get();
	pm.makePersistent(cat);
	pm.flush();
	response.getWriter().print("ID: " + cat.name);
	
	PMF.release();
}

%>
</body>
</html>