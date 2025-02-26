<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>


<script type="text/javascript">
   function popupChangeRequestViewer(changeRequestId) {
	      var link = "manageChangeRequest.htm?changeRequestId="+changeRequestId ;
		  var newwindow = window.open(link, "changeRequest"+changeRequestId, "resizable=yes,scrollbars=yes ");
		  newwindow.moveTo(0,0);
		  newwindow.resizeTo(screen.width, (screen.height-(screen.height/20)));
		  if (window.focus)  {
			  newwindow.focus();
		  }
   }		

</script>


<div class="content">

<fieldset>
   <legend><fmt:message key="home.subtitle.assigned"/></legend>
    <c:if test="${fn:length(myChangeRequests) gt 0}">
	<display:table name="myChangeRequests" id="changeRequest" defaultsort="2" defaultorder="descending" requestURI="" pagesize="${pageSize}" sort="external" partialList="true" size="resultSize" class="listTable"  style="width: 100%; ">
		<display:column titleKey="manage.change.request.id" headerClass="tableHeader" sortName="ID">
          <a href="javascript:popupChangeRequestViewer(${changeRequest.changeRequestId});">${changeRequest.changeRequestId}</a>
        </display:column>
   
        <display:column sortable="true" titleKey="manage.change.request.name" headerClass="tableHeader" sortName="NAME">
             ${changeRequest.name}
        </display:column>
        
        <display:column sortable="true" titleKey="change.request.classification" headerClass="tableHeader" sortName="CLASSIFICATION">
             ${changeRequest.baseClassification}
        </display:column>
        
        <display:column  titleKey="manage.change.request.fiscalYear" headerClass="tableHeader" sortName="VERSIONCODE">
             ${changeRequest.baseVersionCode}
        </display:column>
        <display:column  titleKey="manage.change.request.category" headerClass="tableHeader" sortName="CATEGORY">
             ${changeRequest.category.code}
        </display:column>
        
        <display:column  titleKey="manage.change.request.status" headerClass="tableHeader" sortName="STATUS">
             ${changeRequest.status.statusCode}
        </display:column>
       
    </display:table>
    </c:if>
    <c:if test="${fn:length(myChangeRequests) eq 0}">
       You have no assigned tasks 
    </c:if>
</fieldset>

</div>
</html>

