<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Truck</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/truck-style.css">
</head>
<body>
<div class="main-part">
    <div class="edit-truck-form">
        <h2 class="text-decoration-underline text-center mb-3 form-title">Edit Truck Details</h2>
        <%--@elvariable id="truck" type="truck"--%>
        <form:form action="/truck/edit/${truck.id}" method="POST" modelAttribute="truck">
            <input type="hidden" name="_method" value="put">
            <div class="d-flex align-content-center m-1">
                <form:label path="chassisNumber" class="col-sm-5 col-form-label">Chassis Number:</form:label>
                <form:input path="chassisNumber" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="chassisNumber" class="text-danger"/></div>
            <c:if test="${chassisNumberExists != null}">
                <div class="text-danger m-1"><c:out value="${chassisNumberExists}"/></div>
            </c:if>
            <div class="d-flex align-content-center m-1">
                <form:label path="licensePlate" class="col-sm-5 col-form-label">License Plate:</form:label>
                <form:input path="licensePlate" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="licensePlate" class="text-danger"/></div>
            <button class="btn btn-success truck-button mt-2">Confirm Editing</button>
        </form:form>
    </div>
</div>
</body>
</html>