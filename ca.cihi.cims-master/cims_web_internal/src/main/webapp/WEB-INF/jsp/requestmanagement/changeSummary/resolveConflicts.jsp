<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<jsp:include page="../../common/common-header.jsp"/>
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
<html>

<script type="text/javascript">	
 
$('#crLastUpdatedTime',window.opener.document.body).text('${changeRequest.lockTimestamp}');
var errorCallback = function(data) {
	hideLoading();
	var responseData = data.responseText;
	if(responseData != "undefined" && responseData != null) {
		$('#concurrentError').text(responseData).show();
	}
};
var replaceContent = function(data) {
  document.open();
  document.write(data);
  document.close();
};

function submitForm(){
	 var $form = $("#resolveConflict");
	    $.ajax({
	        url: $form.attr("action"),
	        data: $form.serialize(),
	        type: "POST",
	        dataType: "html",
	        success: replaceContent,
	        error: errorCallback
	    });
}

function resolveConflicts(){
   var url = turnOnTimestampCheck("<c:url value='/submitResolveConflicts.htm'/>", window.opener.document.body);
	$("#resolveConflict")[0].action=url;
	submitForm();
}
  
  function resetResolveConflictsPage(){
      $("#resolveConflict")[0].action="<c:url value='/resolveConflicts.htm?changeRequestId=${changeRequest.changeRequestId}'/>";
	  $("#resolveConflict")[0].submit();
  }
  
  
   function popupValidationProposedAndConflict(url){
		newWindow = window.open(url, 'ProposedAndConflict', 'top=500, left=400,height=200,width=850,resizable'); 
	}
   
	function popupProposedChangeXmlProperty(url){
		newWindow = window.open(url, 'ProposedChangeProposed', 'top=300, left=300,height=400,width=850,scrollbars=1,resizable');  	
	}
	function popupConflictXmlProperty(url){
		newWindow = window.open(url, 'ProposedChangeConflict', 'top=300, left=300,height=400,width=850,scrollbars=1,resizable'); 	
	}
   
   
   
   
   
   function printAlert() {
		window.print();
  }
</script>



<h2 style="text-align: center;">Resolve Conflicts</h2>

