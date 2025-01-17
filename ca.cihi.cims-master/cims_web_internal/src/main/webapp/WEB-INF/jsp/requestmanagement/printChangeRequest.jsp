<%@page language="java" %>
<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
	<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
		<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link href="css/cims.css" rel="stylesheet">
	
	<title>Print Change Request</title>
</head>
	<body  style="height:100%;">
<style type="text/css" media="all">

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

#changerequest-header.fixed {
    height: 80px;
    margin-bottom: 20px;
}

#changerequest-header.fixed .wrapper {
    height: 75px;
    position: fixed;
    top: 0;
    left: 10px;
    z-index: 100;
    padding: 0;
}
 table {
   width: 100%;
  }
  td{
    text-align: left;
    padding-right: 1px;
    padding-left: 1px;
    padding-top: 5px;
    padding-bottom: 5px;
  }
  img {border : 0;}
  body{
	  -webkit-print-color-adjust:exact;
	}

</style>
  <div id="changerequest-header" style="width: 100%; overflow: visible !important; " class="header">


   <table style="margin-bottom: 0px; width:100%;">
		<tr>
			<td width="30%">Change Request ID:</td><td width="15%">${changeRequestDTO.changeRequestId}</td>
			<td width="25%">Classification:</td><td width="30%">${changeRequestDTO.baseClassification}</td>
		</tr>
		<tr>
		    <td>Year:</td><td>${changeRequestDTO.baseVersionCode}</td>
			 <td>Request Category:</td><td>${changeRequestDTO.category.code}</td> 
		</tr>
   </table>	
   </div>
	
	  <div style="width: 100%; overflow: visible !important; " >
    		 <div class="contentContainer" >

<div class="content">
		<div id="button-header">
            <div class="alignRight">
	      
           <div class="btn_alignRight header">
               <c:if test="${changeRequestDTO.deferredChangeRequestId !=null }">
                 Original Deferring CR: &nbsp;   ${changeRequestDTO.deferredChangeRequestId} &nbsp;&nbsp;&nbsp;
               </c:if>
              
               <c:if test="${changeRequestDTO.deferredTo !=null }">
                 This change request has been deferred to ${changeRequestDTO.deferredTo.baseVersionCode} ; Deferred CR:&nbsp;  ${changeRequestDTO.deferredTo.changeRequestId} &nbsp;&nbsp;&nbsp;
               </c:if>
              
              Owner: &nbsp; ${changeRequestDTO.owner.username}  &nbsp;&nbsp;&nbsp;
              Assignee:   &nbsp; 
             <c:choose>
                <c:when test="${changeRequestDTO.userAssignee !=null }">
                    ${changeRequestDTO.userAssignee.username}
               </c:when>
                <c:otherwise>
                    ${changeRequestDTO.dlAssignee.name}
               </c:otherwise>  
             </c:choose>    
           </div>
         </div>
       </div>
<jsp:include page="section_basicInfo_print.jsp"/>
		<div id="rational" class="section">
            <div>
                <div class="sectionHeader"><span class="section-header">Rationale for Change and Decisions</span></div>
             	<div id ="div_rational">
             	   <table style="margin-bottom: 5px;">
             	   <tr>
             	   <td width="20%" class="alignTop label">Rationale for Change: </td> 
	               <td width="80%" class="alignTop">
	               		${changeRequestDTO.changeRationalTxt }
	               </td>
	               </tr>
	               <c:if test='${cf:hasReadAccess(currentUser,"SECTION_RATIONALE_FOR_STATUS_CHANGE")}'> 
	                <c:if test="${fn:length(changeRequestDTO.rationaleForValid) gt 0}">
	                <tr>
    	        	<td class="alignTop label">Rationale for "Valid" Status :</td>
                  	<td class="alignTop">
	               		${changeRequestDTO.rationaleForValid }
	               	</td>
	               	</tr>
	               	</c:if>
	               	<c:if test="${fn:length(changeRequestDTO.rationaleForClosedDeferred) gt 0}">
	                <tr>
	                <td class="alignTop label">Rationale for "Closed-Deferred" Status :</td>   
	                <td class="alignTop">
	               		${changeRequestDTO.rationaleForClosedDeferred }
	               	</td>
	                </tr>
	                 </c:if>    
                	<c:if test="${fn:length(deferableContextIdentifiers) gt 0}">
	    	         <tr>
	    	         <td class="alignTop label">Rationale for "Rejected" Status :</td>  
	    	         <td class="alignTop">
	               		${changeRequestDTO.rationaleForReject }
	               	 </td>
	               	 </tr>
	               	</c:if>
                  </c:if>
                  </table>
				</div>   
			</div>
        </div>
