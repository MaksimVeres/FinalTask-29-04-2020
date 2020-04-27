<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jspf/directive/taglib.jspf"%>
<%@ include file="/WEB-INF/jspf/directive/locale.jspf"%>
<%@ include file="/WEB-INF/jspf/directive/bootstrap.jspf"%>
<html>
<head>
    <title>
        <fmt:message key="tariffs_jspf.tariffs"/>
    </title>
    <%@ include file="/WEB-INF/jspf/directive/indexStyle.jspf"%>
</head>
<body>
    <%@ include file="/WEB-INF/jspf/navigation.jspf"%>
    <%@ include file="/WEB-INF/jspf/tariff/tariffs.jspf"%>
</body>
</html>
