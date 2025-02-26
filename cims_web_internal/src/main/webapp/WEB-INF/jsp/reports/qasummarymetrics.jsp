
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css">
</style>

<script language="JavaScript" type="text/javascript" >

$(function() {
	//initialize date pickers
	$(".datepicker").datepicker();
	var classification = $('#classification').val();

	changeClassification(classification);	
	
	$("#classification").change( function () {
		classification=$(this).val();
		changeClassification(classification);
		
	});
});
	function download(){
		var validationUrl = '<c:url value="/reports/qaSummaryMetrics/validate.htm"/>';
		var generationUrl = '<c:url value="/reports/generate.htm"/>';
		var checkUrl = '<c:url value="/reports/checkDownloadProgress.htm"/>';
		generateReport(validationUrl, generationUrl,checkUrl);
	}
	
	function changeClassification(classification){
		$.ajax({
            url: "${pageContext.request.contextPath}/reports/getBaseContextYearsAll.htm?baseClassification="+classification,
            success: function(data) {
            	var options = '';
                for (var i = 0; i < data.length; i++) 
                {
                	options += '<option value="' + data[i] +'">' + data[i] + '</option>';
                }
                $('#year').html(options);
            }
          });
	}

</script>

<h4 class="contentTitle">Reports > QA Summary Metrics Report</h4> 

<div class="content">

<form:form modelAttribute="reportViewBean" id="viewForm">
	<ul class="errorMsg" id="errorMessages"></ul>
	<input type="hidden" name="reportType" value="QASummaryMetrics"/>
<fieldset id="reportCriteria">
   <legend>QA Summary Metrics Report</legend>

	<fieldset>	
	    <table>
		    <tr>
			    <td width="10%">
				<label for="classification"><fmt:message key="reports.classification"/>:</label>
				</td>
				<td width="15%">		
				<form:select  path="classification" id="classification">
		            <form:option value=""></form:option>
				    <c:forEach var="baseClassification" items="${baseClassifications}">
		                <form:option value="${baseClassification}"> ${baseClassification}</form:option>
		            </c:forEach>
				</form:select>		
				</td>
				<td width="5%">
				<label for="year"><span class="required">*</span>Year:</label>	
				</td>
				<td width="15%">	
					<form:select path="year" id="year">
			        </form:select>
			    </td>
			        
			    <td width="10%">
			    <label for="language"><fmt:message key="reports.language"/>:</label>
			    </td>
			    <td width="15%">		
					<form:select  path="language" id="language">
					   <form:option value=""></form:option>
					   <form:options items="${languages}" itemLabel="label" itemValue="label"/>
					</form:select>
				</td>
				<td width="30%">&nbsp;</td>
		    </tr>	
	
		    <tr>
			    <td>
				<label for="fromDate"><fmt:message key="reports.fromdate"/>:&nbsp;</label>
				</td>
				<td>
				<form:input id="fromDate" path="fromDate" class="datepicker" cssErrorClass="datepicker fieldError"/>
				</td>
				<td>
				<label for="toDate"><fmt:message key="reports.todate"/>:&nbsp;</label>
				</td>
				<td>
				<form:input id="toDate" path="toDate" class="datepicker" cssErrorClass="datepicker fieldError"/>
				</td>
				<td colspan="2">&nbsp;</td>
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