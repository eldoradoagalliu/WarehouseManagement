<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manager Dashboard</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/manager-dashboard-style.css">
</head>
<body class="container">
<h1 class="text-center title">Manager Dashboard</h1>
<div class="d-flex justify-content-end mt-5">
    <form:form action="/api/v1/item/inventory" method="GET">
        <button class="btn btn-success dashboard-button">View Inventory Items</button>
    </form:form>
    <form:form action="/api/v1/truck" method="GET">
        <button class="btn btn-primary dashboard-button">New Truck</button>
    </form:form>
    <form:form action="/api/v1/account/${currentUser.id}" method="GET">
        <button class="btn btn-light account-button">My Account</button>
    </form:form>
    <form:form action="/api/v1/logout" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-dark logout">Logout</button>
    </form:form>
</div>
<div>
    <div class="d-flex align-items-start justify-content-between">
        <h2>All Client Orders:</h2>
        <form:form class="d-flex align-items-center" action="/api/v1/order/filter" method="GET">
            <label class="col-sm-3 filter p-0">Filter By:</label>
            <select class="form-select option" name="status" type="hidden">
                <option value="CREATED">Created</option>
                <option value="AWAITING_APPROVAL">Awaiting Approval</option>
                <option value="APPROVED">Approved</option>
                <option value="DECLINED">Declined</option>
                <option value="UNDER_DELIVERY">Under Delivery</option>
                <option value="FULFILLED">Fulfilled</option>
                <option value="CANCELLED">Cancelled</option>
            </select>
            <button class="btn btn-light mx-2 filter-button">Filter</button>
        </form:form>
    </div>
    <c:if test="${!orders.isEmpty()}">
        <div>
            <c:if test="${busyTruck != null}">
                <div class="text-center text-danger m-1"><c:out value="${busyTruck}"/></div>
            </c:if>
            <c:if test="${offDay != null}">
                <div class="text-center text-danger m-1"><c:out value="${offDay}"/></div>
            </c:if>
            <c:if test="${maxTruckItemAmount != null}">
                <div class="text-center text-danger m-1"><c:out value="${maxTruckItemAmount}"/></div>
            </c:if>
            <ol>
                <c:forEach var="order" items="${orders}">
                    <div class="d-flex align-content-start m-0">
                        <li class="mt-2">
                            <form:form method="GET">
                                <a href="/api/v1/order/${order.orderNumber}" class="link">Order</a>
                            </form:form>
                            --> Current Status: <c:out value="${order.formatStatus()}"/>
                            <c:if test="${order.isStatusAwaitingApproval()}">
                                <div>
                                    Submitted on: <fmt:formatDate pattern="dd/MM/yyyy HH:mm"
                                                                  value="${order.submittedDate}"/>
                                </div>
                            </c:if>
                            <c:if test="${order.status == 'APPROVED'}">
                                <form:form action="/api/v1/truck/schedule/delivery/${order.orderNumber}" method="POST">
                                    <label>--> Deliver on: <input type="date" name="date" min="${now}"
                                                                  required="required"> with Truck:</label>
                                    <label>
                                        <select class="form-select option" name="licensePlate" type="hidden">
                                            <c:forEach var="truck" items="${trucks}">
                                                <option><c:out value="${truck.licensePlate}"/></option>
                                            </c:forEach>
                                        </select>
                                    </label>
                                    <button class="btn btn-warning dashboard-button">Schedule Delivery</button>
                                </form:form>
                            </c:if>
                        </li>
                        <c:if test="${order.status == 'AWAITING_APPROVAL'}">
                            <form:form action="/api/v1/order/approve/${order.orderNumber}" method="POST">
                                <button class="btn btn-info dashboard-button mt-1">Approve</button>
                            </form:form>
                            <form:form action="/api/v1/order/decline/${order.orderNumber}" method="POST">
                                <input type="text" name="reason" class="reason-input" placeholder="Decline reason"
                                       required="required">
                                <button class="btn btn-danger dashboard-button mb-2">Decline</button>
                            </form:form>
                        </c:if>
                        <c:if test="${order.isDeliveryDone() && order.status != 'FULFILLED'}">
                            <form:form action="/api/v1/order/fulfill/${order.orderNumber}" method="POST">
                                <button class="btn btn-success dashboard-button">Fulfill Order</button>
                            </form:form>
                        </c:if>
                    </div>
                </c:forEach>
            </ol>
        </div>
    </c:if>
</div>
</body>
</html>