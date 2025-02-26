<html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body>
	<div class="contentContainer">
		<c:if test="${canAdd eq true}">
			<h3 class="contentHeader">Add New Supplement Book</h3>
			<div class="content">
				<center>
					<a class="add" href="<c:url value="/supplements/children/add.htm?${automaticContextParams}&root=true&id=${param.id}&language=${param.language}"/>"><img title="Add" src="<c:url value="/img/icons/Add.png"/>"/></a>
				</center>
			</div>
		</c:if>
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
