<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">
  
   function popupChangeRequestViewer(changeRequestId) {
	      var link = "manageChangeRequest.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "changeRequest"+changeRequestId, "width=1200,height=750,resizable=yes,scrollbars=yes ");
		  if (window.focus)  {
			  newwindow.focus();
		  }
		  
    }		

</script>

<h4 class="contentTitle">Change Requests &#62; All ${classification } Change Requests</h4>

<div class="content">

<fieldset>
   <legend>All Change Requests</legend>
    <c:if test="${fn:length(allChangeRequests) gt 0}">
	<display:table name="allChangeRequests" id="changeRequest"  requestURI="" class="listTable" 
		style="width: 100%; margin-top: 0px;">
		
		<display:column sortable="true" titleKey="manage.change.request.id" headerClass="tableHeader sizeOneTen" >
          <a href="javascript:popupChangeRequestViewer(${changeRequest.changeRequestId});">${changeRequest.changeRequestId}</a>
        </display:column>
        
        <display:column sortable="true" titleKey="manage.change.request.fiscalYear" headerClass="tableHeader sizeEighty"
        	style="text-align:center;">
        	
			${changeRequest.baseVersionCode}
        </display:column>
        
        
        <display:column sortable="true" titleKey="manage.change.request.name" headerClass="tableHeader" >
             ${changeRequest.name}
        </display:column>
        <display:column sortable="true" titleKey="manage.change.request.status" headerClass="tableHeader sizeOneFifty" 
        	style="text-align:center;">
        	
            ${changeRequest.status.statusCode}
        </display:column>
        
    </display:table>
    </c:if>
    <c:if test="${fn:length(allChangeRequests) eq 0}">
		There is no change requests
    </c:if>
</fieldset>

</div>


