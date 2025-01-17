<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">

	$(document).ready(function() {
		showMessageBox();
	});
	
	function showMessageBox() {
		var message = $("#messageBox").text();
		message = $.trim(message);
		
		if (message.length > 0) {			
			$("#messageBox").show();
		} 
	}
	
	function confirmDisable() {
		return confirm('<fmt:message key="confirm.disable"/>');
	}
	
	function editDL() {
		var editUrl = "distribution/edit.htm?distributionId=" + $('input[name=distributionlistid]:checked').val();
		
		if (editUrl.toLowerCase().indexOf("undefined")>= 0) {		
			alert('<fmt:message key="choose.code"/>');
		} else {
			window.location.href = editUrl;
		}
	}
	
	function displayDL() {
		var displayUrl = "recipientList.htm?distributionId=" + $('input[name=distributionlistid]:checked').val();
		if (displayUrl.toLowerCase().indexOf("undefined")>= 0) {		
			alert('<fmt:message key="choose.code"/>');
		} else {
			window.location.href = displayUrl;
		}
	}
</script>

<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.user.distributionlist.title" />
</h4>

<div class="content">

<form:form method="POST" modelAttribute="distributionListViewBean" name="distribution">
<fieldset>
	<legend>
		<fmt:message key="admin.user.distributionlist.title" />
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
  
  
	<ul style="padding-left:.9em; ">
	
		<li style="float: left; list-style-type: none; ">
			<div id="messageBox" class="success" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">
				<ul style="list-style-type: none;">		
					<c:forEach var="message" items="${messageToDisplay}">
						<li><fmt:message key="${message}" /></li>
					</c:forEach>
				</ul>				
			</div>
		</li>	
		<security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR','ROLE_ADMINISTRATOR')">				
			<li style="float: right; top: 0px; border: 0px; background: #ffffff; list-style-type: none;">				
				<img id="edit" title="Edit Distribution List Code" src="<c:url value="/img/icons/EditGrey.png"/>" onclick="editDL();" />
				&nbsp;&nbsp;
				<img id="display" title="Manage Recipients" src="<c:url value="/img/icons/GroupGrey.png"/>" onclick="displayDL();" />
		      	&nbsp;&nbsp;
				<img id="add" title="Add New Distribution List Code" src="<c:url value="/img/icons/Add.png"/>" 
					onclick="window.location.href='distribution/add.htm'">	
			</li>
		</security:authorize>
	</ul>  
	  
	 
<display:table name="distributionBean" id="distributions" requestURI="" defaultsort="5" 
	sort ="list" class="listTable" style="width: 100%; table-layout:fixed;">
	
	<display:column headerClass="tableHeader sizeThirty">
		<input type="radio" name="distributionlistid" value="${distributions.distributionlistid}" />
	</display:column>
			
	<display:column property="code" sortable="true" titleKey="admin.distributionlist.code" headerClass="tableHeader sizeOneFifty"/>
	
	<display:column property="name" sortable="true" titleKey="admin.distributionlist.name" 
		style="word-wrap:break-word;" headerClass="tableHeader sizeTwoFifty"/>
		
	<display:column property="description" sortable="true" titleKey="admin.distributionlist.description" headerClass="tableHeader sizeTwoFifty" 
		style="word-wrap:break-word;"/>		 	   

	<display:column sortable="true" titleKey="admin.distributionlist.status" headerClass="tableHeader sizeEighty" 
		style="text-align:center;">
		
		${cims:statusConvertFromLetter(distributions.status)}
	</display:column>		
	
	<display:column sortable="true" titleKey="admin.distributionlist.reviewgroup" headerClass="tableHeader sizeEighty" 
		style="text-align:center;">
		
		${cims:yesNoConvertFromLetter(distributions.reviewgroup)}
	</display:column>		

</display:table>
					  
</fieldset>
</form:form>
</div>


<script>
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/	
	 $("input[type='radio']").click(function() {
		 $("#edit").attr("src", "<c:url value='/img/icons/Edit.png'/>");
		 $("#display").attr("src", "<c:url value='/img/icons/Group.png'/>");
	 });
	
</script>
