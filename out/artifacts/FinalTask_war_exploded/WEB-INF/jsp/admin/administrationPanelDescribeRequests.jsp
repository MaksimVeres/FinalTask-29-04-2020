<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../jspf/directive/taglib.jspf"%>
<%@ include file="../../jspf/directive/locale.jspf"%>
<%@ include file="../../jspf/directive/administratorCheck.jspf"%>
<html>
<head>
    <title><fmt:message key="administrationDescribeRequests_jspf.describeRequests"/></title>
    <%@ include file="../../jspf/directive/bootstrap.jspf"%>

</head>
<body>
<%@ include file="../../jspf/navigation.jspf"%>

<h4 class="text-center"><fmt:message key="administrationDescribeRequests_jspf.describeRequests"/></h4>
<br/>

<%@ include file="../../jspf/administration/administrationDescribeRequests.jspf"%>

</body>
</html>
