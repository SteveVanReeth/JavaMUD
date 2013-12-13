<%-- 
    Document   : foutVerwijderd
    Created on : 13-dec-2013, 11:41:25
    Author     : Steve.Vanreeth
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="nl">
<head>
<title>Gebruiker niet verwijderd</title>
<link rel="stylesheet"
	href="${pageContext.servletContext.contextPath}/styles/default.css" />
</head>
<body>
	<div>De gebruiker ${emailAdres} (${id}) is NIET verwijderd omdat er nog een karakter bestaat.</div>
        <nav>
            <ul class="menu">
		<c:url value="/hoofdmenu" var="hoofdmenuURL" />
		<li><a href="${hoofdmenuURL}">Ga terug naar hoofdmenu</a></li>
            </ul>
	</nav
</body>
</html>
