<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../../jspf/directive/noAuthenticationCheck.jspf"%>
<%@ include file="../../../jspf/directive/taglib.jspf"%>
<%@ include file="../../../jspf/directive/locale.jspf"%>

<html>
<head>
    <title>
        <fmt:message key="authentication_jspf.authentication"/>
    </title>

    <%@ include file="../../../jspf/directive/bootstrap.jspf"%>

    <!-- CUSTOM CSS -->
    <link rel="stylesheet" href="../../../../css/login.css"/>

    <!-- CUSTOM JS -->
    <script src="../../../../js/login.js" type="text/javascript"></script>

</head>
<body>

<%@ include file="../../../jspf/navigation.jspf"%>
<%@ include file="../../../jspf/authentication/authentication.jspf"%>

</body>
</html>
