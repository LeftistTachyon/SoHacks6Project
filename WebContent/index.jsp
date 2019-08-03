<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="Home page"></c:param>
</c:import>
<div id="banner" class="container">
	<section>
		<img alt="Shinzo" src="images/logo1.png" class="hvr-float" width="412"><br>
		<br> <a href="login.jsp" class="button medium hvr-float">Log in</a>
	</section>
</div>
<%@ include file="footer.jsp"%>