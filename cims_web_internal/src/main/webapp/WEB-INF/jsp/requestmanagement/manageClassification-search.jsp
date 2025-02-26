  <%@ include file="/WEB-INF/jsp/common/include.jsp"%>
  	  <div style="display: inline-block;">
	      <jsp:include page="../classification/view/conceptSearchBox.jsp">
	      		<jsp:param name="classification" value="${changeContext.baseClassification}" />
				<jsp:param name="contextId" value="${changeContext.contextId}" />
				<jsp:param name="changeRequestId" value="${changeRequestDTO.changeRequestId}" />
				<jsp:param name="language" value="${language}" />
				<jsp:param name="parentPage" value="manageClassification.htm" />
				<jsp:param name="showLanguageOptions" value="${changeRequestDTO.languageCode=='ALL'}" />
	      </jsp:include>
      </div>
      <div class="table floatRight">
	  	 <div class="padded inline cell">
	  	 	<a id="reportLink" href="javascript:synchronize();">Synchronize</a>
  	   	 	 <c:choose>
	           <c:when test="${changeRequestDTO.category.code == 'Index'}">  
	           	   <a id="reportLink" href="javascript:popupIncompleteReport('viewIndexIncompleteReport.htm?changeRequestId=${changeRequestDTO.changeRequestId}')"><fmt:message key="change.summary.incompleteReport" /></a> 
	           	</c:when>
	           <c:when test="${changeRequestDTO.category.code == 'Supplements'}">
	                <a id="reportLink" href="javascript:popupIncompleteReport('viewSupplementIncompleteReport.htm?changeRequestId=${changeRequestDTO.changeRequestId}')"><fmt:message key="change.summary.incompleteReport" /></a> 
	            </c:when>
	           <c:otherwise>
	               <a id="reportLink" href="javascript:popupIncompleteReport('viewIncompleteReport.htm?changeRequestId=${changeRequestDTO.changeRequestId}')"><fmt:message key="change.summary.incompleteReport" /></a> 
	           </c:otherwise>
             </c:choose>
	  	 </div>
	     <div class="padded inline cell">
	     	<a id="icon_edit" href="javascript:editCurrentNode();"><img src='<c:url value="/img/edit.png" />' alt="editMode"></a>
	        <a id="icon_done" href="javascript:editCurrentNodeDone();"><img src='<c:url value="/img/done.png" />' alt="viewMode"></a>
	     </div>
	 </div>
<script>
	function synchronize(){
		var url = turnOnTimestampCheck('synchronization/start.htm?ccp_bc=${changeContext.baseClassification}&ccp_cid=${changeContext.contextId}&ccp_rid=${changeRequestDTO.changeRequestId}&ccp_on=1', parent.document.body);
		showIframeDialog('Synchronizing...', 300, 120, url);
	}
	$(document).ready( function() {
	    <c:choose>
	       <c:when test="${viewMode}">
	           $( "#icon_edit" ).show();
	           $( "#icon_done" ).hide();
	           
	        </c:when>
	        <c:otherwise>   
	            $( "#icon_edit" ).hide();
	            $( "#icon_done" ).show();
	        </c:otherwise>
	    </c:choose>  
	    
	    EventManager.subscribe("viewmodechanged", function(event, data){
	    	if(data) {
		   		 $("#icon_edit").show();
		         $("#icon_done").hide();
	    	}
	    	else {
	    		$("#icon_edit").hide();
		        $("#icon_done").show();
	    	}
	    });
	});
	
	function editCurrentNode(){
         EventManager.publish("viewmodechanged", false);
	}
	function editCurrentNodeDone(){
		EventManager.publish("viewmodechanged", true);
	}
	function popupIncompleteReport(url){
		newWindow = window.open(url, 'IncompleteReport', 'top=300, left=300,height=600,width=850,resizable'); 
	}
</script>