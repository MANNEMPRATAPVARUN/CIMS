<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>

<script type="text/javascript">	
	function printAlert() {
		window.print();
	}
</script>

<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
		<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link href="css/cims.css" rel="stylesheet">
	<title><fmt:message key="incomplete.report.title" /></title>
	<style type="text/css" media="all">
		img {border : 0;}
		.header  {
		    font-weight: bold;
		    font-size: 1.2em;
		}
		
		.section-header {
		    font-weight: bold;
		    font-size: 1.1em;
		}
		
		.label{
			font-weight: bold;
		}
	</style>
</head>

<h2 style="text-align: center;"><fmt:message key="incomplete.report.title" /></h2>

<div class="content">    
   <table style="margin-bottom: 10px;border: 0; text-align:left; width:100%">
		<tr>
			<td width="25%" class="label">Change Request ID: &nbsp;&nbsp;
			   ${incompleteReport.changeRequest.changeRequestId}</td>
			<td width="25%" class="label">Classification: &nbsp;&nbsp;
			   ${incompleteReport.changeRequest.baseClassification}</td>
		    <td width="25%" class="label">Year:  &nbsp;&nbsp;
			  ${incompleteReport.changeRequest.baseVersionCode}</td>
			 <td width="25%" class="label">Request Category:  &nbsp;&nbsp;
			  ${incompleteReport.changeRequest.category.code}</td> 
		</tr>
		<tr>
			<td colspan="4" class="label"><fmt:message key="manage.change.request.name" />:  ${incompleteReport.changeRequest.name}</td>		
		</tr>
   </table>
   
   <c:choose>     
	   <c:when test="${fn:length(incompleteReport.incomProperties) gt 0}">
			<display:table name="incompleteReport.incomProperties" id="incompleteProperty" requestURI="" class="listTable"  style="width: 100%; margin-top: 0px;">
						   
		        <display:column sortable="true" titleKey="incomplete.report.codeValue" property="codeValue"/>		        
		        <display:column sortable="true" titleKey="incomplete.report.incompleteRationale" property="incompleteRatoinale"/>		       
		    </display:table>
		    <div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">		
				<img id="print" class="viewMode" title="Print" src="img/icons/Print.png" onclick="printAlert();" />			
			</div>
	    </c:when>
	    <c:otherwise>
	       <fmt:message key="incomplete.report.noIncomplete" />
	    </c:otherwise>
    </c:choose>
</div>