<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../jspf/directive/taglib.jspf" %>
<%@ include file="../../jspf/directive/administratorCheck.jspf" %>
<%@ include file="../../jspf/directive/locale.jspf" %>
<html>
<head>
    <title>
        <fmt:message key="administrationPanelUserList_jspf.userList"/>
    </title>
    <%@ include file="../../jspf/directive/bootstrap.jspf" %>
</head>
<body>
<%@ include file="../../jspf/navigation.jspf" %>

<h4 class="text-center">
    <fmt:message key="administrationPanelUserList_jspf.userList"/>
</h4>
<br/>

<%@ include file="../../jspf/administration/administrationUserList.jspf" %>

</body>
</html>
