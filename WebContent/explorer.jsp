<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="Explorer"></c:param>
</c:import>
<c:set var="condition" value="${sessionScope.orgList != null || sessionScope.orgList.isEmpty()}"></c:set>
<c:if test="${condition}">
	<ul>
		<c:forEach var="org" items="${sessionScope.orgList}">
			${org.toString()}<br>
		</c:forEach>
		<c:remove var="orgList" scope="session" />
	</ul>
</c:if>
<c:if test="${not condition}">
	<p style="color: white; text-align: center;">There are no
		organizations to display.</p>
</c:if>
<%@ include file="footer.jsp"%>