<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../jspf/directive/taglib.jspf" %>
<%@ include file="../../jspf/directive/administratorCheck.jspf" %>
<%@ include file="../../jspf/directive/locale.jspf" %>
<html>
<head>
    <title>
        <fmt:message key="administrationTariffCreateForm_jspf.tariffCreateForm"/>
    </title>
    <%@ include file="../../jspf/directive/bootstrap.jspf" %>
</head>
<body>
<%@ include file="../../jspf/navigation.jspf" %>

<h4 class="text-center">
    <fmt:message key="administrationTariffCreateForm_jspf.tariffCreateForm"/>
</h4>
<br/>

<%@ include file="../../jspf/administration/administrationTariffCreateForm.jspf" %>

</body>
</html>