<div class="content">    
   <table style="margin-bottom: 10px;border: 0; text-align:left; width:100%">
		<tr>
			<td width="25%" class="label">Change Request ID: &nbsp;&nbsp;
			   ${changeRequest.changeRequestId}</td>
			<td width="25%" class="label">Classification: &nbsp;&nbsp;
			   ${changeRequest.baseClassification}</td>
		    <td width="25%" class="label">Year:  &nbsp;&nbsp;
			  ${changeRequest.baseVersionCode}</td>
			 <td width="25%" class="label">Request Category:  &nbsp;&nbsp;
			  ${changeRequest.category.code}</td> 
		</tr>
		<tr>
			<td colspan="4" class="label"><fmt:message key="manage.change.request.name" />:  ${changeRequest.name}</td>		
		</tr>
   </table>
   
   <div id="concurrentError" class="error" style="display:none;">
   </div>
    
  <c:choose>     
	<c:when test="${fn:length(resolveConflict.conflictChanges) gt 0}">
    <form:form id="resolveConflict"  modelAttribute="resolveConflict" method="post" >
          <form:hidden path="changeRequestId" value="${changeRequest.changeRequestId}"/>
          <form:hidden path="currentContextId" value="${resolveConflict.currentContextId}"/>
          
    	<table id="proposedConflictTabularChanges" style="width: 100%; margin-top: 20px;" class="listTable">
			<thead>
				<tr>
				    <th class="tableHeader" >Code Value</th>	
					<th class="tableHeader" ><fmt:message key="change.summary.fieldName" /></th>								
					<th class="tableHeader" ><fmt:message key="change.summary.proposedValue" /></th>
					<th class="tableHeader" >Realized Conflict Value</th>
					<th class="tableHeader" >Realizing Change Request</th>
					<th class="tableHeader" >Resolve Conflict</th>						
				</tr>
			</thead>
			<tbody>
	          <c:forEach var="conflictChange" items="${resolveConflict.conflictChanges}" varStatus="status">
			   <tr class="${status.index%2==0 ? 'even' : 'odd'}">
			 	 <form:hidden path="conflictChanges[${status.index}].elementId" value="${conflictChange.elementId}"/>
			 	 <form:hidden path="conflictChanges[${status.index}].elementVersionId" value="${conflictChange.elementVersionId}"/>
			 	 <form:hidden path="conflictChanges[${status.index}].conflictRealizedByContext.contextId" value="${conflictChange.conflictRealizedByContext.contextId}"/>
			 	 <form:hidden path="conflictChanges[${status.index}].changeType" value="${conflictChange.changeType}"/>
			 
			 	 
			 	 <td>${conflictChange.code}</td>
			 	 <td>${conflictChange.fieldName}</td>
			 	 <c:choose>
				     <c:when test="${conflictChange.changeType == 'Tabular'}">		
			 	         <c:choose>
				          <c:when test="${conflictChange.tableName=='XMLPropertyVersion'}">		
			 	            <td><a href="javascript:popupProposedChangeXmlProperty('viewXmlProperty.htm?xmlPropertyId=${conflictChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${conflictChange.fieldName}&code=${conflictChange.code}');"><fmt:message key="change.summary.proposed" />&nbsp;${conflictChange.fieldName}</a></td>
							<td><a href="javascript:popupConflictXmlProperty('viewXmlProperty.htm?xmlPropertyId=${conflictChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${conflictChange.fieldName}&code=${conflictChange.code}');"><fmt:message key="change.summary.conflict" />&nbsp;${conflictChange.fieldName}</a></td>
																			
			 	          </c:when>
			 	          <c:when test="${conflictChange.tableName=='HTMLPropertyVersion'}">		
			 	          	<td><a href="javascript:popupProposedChangeXmlProperty('viewHtmlProperty.htm?htmlPropertyId=${conflictChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${conflictChange.fieldName}&code=${conflictChange.code}');"><fmt:message key="change.summary.proposed" />&nbsp; ${conflictChange.fieldName}</a></td>
							<td><a href="javascript:popupConflictXmlProperty('viewHtmlProperty.htm?htmlPropertyId=${conflictChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${conflictChange.fieldName}&code=${conflictChange.code}');"><fmt:message key="change.summary.conflict" />&nbsp; ${conflictChange.fieldName}</a></td>	
			 	          </c:when>
			 	         
			 	          <c:otherwise>  
			 	             <td>${conflictChange.proposedValue}</td>
				          
				             <td>
				                 <c:choose>
				                    <c:when test="${conflictChange.conflictValue=='no_value'}">
									 		<fmt:message key="change.summary.noValue"/>
									</c:when>
									<c:otherwise>				
				                        ${conflictChange.conflictValue}
				                    </c:otherwise>
				                  </c:choose>      
				             </td>	
				          
				          </c:otherwise>
				         </c:choose> 
				     
				     </c:when>
				     
				     
				     
				     <c:when test="${conflictChange.changeType == 'Validation'}">
				        <td colspan="2" style="text-align:center">
				        <a href="javascript:popupValidationProposedAndConflict('showProposedAndConflict.htm?changeRequestId=${changeRequest.changeRequestId}&validationId=${conflictChange.validationId}&elementVersionId=${conflictChange.elementVersionId}');" >Proposed and Conflict Validation </a>
				        </td>
				     </c:when>	
				 </c:choose>
				 
				 <td>${conflictChange.conflictRealizedByContext.requestId}</td>	
				 
				 <td nowrap><form:radiobutton path="conflictChanges[${status.index}].resolveActionCode" value="keep" />Keep
				     <form:radiobutton path="conflictChanges[${status.index}].resolveActionCode" value="discard" />Discard
				 </td>	
				 
			   </tr>
			  </c:forEach>																	
			</tbody>
	   </table>								
      <div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">	
	  
	      <img title="Save" onclick="javascript:resolveConflicts();"  src="<c:url value="/img/icons/Save.png"/>"/>
	  	
	  	  <img title="Reset" onclick="javascript:resetResolveConflictsPage();" src="<c:url value="/img/icons/Reset.png"/>"/>
		  <img id="print" class="viewMode" title="Print" src="<c:url value="/img/icons/Print.png"/>" onclick="printAlert();" />
	 </div>
    
    </form:form>
	</c:when>
	<c:otherwise>
	       There is no conflicts
	</c:otherwise>
  </c:choose>
 	
</div>

</html>