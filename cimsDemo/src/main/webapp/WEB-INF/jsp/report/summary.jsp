<%@ include file="../common/header.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h3>Report View</h3>
<div class="content">
<table border="1px" cellpadding="8px">
<tr>
<td>Month</td><td>Cost</td>
</tr>

<c:forEach items="${reportData}" var="current">
<tr>
   <td><c:out value="${current.key}" /></td>
   <td><c:out value="${current.value}" /></td>
</tr>
</c:forEach>
</table>

<div style="padding-top: 10px;">
    <button class="button" type="button" onclick="window.location.href='cimsReport.htm?output=excel'">Excel Download</button>
    <button class="button" type="button" onclick="window.location.href='cimsReport.htm?output=pdf'">Pdf Download</button>
</div>

</body>
</html>

<%@ include file="../common/footer.jsp"%>