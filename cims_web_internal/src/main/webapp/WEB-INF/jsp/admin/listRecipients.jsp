<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
function confirmRemove() {
	return confirm('<fmt:message key="confirm.remove"/>');
}

function confirmAdd() {
	return confirm('<fmt:message key="confirm.add"/>');
}

</script>

<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62;
	<fmt:message key="admin.user.distributionlist.title" /> &#62;  
	<fmt:message key="admin.recipient.manage.title" /> &#62;
	DL${reipientListViewBean.distribution_code}
</h4>

<div class="content">

<form:form method="POST" modelAttribute="reipientListViewBean" name="listReipient">
	
	<table border="0" cellspacing="10">
		<tr>
			<td>
				<label>Distribution Name:</label>&nbsp;
				<font color=red>${reipientListViewBean.distribution_name}</font>
			</td>			
			<td>
				<label>Distribution List Code:</label>&nbsp;
				<font color=red>DL${reipientListViewBean.distribution_code}</font>
			</td>								
		</tr>
	</table>
	
	<div align="right"> 
		<img title="Done" src="<c:url value="/img/icons/Done.png"/>" onclick="window.location.href='distribution.htm'" />
	</div>
	   
	<fieldset>  
		<legend><fmt:message key="admin.recipient.list" /></legend>
		
		<display:table name="reipientListViewBean.recipients" id="recipients" requestURI="" sort ="list" 
			class="listTable" defaultsort="2" style="width: 100%; table-layout:fixed;">
			
			<display:column property="userId" sortable="true" titleKey="admin.user.id" headerClass="tableHeader sizeEighty" style="text-align:center;"/>
			<display:column property="username" sortable="true" titleKey="admin.user.username" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column property="firstname" sortable="true" titleKey="admin.user.firstname" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column property="lastname" sortable="true" titleKey="admin.user.lastname" headerClass="tableHeader" style="word-wrap:break-word;"/>			
			<display:column property="title" sortable="true" titleKey="admin.user.title" headerClass="tableHeader"/>
			<display:column property="department" sortable="true" titleKey="admin.user.department" headerClass="tableHeader"/>				   
			<display:column property="email" sortable="true" titleKey="admin.user.email" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column sortable="false" titleKey="common.action" headerClass="tableHeader sizeEighty" style="text-align:center;">          
				<a href="recipientList.htm?distributionId=${reipientListViewBean.distribution_id}&amp;user=${recipients.userId}&amp;type=remove" onclick="return confirmRemove();"><fmt:message key="admin.recipient.remove" /></a>
			</display:column>
								
	 	</display:table>	
				  
     </fieldset>

	<fieldset> 
		<legend><fmt:message key="admin.user.list" /></legend>  

		<display:table name="reipientListViewBean.users" id="users" requestURI="" sort ="list" 
			class="listTable" defaultsort="2" style="width: 100%; table-layout:fixed;">
			
			<display:column property="userId" sortable="true" titleKey="admin.user.id" headerClass="tableHeader sizeEighty" style="text-align:center;"/>
			<display:column property="username" sortable="true" titleKey="admin.user.username" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column property="firstname" sortable="true" titleKey="admin.user.firstname" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column property="lastname" sortable="true" titleKey="admin.user.lastname" headerClass="tableHeader" style="word-wrap:break-word;"/>			
			<display:column property="title" sortable="true" titleKey="admin.user.title" headerClass="tableHeader"/>
			<display:column property="department" sortable="true" titleKey="admin.user.department" headerClass="tableHeader"/>   
			<display:column property="email" sortable="true" titleKey="admin.user.email" headerClass="tableHeader" style="word-wrap:break-word;"/>
			<display:column sortable="false" titleKey="common.action" headerClass="tableHeader sizeEighty" style="text-align:center;">          
				<a href="recipientList.htm?distributionId=${reipientListViewBean.distribution_id}&amp;user=${users.userId}&amp;type=add" onclick="return confirmAdd();"><fmt:message key="admin.recipient.add" /></a>
			</display:column>
					
		</display:table>        	

     </fieldset>    
     
</form:form>
</div>


