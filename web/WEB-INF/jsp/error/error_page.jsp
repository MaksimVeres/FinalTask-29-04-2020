<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../jspf/directive/taglib.jspf" %>
<%@ include file="../../jspf/directive/locale.jspf" %>
<%@ include file="../../jspf/directive/bootstrap.jspf" %>
<html>
<head>
    <title>
        <fmt:message key="error_page_jsp.error"/>
    </title>
</head>
<body>

<%@ include file="../../jspf/navigation.jspf" %>

<div class="container">

    <div class="row">
        <div class="col">
            <div class="card mb-4 box-shadow">
                <div class="card-header">
                    <h4 class="my-0 font-weight-normal">
                        <c:choose>
                            <c:when test="${not empty requestScope.errorTitle}">
                                <fmt:message key="${requestScope.errorTitle}"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:message key="error_page_jsp.unknown.error"/>
                            </c:otherwise>
                        </c:choose>

                    </h4>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty requestScope.ufErrorMessage}">
                            <fmt:message key="${requestScope.ufErrorMessage}"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="error_page_jsp.problem.occurred"/>
                        </c:otherwise>
                    </c:choose>
                    <hr/>
                    <c:if test="${not empty sessionScope.user}">
                        <c:if test="${sessionScope.user.roleId == 0}">
                            <fmt:message key="error_page_jsp.detailed"/>
                            <br/>
                            <fmt:message key="${requestScope.errorMessage}"/>
                        </c:if>
                    </c:if>
                </div>
                <br/>
                <div class="card-footer">
                    <a class="btn btn-outline-info bg-info text-light" href="page?name=mainPage">
                        <fmt:message key="forward.toMainPage"/>
                    </a>
                </div>
            </div>
        </div>
    </div>

</div>

<%@ include file="../../jspf/footer.jspf" %>

</body>
</html>
