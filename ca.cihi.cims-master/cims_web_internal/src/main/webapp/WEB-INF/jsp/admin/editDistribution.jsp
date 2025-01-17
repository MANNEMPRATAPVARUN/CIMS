<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript">
    $(document).ready(function(e) {
    	disableRecord();       
    });

	function disableRecord() {
		if ($("select[name=status] :selected").val() == 'D') {
			$("input[name=code]").attr("disabled",true);
			$("input[name=name]").attr("disabled",true);
			$("input[name=reviewgroup]").attr("disabled",true);
			$("input[name=description]").attr("disabled",true);			
			
			$("#codeTag").css("color", "#cccccc");
			$("#nameTag").css("color", "#cccccc");
			$("#reviewgroupTag").css("color", "#cccccc");
			$("#descriptionTag").css("color", "#cccccc");
			
		} else {
			$("input[name=code]").attr("disabled",false);
			$("input[name=name]").attr("disabled",false);
			$("input[name=reviewgroup]").attr("disabled",false);
			$("input[name=description]").attr("disabled",false);
			
			$("#codeTag").css("color", "#000000");
			$("#nameTag").css("color", "#000000");
			$("#reviewgroupTag").css("color", "#000000");
			$("#descriptionTag").css("color", "#000000");			
		}
	}	
</script>	

<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; 
	<fmt:message key="admin.user.distributionlist.title" /> &#62;  
	<c:if test="${distributionListViewBean.actionType == 'E'}">
		<fmt:message key="admin.distribution.edit" /> &#62; DL${distributionListViewBean.code}
	</c:if>
	<c:if test="${distributionListViewBean.actionType == 'A'}">
		<fmt:message key="admin.distribution.add" />
	</c:if>
				
	
</h4>

<div class="content">

<form:form method="POST" modelAttribute="distributionListViewBean" name="editDistribution"> 
<fieldset>
	<legend>
		<form:hidden path="actionType"/>
			<c:if test="${distributionListViewBean.actionType == 'E'}">
				<fmt:message key="admin.distribution.edit" />
			</c:if>
			<c:if test="${distributionListViewBean.actionType == 'A'}">
				<fmt:message key="admin.distribution.add" />
			</c:if>
	</legend> 
 <table border="0">
	<tr>
		<td style="width:200px;"><fmt:message key="admin.distribution.status" /><font color=red>&nbsp;*</font></td>		
		<td>
			<form:select path="status" items="${status}" itemLabel="value" itemValue="key" onChange="disableRecord();" />
			<form:errors path="status" class="errorMsg"/>
		</td>
	</tr>	
	
	<tr id="codeTag">
		<td style="width:200px;"><fmt:message key="admin.distribution.code" /><font color=red>&nbsp;*</font></td>
		<td>DL
			<c:if test="${distributionListViewBean.actionType == 'A'}"> 
				<form:input path="code" size="3" maxlength="3"/>
				&nbsp;(Please enter a unique distribution list code.numeric,max.3)&nbsp;
				<form:errors path="code" class="errorMsg"/>
			</c:if>
			<c:if test="${distributionListViewBean.actionType == 'E'}">
				${distributionListViewBean.code} (numeric,max.3)
		    </c:if>
		   
	    </td>
	</tr>
	
	<tr id="nameTag">
		<td style="width:200px;"><fmt:message key="admin.distribution.name" /><font color=red>&nbsp;*</font></td>
		<td>
			<form:input path="name" size="20" maxlength="20"/>&nbsp;(max.20)&nbsp;
			<form:errors path="name" class="errorMsg"/>
		</td>
	</tr>
	
	<tr id="reviewgroupTag">
		<td style="width:200px;"><fmt:message key="admin.distribution.review.group" /></td>		
		<td>
			<c:if test="${distributionListViewBean.actionType == 'A'}"> 
				<form:select path="reviewgroup" items="${reviewGroup}" itemLabel="value" itemValue="key" />
				<form:errors path="reviewgroup" class="errorMsg"/>
			</c:if>
			<c:if test="${distributionListViewBean.actionType == 'E'}">
				<form:select path="reviewgroup" items="${reviewGroup}" itemLabel="value" itemValue="key" style="disabled:true;" disabled="true"/>
				<form:errors path="reviewgroup" class="errorMsg"/>
		    </c:if>
		</td>
	</tr>
		
	<tr id="descriptionTag">
		<td style="width:200px; vertical-align:top;"><fmt:message key="admin.distribution.description" /></td>
		<td>
			<form:textarea path="description" style="width: 350px; height: 150px; word-wrap: break-word;" maxlength="200"/>&nbsp;(max.200)
		</td>
	</tr>	
</table>

<div style="padding-left: 500px;">
	<img title="Save" src="<c:url value="/img/icons/Save.png"/>" onclick="document.forms['distributionListViewBean'].submit();">
 	&nbsp;&nbsp; 
 	<img title="Cancel" src="<c:url value="/img/icons/Cancel.png"/>" onclick="window.location.href='<c:url value='/admin/distribution.htm'/>'" />
 </div> 

</form:form>
