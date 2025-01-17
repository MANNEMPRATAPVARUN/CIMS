
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
   <table class="changeRequestHeader" >
		<tr>
			<td >Change Request ID: &nbsp;&nbsp;
			   ${changeRequestDTO.changeRequestId}</td>
			<td>Classification: &nbsp;&nbsp;
			   ${changeRequestDTO.baseClassification}</td>
		    <td>Year:  &nbsp;&nbsp;
			  ${changeRequestDTO.baseVersionCode}</td>
			 <td>Request Category:  &nbsp;&nbsp;
			  ${changeRequestDTO.category.code}</td> 
		</tr>
   </table>
   <span style="display:none;" id="crLastUpdatedTime">${changeRequestDTO.lockTimestamp}</span>
    
   <div class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all noborder">
     <ul  class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all"> 
        
        <li class="${activeTab=='manageChangeRequest'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
          <a href='<c:url value="/manageChangeRequest.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /></c:url> '>
            <span>Manage Change Request</span>
          </a>
        </li>
        
        <li class="${activeTab=='changeRequestModificationHistory'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
          <a href='<c:url value="/changeRequestHistory.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /></c:url> '>
            <span>Change Request Modification History</span>
          </a>
        </li>
        <c:if test="${changeRequestDTO.status.statusId ==4 || changeRequestDTO.status.statusId==5 || changeRequestDTO.status.statusId >=9}">
        <li class="${activeTab=='manageClassification'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
           <a href='<c:url value="/manageClassification.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /><c:param name="contextId" value="${changeRequestDTO.baseContextId}" /><c:param name="language" value="${changeRequestDTO.languageCode}" /></c:url>'>
             <span>Manage Classification</span>
            </a>
        </li>
        </c:if>
        <li class="${activeTab=='classificationChangeSummary'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
          <c:choose>
	           <c:when test="${changeRequestDTO.category.code == 'Index'}">           
		           <a href='<c:url value="indexChangeSummary.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /></c:url>'>
		              <span>Classification Change Summary</span>
		           </a>
	           </c:when>
	           <c:when test="${changeRequestDTO.category.code == 'Supplements'}">
	               <a href='<c:url value="supplementChangeSummary.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /></c:url>'>
		              <span>Classification Change Summary</span>
		           </a>
	           </c:when>
	           <c:otherwise>
	               <a href='<c:url value="tabularChangeSummary.htm"><c:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" /></c:url>'>
		              <span>Classification Change Summary</span>
		           </a>
	           </c:otherwise>
           </c:choose>
        </li>
     </ul>
   </div>




