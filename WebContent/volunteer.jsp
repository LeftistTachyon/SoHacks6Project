<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="Volunteer for ${param.name}"></c:param>
</c:import>
<div class="main">
	<div class="header container">
		<h2 style="color: white;">Volunteer for ${param.name}</h2>
	</div>
	<hr>
	<div class="container">
		<div class="row">
			<c:set var="flag" value="${true}"></c:set>
			<c:forEach var="o" items="${orgList}">
				<c:if test="${flag && o.name.equals(param.name)}">
					<c:set var="org" value="${o}"></c:set>
					<c:set var="flag" value="${false}"></c:set>
				</c:if>
			</c:forEach>
			<div class="8u 12u(small)" style="background: white; padding: 13px;">
				<c:if test="${org != null}">
					<br>${org.toString()}
				</c:if>
				<c:if test="${org == null}">
					<p>The ${org.name} organization does not exist.</p>
				</c:if>
			</div>
			<div class="4u$ 12u(small)" style="color: white;">
				Address: <%= ((int) (Math.random() * 99)) * 100 %> Pinewood Avenue
				<br><br>
				Volunteer times:
				<ul>
					<li>11:00 AM</li>
					<li>1:00 PM</li>
				</ul>
			</div>
		</div>
	</div>
</div>
<%@ include file="footer.jsp" %>