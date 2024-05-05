<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/admin-dashboard-style.css">
</head>
<body class="container">
<h1 class="text-center text-u">Admin Dashboard</h1>
<div class="d-flex justify-content-end mt-5">
    <%--@elvariable id="user" type=""--%>
    <form:form action="/register" method="GET">
        <button class="btn btn-light register">Register User</button>
    </form:form>
    <form id="logoutForm" action="/logout" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-dark logout">Logout</button>
    </form>
</div>
<c:if test="${users.size() > 0}">
    <h2>Users registered in Warehouse Application</h2>
    <c:if test="${successfulChangeMessage != null}">
        <div class="text-success text-center m-1"><c:out value="${successfulChangeMessage}"/></div>
    </c:if>
    <c:if test="${reusedOldPasswordMessage != null}">
        <div class="text-danger text-center m-1"><c:out value="${reusedOldPasswordMessage}"/></div>
    </c:if>
    <table class="table table-hover table-dark table-striped text-center">
        <thead>
        <th>Full name</th>
        <th>Role</th>
        <th>Actions</th>
        </thead>
        <c:forEach var="user" items="${users}">
            <tbody>
            <tr>
                <td><a href="/account/${user.id}" class="link">${user.getFullName()}</a></td>
                <td><c:out value="${user.getUserRole()}"/></td>
                <td class="d-flex align-content-center justify-content-end">
                    <c:if test="${user.newRequestedPassword != null}">
                        <form:form action="/approve/password/change/${user.id}" method="POST">
                            <input type="hidden" name="newPassword" value="${user.newRequestedPassword}">
                            <button class="btn btn-primary">Approve Password Change</button>
                        </form:form>
                    </c:if>
                    <form action="/account/edit/${user.id}">
                        <button class="btn btn-secondary">Edit</button>
                    </form>
                    <form:form action="/account/delete/${user.id}" method="POST">
                        <input type="hidden" name="_method" value="delete">
                        <button class="btn btn-danger">Delete</button>
                    </form:form>

                </td>
            </tr>
            </tbody>
        </c:forEach>
    </table>
</c:if>
</body>
</html>