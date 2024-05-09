<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/dashboard-style.css">
</head>
<body class="container">
<h2 class="text-center my-2 title">Order Details</h2>
<div>
    <div class="text-center title">
        <h4 class="title">Current Status: <c:out value="${order.formatStatus()}"/></h4>
        <c:if test="${order.declineReason != null}">
            <div class="text-light">The declined reason: <c:out value="${order.declineReason}"/></div>
        </c:if>
    </div>
    <form:form action="/" method="GET">
        <button class="btn btn-light"><- Back</button>
    </form:form>
    <c:if test="${!orderItems.isEmpty()}">
        <h3 class="text-decoration-underline text-center mb-0">Current Order Items:</h3>
        <div class="d-flex justify-content-center">
            <ol>
                <c:forEach var="orderItem" items="${orderItems}">
                    <div class="d-flex">
                        <li class="mt-1">
                            <c:out value="${orderItem.item.name}"/> -- <c:out value="${orderItem.quantity}"/>x
                        </li>
                        <c:if test="${!order.isStatusUnderDeliveryOrFulfilled()}">
                            <form:form action="/order/remove/item/${orderItem.item.id}" method="POST">
                                <input type="hidden" name="_method" value="delete">
                                <input type="hidden" name="orderNumber" value="${order.orderNumber}">
                                <button class="btn btn-danger">Remove</button>
                            </form:form>
                        </c:if>
                    </div>
                </c:forEach>
            </ol>
        </div>
    </c:if>
    <c:if test="${lowQuantity != null}">
        <h5 class="text-danger text-center m-1"><c:out value="${lowQuantity}"/></h5>
    </c:if>
    <c:if test="${!order.isStatusUnderDeliveryOrFulfilled()}">
        <h3>Add Items to the Order:</h3>
        <c:if test="${!items.isEmpty()}">
            <div>
                <table class="table table-hover table-secondary table-striped text-center">
                    <thead>
                    <th>Item Name</th>
                    <th>Unit Price (ALL)</th>
                    <th>Actions</th>
                    </thead>
                    <c:forEach var="item" items="${items}">
                    <tbody>
                    <tr>
                        <td class="pt-4"><c:out value="${item.name}"/></td>
                        <td class="pt-4"><c:out value="${item.unitPrice}"/></td>
                        <td class="d-flex justify-content-center">
                            <form:form action="/order/add/item/${item.id}" method="POST" class="mt-1">
                                <input type="hidden" name="orderNumber" value="${order.orderNumber}">
                                <button class="btn btn-success">Add 1</button>
                            </form:form>
                            <form:form action="/modify/item/quantity/${item.id}" method="POST">
                                <input type="number" name="quantity" class="quantity-input" min="2"
                                       placeholder="Item quantity" required="required">
                                <input type="hidden" name="orderNumber" value="${order.orderNumber}">
                                <button class="btn btn-success">Add</button>
                            </form:form>
                        </td>
                    </tr>
                    </tbody>
                    </c:forEach>
            </div>
        </c:if>
    </c:if>
</div>
</body>
</html>