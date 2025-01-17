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
	   var url = turnOnTimestampCheck("<c:url value='/submitResolveSupplementConflicts.htm'/>", window.opener.document.body);
		$("#resolveConflict")[0].action=url;
		submitForm();
  }
  
   function resetResolveConflictsPage(){
      $("#resolveConflict")[0].action="<c:url value='/resolveSupplementConflicts.htm?changeRequestId=${changeRequest.changeRequestId}'/>";
	  $("#resolveConflict")[0].submit();
  }
  
   function popupXmlProperty2(url){
		newWindow = window.open(url, 'ProposedChangeProposed', 'top=300, left=300,height=400,width=850,scrollbars=1,resizable');  	
   }
	
   function popupXmlProperty3(url){
		newWindow = window.open(url, 'ProposedChangeConflict', 'top=300, left=300,height=400,width=850,scrollbars=1,resizable'); 	
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
	<c:when test="${fn:length(resolveConflict.conflictSupplementChanges) gt 0}">
    <form:form id="resolveConflict"  modelAttribute="resolveConflict" method="post" >
          <form:hidden path="changeRequestId" value="${changeRequest.changeRequestId}"/>
          <form:hidden path="currentContextId" value="${resolveConflict.currentContextId}"/>
          
    	<table id="proposedConflictSupplementChanges" style="width: 100%; margin-top: 20px;" class="listTable">
			<thead>
				<tr>
				    <th class="tableHeader" >Supplement</th>
				    <th class="tableHeader" >Hierarchical Path</th>	
					<th class="tableHeader" ><fmt:message key="change.summary.fieldName" /></th>								
					<th class="tableHeader" ><fmt:message key="change.summary.proposedValue" /></th>
					<th class="tableHeader" >Realized Conflict Value</th>
					<th class="tableHeader" >Realizing Change Request</th>
					<th class="tableHeader" >Resolve Conflict</th>						
				</tr>
			</thead>
			<tbody>
	          <c:forEach var="conflictSupplementChange" items="${resolveConflict.conflictSupplementChanges}" varStatus="status">
			   <tr class="${status.index%2==0 ? 'even' : 'odd'}">
			 	 <form:hidden path="conflictSupplementChanges[${status.index}].elementId" value="${conflictSupplementChange.elementId}"/>
			 	 <form:hidden path="conflictSupplementChanges[${status.index}].elementVersionId" value="${conflictSupplementChange.elementVersionId}"/>
			 	 <form:hidden path="conflictSupplementChanges[${status.index}].conflictRealizedByContext.contextId" value="${conflictSupplementChange.conflictRealizedByContext.contextId}"/>
			 	 
			 	 <td>${conflictSupplementChange.supplement}</td>
			 	 <td><fmt:message key="change.summary.breadCrumbsRoot" /><c:if test="${conflictSupplementChange.hierarchicalPath != ''}"> &gt; </c:if>${conflictSupplementChange.hierarchicalPath}</td>			 	 
			 
			     <td>${conflictSupplementChange.fieldName}</td>
			     
			     <c:choose>   
				     <c:when test="${conflictSupplementChange.tableName=='XMLPropertyVersion'}">
			         <td>
				       <c:if test="${conflictSupplementChange.proposedValue != null and conflictSupplementChange.proposedValue != 'no_value' and conflictSupplementChange.proposedValue != ''}">
						  <a href="javascript:popupXmlProperty2('viewSupplementXmlProperty.htm?xmlPropertyId=${conflictSupplementChange.proposedValue}&category=Proposed%20Change&fieldName=Proposed%20${conflictSupplementChange.fieldName}&code=${conflictSupplementChange.supplement}');"><fmt:message key="change.summary.proposed" /> ${conflictSupplementChange.fieldName}</a>
				       </c:if>
				     </td>
				     <td>
				       <c:choose>
				        <c:when test="${conflictSupplementChange.conflictValue=='no_conflict'}">
							   <!-- present nothing -->
						     </c:when>
						     <c:when test="${conflictSupplementChange.conflictValue=='no_value'}">
							    <fmt:message key="change.summary.noValue"/>
						     </c:when>														  	
						     <c:otherwise>														  	    
							      <a href="javascript:popupXmlProperty3('viewSupplementXmlProperty.htm?xmlPropertyId=${conflictSupplementChange.conflictValue}&category=Proposed%20Change&fieldName=Conflict%20${conflictSupplementChange.fieldName}&code=${conflictSupplementChange.supplement}');"><fmt:message key="change.summary.conflict" /> ${conflictSupplementChange.fieldName}</a>
						     </c:otherwise>
					       </c:choose>													
				          </td>	
				      </c:when>	
				      <c:otherwise>
					      <td>
						     <c:if test="${conflictSupplementChange.proposedValue != null and conflictSupplementChange.proposedValue != 'no_value' and conflictSupplementChange.proposedValue != ''}">
							   ${conflictSupplementChange.proposedValue}
						     </c:if>
					       </td>
					       <td>
						     <c:choose>
						        <c:when test="${conflictSupplementChange.conflictValue=='no_conflict'}">
								   <!-- present nothing -->
							    </c:when>
							    <c:when test="${conflictSupplementChange.conflictValue=='no_value'}">
									<fmt:message key="change.summary.noValue"/>
							    </c:when>														  	
							    <c:otherwise>														  	    
							 	    ${conflictSupplementChange.conflictValue}
							    </c:otherwise>
						      </c:choose>
					       </td>	
			           </c:otherwise>	
			     </c:choose>				
				
				 <td>${conflictSupplementChange.conflictRealizedByContext.requestId}</td>	
				 
				 <td nowrap><form:radiobutton path="conflictSupplementChanges[${status.index}].resolveActionCode" value="keep" />Keep
				     <form:radiobutton path="conflictSupplementChanges[${status.index}].resolveActionCode" value="discard" />Discard
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