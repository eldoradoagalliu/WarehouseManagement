<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Truck Management</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/manager-dashboard-style.css">
</head>
<body class="container">
<div class="truck-form">
    <form:form action="/api/v1/account/manage/warehouse" method="GET" css="float-start">
        <button class="btn btn-secondary dashboard-button">Return to Dashboard</button>
    </form:form>
    <h2 class="text-center mt-3">New Truck</h2>
    <%--@elvariable id="truck" type=""--%>
    <form:form action="/api/v1/truck" method="POST" modelAttribute="truck">
        <div class="d-flex align-content-start justify-content-evenly m-1">
            <div class="d-flex align-content-center">
                <form:label path="chassisNumber" class="col-sm-5 col-form-label">Chassis Number:</form:label>
                <form:input path="chassisNumber" type="text" class="form-control truck-input"/>
            </div>
            <div class="d-flex align-content-center">
                <form:label path="licensePlate" class="col-sm-4 col-form-label">License Plate:</form:label>
                <form:input path="licensePlate" type="text" class="form-control truck-input"/>
            </div>
            <button class="btn btn-primary truck-button mt-2">Add</button>
        </div>
        <c:if test="${chassisNumberExists != null}">
            <div class="text-center text-danger m-1"><c:out value="${chassisNumberExists}"/></div>
        </c:if>
        <div class="d-flex align-content-center justify-content-evenly m-1">
            <form:errors path="chassisNumber" class="text-danger"/>
            <form:errors path="licensePlate" class="text-danger"/>
        </div>
    </form:form>
    <c:if test="${!trucks.isEmpty()}">
        <h4 class="pb-0">Registered Trucks:</h4>
        <ul>
            <c:forEach var="truck" items="${trucks}">
                <div class="d-flex align-content-start m-0">
                    <li class="mt-1">
                        Truck with Chassis number <c:out value="${truck.chassisNumber}"/> and License plate
                        <c:out value="${truck.licensePlate}"/>
                    </li>
                    <form:form action="/api/v1/truck/${truck.id}" method="GET">
                        <button class="btn btn-secondary">Edit</button>
                    </form:form>
                    <form:form action="/api/v1/truck/${truck.id}" method="POST">
                        <input type="hidden" name="_method" value="delete">
                        <button class="btn btn-danger">Delete</button>
                    </form:form>
                </div>
            </c:forEach>
        </ul>
    </c:if>
</div>
</body>
</html>