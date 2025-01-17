
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
		var validationUrl = '<c:url value="/reports/cciNewTableCodes/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeCurrentYear(currentYear){
		var priorYear = parseInt(currentYear)-1;
        $('#priorYear').val(priorYear);
	}
</script>

<h4 class="contentTitle">Reports > CCI New Table Codes w Coding Directives Report</h4> 

<div class="content">


<form:form method="POST" modelAttribute="reportViewBean" id="viewForm">
	<form:hidden path="classification"/>
	<input type="hidden" name="reportType" value="CCINewTableCodesWithCodingDirectives"/>
	<input type="hidden" name="priorYear" id="priorYear"/>
	<ul class="errorMsg" id="errorMessages"></ul>
<fieldset id="reportCriteria">
   <legend>CCI New Table Codes w Coding Directives Report</legend>
   <fieldset>
	 	<table>	
	 	<tr>
		<td width="10%"><span class="required">*</span><label>Year:</label></td>
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