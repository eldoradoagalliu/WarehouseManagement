<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isErrorPage="true" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/dashboard-style.css">
</head>
<body class="container">
<h1 class="text-center">My Dashboard</h1>
<div class="d-flex justify-content-end mt-5">
    <form:form action="/order/create" method="POST">
        <button class="btn btn-primary order-button">New Order</button>
    </form:form>
    <form:form action="/" method="GET">
        <button class="btn btn-success order-button">View All Orders</button>
    </form:form>
    <form:form action="/account/${user.id}" method="GET">
        <button class="btn btn-light account-button">My Account</button>
    </form:form>
    <form id="logoutForm" action="/logout" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-dark logout">Logout</button>
    </form>
</div>
<c:if test="${!user.orders.isEmpty()}">
    <div>
        <c:if test="${notCorrectStatus != null}">
            <div class="text-danger m-1"><c:out value="${notCorrectStatus}"/></div>
        </c:if>
        <c:if test="${notCorrectSubmissionStatus != null}">
            <div class="text-danger m-1"><c:out value="${notCorrectSubmissionStatus}"/></div>
        </c:if>
        <div class="d-flex align-items-start justify-content-between">
            <div>
                <h2>Your Orders:</h2>
                <c:choose>
                    <c:when test="${orders == null}">
                        <ol>
                            <c:forEach var="order" items="${user.orders}">
                                <div class="d-flex align-content-start m-0">
                                    <li class="mt-2">
                                        <c:choose>
                                            <c:when test="${order.status == 'CREATED' or order.status == 'DECLINED'}">
                                                <a href="/order/${order.orderNumber}" class="link">Order</a>
                                            </c:when>
                                            <c:otherwise>Order</c:otherwise>
                                        </c:choose>
                                        --> Current Status: <c:out value="${order.formatStatus()}"/>
                                        <c:if test="${order.submittedDate != null && order.status == 'AWAITING_APPROVAL'}">
                                            <div>Submitted on: <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${order.submittedDate}"/></div>
                                        </c:if>
                                    </li>
                                    <c:if test="${order.status == 'CREATED' or order.status == 'DECLINED'}">
                                        <form:form action="/order/submit/${order.orderNumber}" method="POST">
                                            <button class="btn btn-warning order-button">Submit</button>
                                        </form:form>
                                    </c:if>

                                    <c:if test="${!(order.status == 'FULFILLED' or order.status == 'UNDER_DELIVERY' or order.status == 'CANCELLED')}">
                                        <form:form action="/order/cancel/${order.orderNumber}" method="POST">
                                            <input type="hidden" name="_method" value="delete">
                                            <button class="btn btn-danger order-button">Cancel</button>
                                        </form:form>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </ol>
                    </c:when>
                    <c:otherwise>
                        <ol>
                            <c:forEach var="order" items="${orders}">
                                <div class="d-flex align-content-start m-0">
                                    <li class="mt-2">
                                        <c:choose>
                                            <c:when test="${order.status == 'CREATED' or order.status == 'DECLINED'}">
                                                <a href="/order/${order.orderNumber}" class="link">Order</a>
                                            </c:when>
                                            <c:otherwise>Order</c:otherwise>
                                        </c:choose>
                                        --> Current Status: <c:out value="${order.formatStatus()}"/>
                                        <c:if test="${order.submittedDate != null && order.status == 'AWAITING_APPROVAL'}">
                                            <div>Submitted on: <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${order.submittedDate}"/></div>
                                        </c:if>
                                    </li>
                                    <c:if test="${order.status == 'CREATED' or order.status == 'DECLINED'}">
                                        <form:form action="/order/submit/${order.orderNumber}" method="POST">
                                            <button class="btn btn-warning order-button">Submit</button>
                                        </form:form>
                                    </c:if>

                                    <c:if test="${!(order.status == 'FULFILLED' or order.status == 'UNDER_DELIVERY' or order.status == 'CANCELLED')}">
                                        <form:form action="/order/cancel/${order.orderNumber}" method="POST">
                                            <input type="hidden" name="_method" value="delete">
                                            <button class="btn btn-danger order-button">Cancel</button>
                                        </form:form>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </ol>
                    </c:otherwise>
                </c:choose>
            </div>
            <form:form class="d-flex align-items-center" action="/order/client/filter" method="GET">
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
    </div>
</c:if>
</body>
</html>