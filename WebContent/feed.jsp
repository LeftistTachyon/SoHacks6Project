<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="${sessionScope.username}'s Feed"></c:param>
</c:import>

<%@ include file="footer.jsp" %>