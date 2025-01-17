<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
    $(document).ready(function(e) {
    	disableRecord();       
    });


	function disableRecord() {
		if ($("select[name=status] :selected").val() == 'D') {
			$("input[name=email]").attr("disabled",true);
			$("input[name=firstname]").attr("disabled",true);
			$("input[name=lastname]").attr("disabled",true);
			$("input[name=username]").attr("disabled",true);
			$("input[name=title]").attr("disabled",true);
			$("input[name=department]").attr("disabled",true);
			$("#emailTag").css("color", "#cccccc");
			$("#firsNameTag").css("color", "#cccccc");
			$("#lastNameTag").css("color", "#cccccc");
			$("#userNameTag").css("color", "#cccccc");
			$("#titleTag").css("color", "#cccccc");
			$("#departmentTag").css("color", "#cccccc");
		} else {
			$("input[name=email]").attr("disabled",false);
			$("input[name=firstname]").attr("disabled",false);
			$("input[name=lastname]").attr("disabled",false);
			$("input[name=username]").attr("disabled",false);
			$("input[name=title]").attr("disabled",false);
			$("input[name=department]").attr("disabled",false);
			$("#emailTag").css("color", "#000000");
			$("#firsNameTag").css("color", "#000000");
			$("#lastNameTag").css("color", "#000000");
			$("#userNameTag").css("color", "#000000");
			$("#titleTag").css("color", "#000000");
			$("#departmentTag").css("color", "#000000");
		}
	}	

	function autoFilled() {		
		
		if ($("select[name=userType] :selected").val() == 'I') {			
			if ($("input[name=firstname]").val() != null && $("input[name=lastname]").val() != null) {
				var fill = $("input[name=firstname]").val().substring(0, 1) + $("input[name=lastname]").val();
				$("input[name=username]").val(fill);
				$("input[name=email]").val($("input[name=username]").val() + '@cihi.ca');
			} else {				
			}
		}
	}	
	
</script>


<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.user.edit.title" />
	 &#62; 
	 
	<c:if test="${userViewBean.actionType == 'E'}">
		<fmt:message key="admin.user.edit" /> &#62; ${userViewBean.username}
	</c:if>
		
	<c:if test="${userViewBean.actionType == 'A'}">
		<fmt:message key="admin.user.add" />
	</c:if>	
</h4>

<div class="content">

<form:form method="POST" modelAttribute="userViewBean" name="editUser">
 
<fieldset>
	<legend>
		<form:hidden path="actionType"/>
		
		<c:if test="${userViewBean.actionType == 'E'}">
			<fmt:message key="admin.user.edit" />
		</c:if>
		
		<c:if test="${userViewBean.actionType == 'A'}">
			<fmt:message key="admin.user.add" />
		</c:if>
	</legend> 
	
 <table border="0">
	
	<tr id="userNameTag">
		<td class="fieldlabel"><fmt:message key="admin.user.username" /><font color=red>&nbsp;*</font></td>
		<td>
			<c:if test="${userViewBean.actionType == 'A'}">			
				<form:input path="username" size="30" maxlength="30"/>
				&nbsp;&nbsp;(max.30)&nbsp;&nbsp;
				<fmt:message key="admin.user.name.note" />
			</c:if>
			<c:if test="${userViewBean.actionType == 'E'}">
				${userViewBean.username}
			</c:if>
			<form:errors path="username" class="errorMsg"/>
		</td>
	</tr>
	
	<tr id="firsNameTag">
		<td class="fieldlabel"><fmt:message key="admin.user.firstname" /><font color=red>&nbsp;*</font></td>
		<td>
			<form:input path="firstname" size="50" maxlength="100"/>
			&nbsp;&nbsp;(max.100)&nbsp;&nbsp;
			<form:errors path="firstname" class="errorMsg"/>
		</td>
	</tr>
	<tr id="lastNameTag">
		<td class="fieldlabel"><fmt:message key="admin.user.lastname" /><font color=red>&nbsp;*</font></td>
		<td>
			<form:input path="lastname" size="50" maxlength="100"/>
			&nbsp;&nbsp;(max.100)&nbsp;&nbsp;
			<form:errors path="lastname" class="errorMsg"/>
		</td>
	</tr>	
	
	<tr id ='emailTag'>
		<td class="fieldlabel"><fmt:message key="admin.user.email" /><font color=red>&nbsp;*</font></td>
		<td>
			<form:input path="email" size="50" maxlength="100"/>
			&nbsp;&nbsp;(max.100)&nbsp;&nbsp;
			<form:errors path="email" class="errorMsg"></form:errors>
		</td>
	</tr>

	<tr id = 'titleTag'>
		<td class="fieldlabel"><fmt:message key="admin.user.title" /></td>
		<td><form:input path="title" size="50" maxlength="100"/></td>
	</tr>	
	
	<tr id = 'departmentTag'>
		<td class="fieldlabel"><fmt:message key="admin.user.department" /></td>
		<td><form:input path="department" size="50" maxlength="100"/></td>
	</tr>
		
	<tr>
		<td class="fieldlabel"><fmt:message key="admin.user.status" /><font color=red>&nbsp;*</font></td>		
		<td><form:select path="status" items="${userStatus}" itemLabel="value" itemValue="key" onChange="disableRecord();"/></td>
	</tr>
		
</table>

<div style="padding-left: 500px;">
	<img title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="document.forms['userViewBean'].submit();">
 	&nbsp;&nbsp; 
 	<img title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="window.location.href='<c:url value='/admin/user.htm'/>'" />
</div>

</fieldset>
</form:form>
</div>

