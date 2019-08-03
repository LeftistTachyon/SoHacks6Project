<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:import url="/header.jsp">
	<c:param name="title" value="Explorer"></c:param>
</c:import>
<ul id="infiniteList">
</ul>
<script>
var cnt = 0;

$(window).scroll(function(){
    if ($(window).scrollTop() == $(document).height()-$(window).height()){
       $.ajax({
          url: 'site?action=nextExplorer&num=' + (cnt++),
          success: function (data) { $('#infiniteList').append(data); },
          dataType: 'html',
          method: 'POST'
       });
    }
});
</script>
<%@ include file="footer.jsp" %>