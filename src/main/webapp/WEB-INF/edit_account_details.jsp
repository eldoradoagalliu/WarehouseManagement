<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Details</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/main-style.css">
</head>
<body>
<div class="main-part">
    <div class="edit-user-form">
        <h2 class="text-decoration-underline text-center mb-3 details">Edit User Details</h2>
        <%--@elvariable id="user" type="user"--%>
        <form:form action="/api/v1/account/${user.id}" method="POST" modelAttribute="user">
            <input type="hidden" name="_method" value="put">
            <div class="d-flex align-content-center m-1">
                <form:label path="firstName" class="col-sm-5 col-form-label">First Name:</form:label>
                <form:input path="firstName" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="firstName" class="text-danger"/></div>
            <div class="d-flex align-content-center m-1">
                <form:label path="lastName" class="col-sm-5 col-form-label">Last Name:</form:label>
                <form:input path="lastName" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="lastName" class="text-danger"/></div>
            <div class="d-flex align-content-center m-1">
                <form:label path="email" class="col-sm-5 col-form-label">Email:</form:label>
                <form:input path="email" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="email" class="text-danger"/></div>
            <c:if test="${emailExists != null}">
                <div class="text-danger m-1"><c:out value="${emailExists}"/></div>
            </c:if>
            <button class="btn btn-success edit-button mt-2">Confirm Editing</button>
        </form:form>
    </div>
</div>
</body>
</html>