<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	import="partable.webdb.server.*"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%

String part = request.getParameter("part");
if (part != null) {
	PartUpdater pu = new PartUpdater(PMF.get());
	
	List<String> plist = new ArrayList<String>();
	plist.add("BBa_" + part);
	pu.updateParts(plist);
	
	PMF.release();
}
%>

</body>
</html>