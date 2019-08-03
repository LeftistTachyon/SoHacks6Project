<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="Login"></c:param>
</c:import>
<div class="main" style="color: white;">
	<div class="header">
		<h2 style="text-align: center;">Login</h2>
	</div>
	<br>
	<div class="container">
		<form action="${pageContext.request.contextPath}/site" method="POST">
			<input type="hidden" name="action" value="login">
			Username: <input type="text" name="username" placeholder="Enter username"><br>
			Password: <input type="password" name="password" placeholder="Enter password"><br>
			<input type="submit" value="Login">
		</form>
		<br>
	</div>
</div>
<%@ include file="footer.jsp" %>