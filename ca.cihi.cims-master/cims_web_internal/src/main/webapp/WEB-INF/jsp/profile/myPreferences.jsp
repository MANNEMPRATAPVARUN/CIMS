<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
	var userLang ;
	
	$(document).ready(function(e) {
		userLang = navigator.language || navigator.userLanguage; 
	});

	function save() {	
		var editUrl = "myPreferences.htm?language=" + $('input[name=languagepreference]:checked').val();
		
		if (editUrl.toLowerCase().indexOf("undefined")>= 0) {		
			alert('<fmt:message key="choose.code"/>');
		} else {   
			window.location.href = editUrl;
			alert('<fmt:message key="confirmed.save.success"/>');		
		}
	}
</script>

<h4 class="contentTitle">
	<fmt:message key="user.profile.preferences" /> &#62; 
	${userViewBean.username}
</h4>

<div class="content">

<fieldset>
	<legend>
		<fmt:message key="user.profile.preferences" />
	</legend> 
	
	<form:form method="POST" modelAttribute="userViewBean" name="user">
		<table border="0">
			<tr>
				<td class="fieldlabel" style="width: 100px;">Username:</td>
				<td>${userViewBean.username}</td>			
			</tr>		
			<tr>
				<td class="fieldlabel" style="width: 100px;">First Name:</td>
				<td>${userViewBean.firstname}</td>			
			</tr>		
			<tr>
				<td class="fieldlabel" style="width: 100px;">Last Name:</td>
				<td>${userViewBean.lastname}</td>			
			</tr>		
			<tr>
				<td class="fieldlabel" style="width: 100px;">Email:</td>
				<td>${userViewBean.email}</td>			
			</tr>	
			<tr>
				<td class="fieldlabel" style="width: 100px;">Title:</td>
				<td>${userViewBean.title}</td>			
			</tr>	
			<tr>
				<td class="fieldlabel" style="width: 100px;">Department:</td>
				<td>${userViewBean.department}</td>			
			</tr>	
			<tr>
				<td class="fieldlabel" style="width: 100px;">Status:</td>
				<td>
					${cims:statusConvertFromLetter(userViewBean.status)} 
				</td>			
			</tr>																			
		
			<tr>
				<td class="fieldlabel" style="width: 100px;"><fmt:message key="profile.my.language" /></td>
				<td>
					<form:radiobutton path="languagepreference" value="ENG" label='English'/>&nbsp;
			    	<form:radiobutton path="languagepreference" value="FRA" label="French" />
			    </td>
			</tr>
			<tr>
				<td class="fieldlabel" style="width: 100px;"></td>
				<td>&nbsp;&nbsp;<fmt:message key="user.profile.preferences.note" /></td>
			</tr>			
		</table>
		
	    <div style="padding-left: 400px;">
			<img title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="save();">				
		</div>

	</form:form>
</fieldset>
</div>
