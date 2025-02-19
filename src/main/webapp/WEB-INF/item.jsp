<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>New Item</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/item-style.css">
</head>
<body>
<div class="main-part">
    <div class="item-form">
        <h2 class="text-decoration-underline text-center mb-3 form-title">New Item in Inventory</h2>
        <%--@elvariable id="item" type="item"--%>
        <form:form action="/api/v1/item" method="POST" modelAttribute="item">
            <div class="d-flex align-content-center m-1">
                <form:label path="name" class="col-sm-5 col-form-label">Item Name:</form:label>
                <form:input path="name" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="name" class="text-danger"/></div>
            <div class="d-flex align-content-center m-1">
                <form:label path="quantity" class="col-sm-5 col-form-label">Quantity:</form:label>
                <form:input path="quantity" type="number" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="quantity" class="text-danger"/></div>
            <div class="d-flex align-content-center m-1">
                <form:label path="unitPrice" class="col-sm-5 col-form-label">Unit Price:</form:label>
                <form:input path="unitPrice" type="number" step=".01" class="form-control"/>
            </div>
            <div class="d-flex align-content-center m-1"><form:errors path="unitPrice" class="text-danger"/></div>
            <button class="btn btn-success item-button mt-2">Add</button>
        </form:form>
    </div>
</div>
</body>
</html>