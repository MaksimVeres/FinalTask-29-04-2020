<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jspf/directive/taglib.jspf" %>
<%@ include file="/WEB-INF/jspf/directive/locale.jspf" %>
<%@ include file="/WEB-INF/jspf/directive/authenticationCheck.jspf" %>
<%@ include file="/WEB-INF/jspf/directive/bootstrap.jspf" %>

<html>
<head>
    <title>
        <fmt:message key="changePassword_jspf.password.change"/>
    </title>

    <!-- CUSTOM CSS -->
    <link rel="stylesheet" href="../../../../css/login.css"/>

    <!-- CUSTOM JS -->
    <script src="../../../../js/passwordChange.js" type="text/javascript"></script>

</head>
<body>
<%@ include file="/WEB-INF/jspf/navigation.jspf" %>
<%@ include file="/WEB-INF/jspf/change/changePassword.jspf" %>
</body>
</html>
