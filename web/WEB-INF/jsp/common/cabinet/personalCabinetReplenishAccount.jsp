<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../../jspf/directive/taglib.jspf" %>
<%@ include file="../../../jspf/directive/authenticationCheck.jspf" %>
<%@ include file="../../../jspf/directive/locale.jspf" %>
<html>
<head>
    <title>
        <fmt:message key="userCabinetReplenishAccount_jspf.account.replenishment"/>
    </title>
    <%@ include file="../../../jspf/directive/bootstrap.jspf" %>
</head>
<body>
<%@ include file="../../../jspf/navigation.jspf" %>

<h4 class="text-center">
    <fmt:message key="userCabinetReplenishAccount_jspf.account.replenishment"/>
</h4>
<br/>

<%@ include file="../../../jspf/cabinet/userCabinetReplenishAccount.jspf" %>

</body>
</html>
