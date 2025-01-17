
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<style type="text/css">
 
</style>
<script language="JavaScript" type="text/javascript" >
	
	function download(){
		var validationUrl = '<c:url value="/reports/icdModifiedValidCode/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
</script>

<h4 class="contentTitle">Reports > ICD Modified Valid Code Report</h4> 

<div class="content">
<form:form method="POST" modelAttribute="reportViewBean" id="viewForm">
	<form:hidden path="classification"/>
	<input type="hidden" name="reportType" value="ICDModifiedValidCode"/>
	<ul class="errorMsg" id="errorMessages"></ul>
<fieldset id="reportCriteria">
   <legend>ICD Modified Valid Code Report</legend>

	<fieldset>	
	 	<table>	
	 	<tr>
		<td width="10%"><span class="required">*</span><label>Current Year:</label></td>
		<td width="15%">		
		<form:select path="currentYear" id="currentYear">
			<c:forEach var="versionYear" items="${versionYears}">
              <form:option value="${versionYear}"> ${versionYear}</form:option>
           </c:forEach>
	    </form:select>
	    </td>
	    
	    <td width="75%">&nbsp;</td>
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