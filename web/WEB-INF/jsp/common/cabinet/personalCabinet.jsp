<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jspf/directive/taglib.jspf" %>
<%@ include file="/WEB-INF/jspf/directive/locale.jspf"%>
<%@ include file="/WEB-INF/jspf/directive/authenticationCheck.jspf"%>
<html>
<head>
    <title>
        <fmt:message key="personalCabinet_jsp.cabinet"/>
    </title>
    <%@ include file="/WEB-INF/jspf/directive/bootstrap.jspf" %>
    <%@ include file="/WEB-INF/jspf/directive/indexStyle.jspf"%>

</head>
<body>

<%@ include file="/WEB-INF/jspf/navigation.jspf" %>

<%@ include file="/WEB-INF/jspf/cabinet/userCabinetMain.jspf" %>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>


</body>
</html>
