<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300&display=swap">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/admin-dashboard-style.css">
</head>
<body class="container">
<h1 class="text-center">Admin Dashboard</h1>
<div class="d-flex justify-content-end mt-3">
    <form:form action="/api/v1/register" method="GET">
        <button class="btn btn-light register">Register User</button>
    </form:form>
    <form:form action="/api/v1/logout" method="POST">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-dark logout">Logout</button>
    </form:form>
</div>
<c:if test="${!users.isEmpty()}">
    <h3>Users registered in Warehouse Application</h3>
    <c:if test="${successfulPasswordChange != null}">
        <div class="text-success text-center m-1"><c:out value="${successfulPasswordChange}"/></div>
    </c:if>
    <c:if test="${reusedOldPassword != null}">
        <div class="text-danger text-center m-1"><c:out value="${reusedOldPassword}"/></div>
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
                <td class="pt-4"><a href="/api/v1/account/${user.id}" class="link">${user.getFullName()}</a></td>
                <td class="pt-4"><c:out value="${user.getFormattedUserRole()}"/></td>
                <td class="d-flex align-content-center justify-content-end">
                    <c:if test="${user.newRequestedPassword != null}">
                        <form:form action="/api/v1/account/approve/password/change/${user.id}" method="POST">
                            <input type="hidden" name="newPassword" value="${user.newRequestedPassword}">
                            <button class="btn btn-info">Approve Password Change</button>
                        </form:form>
                    </c:if>
                    <form:form action="/api/v1/account/${user.id}/edit" method="GET">
                        <button class="btn btn-dark">Edit Account</button>
                    </form:form>
                    <form:form action="/api/v1/account/${user.id}" method="POST">
                        <input type="hidden" name="_method" value="delete">
                        <button class="btn btn-danger">Delete Account</button>
                    </form:form>
                </td>
            </tr>
            </tbody>
        </c:forEach>
    </table>
</c:if>
</body>
</html>