<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Warehouse Inventory</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/item-style.css">
</head>
<body class="container">
<h2 class="text-center my-2 title">Warehouse Item Inventory</h2>
<div>
    <form:form action="/api/v1/account/manage/warehouse" method="GET">
        <button class="btn btn-light"><- Back</button>
    </form:form>
    <form:form action="/api/v1/item" method="GET" class="float-end">
        <button class="btn btn-primary dashboard-button">New Item</button>
    </form:form>
    <h3 class="title">Inventory Items:</h3>
    <c:if test="${!items.isEmpty()}">
        <div>
            <table class="table table-hover table-secondary table-striped text-center">
                <thead>
                <th>Item Name</th>
                <th>Quantity in Inventory</th>
                <th>Unit Price (ALL)</th>
                <th>Actions</th>
                </thead>
                <c:forEach var="item" items="${items}">
                <tbody>
                <tr>
                    <td class="pt-4"><c:out value="${item.name}"/></td>
                    <td class="pt-4"><c:out value="${item.quantity}"/></td>
                    <td class="pt-4"><c:out value="${item.unitPrice}"/></td>
                    <td class="d-flex align-content-center justify-content-center">
                        <form:form action="/api/v1/item/${item.id}" method="GET">
                            <button class="btn btn-secondary">Edit</button>
                        </form:form>
                        <form:form action="/api/v1/item/${item.id}" method="POST">
                            <input type="hidden" name="_method" value="delete">
                            <button class="btn btn-danger">Delete</button>
                        </form:form>
                    </td>
                </tr>
                </tbody>
                </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>