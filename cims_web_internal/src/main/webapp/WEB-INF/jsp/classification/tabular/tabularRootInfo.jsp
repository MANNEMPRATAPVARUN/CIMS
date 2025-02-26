<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
<div class="contentContainer">
<h3 class="contentHeader">Add New Code Value Information</h3>
<div class="content">
	<c:if test="${canAdd eq true}">
		<center>
			<c:choose>
				<c:when test="${classification eq 'CCI'}">
					<a class="add" href="<c:url value="/tabulars/children/add.htm?id=${param.id}&root=true&type=CCI_SECTION&${automaticContextParams}"/>"><img
						title="Add Section"
						src="<c:url value="/img/icons/Add.png"/>"
					/> </a>
				</c:when>
				<c:otherwise>
					<a class="add" href="<c:url value="/tabulars/children/add.htm?id=${param.id}&root=true&type=ICD_CHAPTER&${automaticContextParams}"/>"><img
						title="Add Chapter"
						src="<c:url value="/img/icons/Add.png"/>"
					/> </a>
				</c:otherwise>
			</c:choose>
		</center>
	</c:if>
</div>
</div>
	<script>
		$(function() {
			$(".add").click(function(event) {
				event.preventDefault();
				ContentPaneController.replaceContent(this.href,null,hideLoading,hideLoading,function(){showLoading("<fmt:message key='progress.msg.load'/>");});
			});
		});
	</script>
</body>
</html>
