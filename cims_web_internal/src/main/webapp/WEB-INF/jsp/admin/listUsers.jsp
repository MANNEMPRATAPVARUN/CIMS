<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

	$(document).ready(function(e) {
		var userLang = navigator.language || navigator.userLanguage; 
		//console.log(userLang);
		modifyPageLinks();
	});

	function confirmDeletion() {
		return confirm('<fmt:message key="confirm.delete"/>');
	}

	function confirmDisable(){
		return confirm('<fmt:message key="confirm.disable"/>');
	}


	function editUser() {
		var editUrl = "user/edit.htm?userId=" + $('input[name=userId]:checked').val();
		if (editUrl.toLowerCase().indexOf("undefined")>= 0) {
			//alert(editUrl);
			alert('<fmt:message key="choose.user"/>');
		} else {
			window.location.href = editUrl;
		}
	}
</script>

<h4 class="contentTitle"><fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.user.manage.title" /></h4>

<div class="content">

<form:form method="POST" modelAttribute="userViewBean" name="listUser">

<fieldset style="height:500px;">
	<legend>
		<fmt:message key="admin.user.manage.title" />
	</legend> 
	
	<spring:bind path="*">
		<div class="errorMsg">
			<ul>
				<c:forEach var="error" items="${status.errorMessages}">
					<li><c:out value="${error}" escapeXml="false" /></li>
				</c:forEach>
			</ul>
		</div>
	</spring:bind>
	

	<security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
		<div align="right">
			<img id="add" title="Add User" src="<c:url value="/img/icons/Add.png"/>" onclick="window.location.href='user/add.htm'">
			&nbsp;&nbsp; 
			<img id="edit" title="Edit User" src="<c:url value="/img/icons/EditGrey.png"/>" onclick="editUser();" />
		</div>
	</security:authorize>
	        
	<display:table name="userBeans" id="users" defaultsort="3" requestURI="" pagesize="${pageSize}"
		class="listTable" style="width: 100%; table-layout:fixed;" sort="list">
					
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="paging.banner.group_size" value="10" />
			
		<display:column headerClass="tableHeader sizeThirty">
			<input type="radio" name="userId" value="${users.userId}" />
		</display:column>
		
		<display:column sortable="true" titleKey="admin.user.username" headerClass="tableHeader sizeOneFifty" style="word-wrap:break-word;">
			${users.username}
		</display:column>
		
		<display:column sortable="true" titleKey="admin.user.firstname" headerClass="tableHeader" style="word-wrap:break-word;">
			${users.firstname}
		</display:column>
		
		<display:column sortable="true" titleKey="admin.user.lastname" headerClass="tableHeader" style="word-wrap:break-word;">
			${users.lastname}
		</display:column>
		
		<display:column sortable="true" titleKey="admin.user.email" headerClass="tableHeader sizeTwoFifty" style="word-wrap:break-word;">		   
			${users.email}
		</display:column>
		
		<display:column sortable="true" titleKey="admin.user.status" headerClass="tableHeader sizeEighty" style="text-align:center;">
			${cims:statusConvertFromLetter(users.status)} 
		</display:column>
		
	</display:table>	
			  
</fieldset>
	
	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
</form:form>
</div>

<script>
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/	
	 $("input[type='radio']").click(function() {
		 $("#edit").attr("src", "<c:url value='/img/icons/Edit.png'/>");
	 });
	
</script>
