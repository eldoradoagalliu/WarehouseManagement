<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Details</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/main-style.css">
</head>
<body>
<div class="main-part">
    <div class="register-form">
        <h2 class="text-decoration-underline text-center mb-3 details">User Details</h2>
        <div>Name: <c:out value="${user.getFullName()}"/></div>
        <div>Email: <c:out value="${user.email}"/></div>
        <div>Account created on: <fmt:formatDate pattern="HH:mm - d MMMM yyyy" value="${user.createdAt}"/></div>
        <form:form action="/account/request/password/change/${user.id}" method="POST">
            <div class="d-flex align-content-center m-1">
                <label class="col-sm-5 col-form-label">New password:</label>
                <input name="newPassword" class="form-control" required="required"/>
            </div>
            <c:if test="${passwordMatches != null}">
                <div class="text-danger m-1"><c:out value="${passwordMatches}"/></div>
            </c:if>
            <button class="btn btn-primary register-button mt-2">Request</button>
        </form:form>
    </div>
</div>
</body>
</html>