<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Created by IntelliJ IDEA. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title></title>
  </head>
  <body>
    <h2>index.jsp loaded from web/view  with Spring</h2>
    <c:forEach items="${users}" var="user">
      <h2>${user}</h2><br/>
    </c:forEach>
  <p><img src="resources/linux.png"/> </p>
  </body>
</html>