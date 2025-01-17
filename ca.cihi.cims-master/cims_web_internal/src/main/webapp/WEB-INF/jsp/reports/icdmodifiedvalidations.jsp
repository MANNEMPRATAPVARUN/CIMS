
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
 
</style>
<script language="JavaScript" type="text/javascript" >
	
	$(function() {
		var currentYear = $('#currentYear').val();
		changeCurrentYear(currentYear);
		$('#currentYear').change(function () {
            var currentYear = $(this).val();
    		changeCurrentYear(currentYear);
		});
	});
	
	function download(){
		var validationUrl = '<c:url value="/reports/icdModifiedValidations/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeCurrentYear(currentYear){
		$.ajax({
           	url:"${pageContext.request.contextPath}/reports/findPriorYear.htm?classification=ICD-10-CA&currentYear="+currentYear,
           	success: function(data){
           		var optionCode='';
           		for (var i=0; i< data.length; i++){
           			optionCode += '<option value="'+data[i].versionCode+'">'+data[i].versionCode+'</option>';
           		}
           		$('#priorYear').html(optionCode);
           	}
		});
	}
</script>

<h4 class="contentTitle">Reports > ICD Modified Validations Report</h4> 

<div class="content">

<form:form method="POST" modelAttribute="reportViewBean" id="viewForm">
	<form:hidden path="classification"/>
	<input type="hidden" name="reportType" value="ICDModifiedValidations"/>
	<ul class="errorMsg" id="errorMessages"></ul>
<fieldset id="reportCriteria">
   <legend>ICD Modified Validations Report</legend>
   <fieldset>
   		<table>	
		<tr>
		<td width="10%"><span class="required">*</span><label>Current Year:</label></td>		
		<td width="15%">
		<form:select path="currentYear" id="currentYear">
			<c:forEach var="contextIdentifier" items="${openedContextIdentifiers}">
              <form:option value="${contextIdentifier.versionCode}"> ${contextIdentifier.versionCode}</form:option>
           </c:forEach>
	    </form:select>
	    </td>
	    <td width="10%"><span class="required">*</span><label>Prior Year:</label></td>		
		<td width="15%">
		<form:select path="priorYear" id="priorYear">
	    </form:select>
	    </td>
	    <td width="50%">&nbsp;</td>
	    </tr>
	    </table>
	    <div>
		<input class="button" type="button" value="<fmt:message key='reports.view'/>" onclick="download();"/>
		</div>
	</fieldset>
</fieldset>
</form:form>
<div id="loadingInfo" class="info" style="display: none; margin-bottom: 0.1em; width: 800px; padding: 0.2em;">Loading</div>
</div>