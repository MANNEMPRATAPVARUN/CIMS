<%@ include file="/WEB-INF/jsp/common/include.jsp"%>


<script type="text/javascript">
function submitForm(actionType){
	  document.changeRequest.actionType.value=actionType;
	  document.changeRequest.submit();
}
</script>
<h3 class="contentTitle">Change Request</h3>
<div class="content">

<form:form method="POST" modelAttribute="requestViewBean" name="changeRequest">
<fieldset>
<legend>Create New Change Request</legend> 
<form:hidden path="actionType"/>
	<table border="0">		
		<tr>
			<td class="fieldlabel">Request Name<font color=red>*</font></td>
			<td><form:input path="requestName" size="200" maxlength="200" /></td>
		</tr>
		<tr>
			<td class="fieldlabel" width=20%>Classification Code</td>
			<td><form:select path="classificationCode" items="${classificationList}" itemLabel="value" itemValue="key" /></td>
		</tr>
		<tr>
			<td class="fieldlabel" width=20%>Version Code</td>
			<td><form:select path="versionCode" items="${versionList}" itemLabel="value" itemValue="key" /></td>
		</tr>
		<tr>
			<td class="fieldlabel" width=20%>Language Code</td>
			<td><form:select path="languageCode" items="${languageList}" itemLabel="value" itemValue="key" /></td>
		</tr>
	</table>

<div style="padding-left: 100px; padding-top: 10px;">
 <button class="button" type="submit" ><fmt:message key="common.save" /></button>
 <button class="button" type="reset" ><fmt:message key="common.reset"/></button>
 <button class="button" type="button" onclick="window.location.href='request.htm'"><fmt:message key="common.back" /></button>
 </div>
 </fieldset>
</form:form>
</div>


