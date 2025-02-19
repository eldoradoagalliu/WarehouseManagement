<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login to Shop</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/main-style.css">
</head>
<body>
<div class="main-part">
    <div class="login-form">
        <%--@elvariable id="user" type="user"--%>
        <form:form action="/api/v1/authenticate" method="POST" modelAttribute="user">
            <h2 class="text-decoration-underline text-center mb-3 login">Login</h2>
            <c:if test="${logoutMessage != null}">
                <div class="text-success text-center m-2"><h5><c:out value="${logoutMessage}"/></h5></div>
            </c:if>
            <c:if test="${errorMessage != null}">
                <div class="text-danger text-center m-2"><h5><c:out value="${errorMessage}"/></h5></div>
            </c:if>
            <div class="mb-3 d-flex align-content-center">
                <form:label path="email" class="col-sm-4 col-form-label">Email:</form:label>
                <form:input path="email" class="form-control" required="required"/>
            </div>
            <div class="mb-3 d-flex align-content-center">
                <form:label path="password" class="col-sm-4 col-form-label">Password:</form:label>
                <form:input path="password" type="password" class="form-control" required="required"/>
            </div>
            <button class="btn btn-success login-button col-sm-4">Login</button>
            <c:if test="${adminDoesntExist}">
                <div class="mt-2 text-center">
                    <form:form method="GET">
                        <a href="/api/v1/register" class="register-link">Register User</a>
                    </form:form>
                </div>
            </c:if>
        </form:form>
    </div>
</div>
</body>
</html>