<jsp:include page="section_questionForReviewers_print.jsp"/>
		<c:if test="${fn:length(changeRequestDTO.commentDiscussions) gt 0}">
			<div id="discussions" class="section">
                <div  >
                 <div class="sectionHeader" ><span class="section-header">Discussions and Comments</span></div>
			    </div>
                    <div id="div_discussions" >     
                    <table id="tbl_discussions">
                    <tbody>
                      <c:forEach items="${changeRequestDTO.commentDiscussions}" var="commentDiscussion" varStatus="status">
                       <tr>
                       <td width="20%" class="alignTop label">${commentDiscussion.commmentUser.username}:</td>
                       <td width="80%" class="alignTop">${commentDiscussion.userCommentTxt}</td>
                       </tr>
                       </c:forEach> 
                      
                     </tbody>
		            </table>
		           </div>
		     </div> 
		 </c:if>
          <c:if test='${cf:hasReadAccess(currentUser,"SECTION_ADVICE_COMMENT")}'>
          <c:if test="${fn:length(changeRequestDTO.advices) gt 0}">
          <div id="advices" class="section">
                <div>
                 <div class="sectionHeader" ><span class="section-header">Received Advices</span></div>
			   </div>
                    <div id="div_advices" >
                    <table id="tbl_advices">
                     <tbody>
                      <c:forEach items="${changeRequestDTO.advices}" var="advice" varStatus="status">
	                      <tr>
	                       <td class="alignTop label" width="20%">${status.index+1}.</td>
	                       <td width="80%" class="alignTop">	
	                           <span class="label">Date:</span>  &nbsp;&nbsp;<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${advice.lastUpdatedTime}" /> &nbsp;&nbsp; 
			                   <span class="label">Requestor:</span>  &nbsp;&nbsp;${advice.sender.username}  &nbsp;&nbsp; 
			                   <span class="label">Recipient:</span>  &nbsp;&nbsp;${advice.userAdvisor.username}&nbsp; ${advice.dlAdvisor.name}
	                           <br/>
	                          <span class="label"> Request Message:</span>  &nbsp;&nbsp;${advice.message}
	                       </td>
	                      </tr>
                     
                           <c:forEach items="${advice.adviceComments}" var="adviceComment" varStatus="statusComment">
    		                <tr>
    		                <td class="alignTop label">${status.index+1} - ${statusComment.index+1}:</td>
    		                <td class="alignTop"><span class="label">Responsed By:</span>  &nbsp;&nbsp;${adviceComment.commmentUser.username}<br/><span class="label">Comment:</span>  &nbsp;&nbsp;${adviceComment.userCommentTxt}</td>
    		                </tr>
    		               </c:forEach>
                       </td>
                    </tr>
		            </c:forEach>
                     </tbody>
		            </table>
		         </div>
		     </div> 
		     </c:if>
		     </c:if>
<jsp:include page="section_references_print.jsp"/>
<div style="float:right; width:30px;" class="no-print">
	<a href="javascript:window.print();"><img title="Print" src="<c:url value="/img/icons/Print.png"/>"/></a>
</div>
</div>
</div>
	  </div>
		    
	

  </body>


</html>

