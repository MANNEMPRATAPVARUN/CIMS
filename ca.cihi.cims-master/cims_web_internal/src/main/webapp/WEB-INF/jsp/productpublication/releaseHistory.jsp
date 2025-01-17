<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  
 
   function popupReleaseDetails(releaseId) {
      var link = "popupReleaseDetails.htm?releaseId="+releaseId ;
	  var newwindow = window.open(link, "releaseId"+releaseId, "width=700, height=500 ,resizable=yes,scrollbars=yes ");
	  if (window.focus)  {
		  newwindow.focus();
	  }
   } 
   
   function popupReleaseEmail(releaseId) {
	      var link = "popupReleaseEmail.htm?releaseId="+releaseId ;
		  var newwindow = window.open(link, "emailRelease "+releaseId, "width=700, height=500 ,resizable=yes,scrollbars=yes ");
		  if (window.focus)  {
			  newwindow.focus();
		  }
	   } 
   
 </script> 



<h4 class="contentTitle">
	Production Publication &#62; Release History
</h4>
 <jsp:include page="productpublication-tabs.jsp"/>

<div class="content">
  
   <div id="noticeMsg" class="notice" style="width: 73%;"> Note: Please ensure to download the release package and unzip it to the public folders and then send the email.</div>
   
   <display:table name="allReleaseHistory" id="releaseHistory"  requestURI="" class="listTable" 
		style="width: 75%; margin-top: 0px;">
		
		<display:column  titleKey="production.releasehistory.year" headerClass="tableHeader" style="text-align:center;">
              ${releaseHistory.versionYear}
        </display:column>
        
        <display:column titleKey="production.releasehistory.icdSnapshotDate" headerClass="tableHeader" style="text-align:center;">
             <fmt:formatDate pattern="yyyy_MMdd" value="${releaseHistory.icdSnapShotDate}"/>
        </display:column>
        
         <display:column titleKey="production.releasehistory.cciSnapshotDate" headerClass="tableHeader" 	style="text-align:center;">
        	  <fmt:formatDate pattern="yyyy_MMdd" value="${releaseHistory.cciSnapShotDate}"/>
        </display:column>
        
         <display:column titleKey="production.releasehistory.preliminaryInternalQARelease" headerClass="tableHeader" 	style="text-align:center;">
        	 <a href="javascript:popupReleaseDetails(${releaseHistory.preliminaryInternalQAReleaseId});"> ${releaseHistory.preliminaryInternalQARelease} </a>
        	 &nbsp;&nbsp; 
        	 <c:if test='${releaseHistory.emailPreliminaryInternalQARelease}'>  
        	       <img title="Email" src="<c:url value="/img/icons/Email.png"/>"  onclick="javascript:popupReleaseEmail(${releaseHistory.preliminaryInternalQAReleaseId});"/>
        	  </c:if>
        </display:column>
        
        <display:column titleKey="production.releasehistory.preliminaryRelease" headerClass="tableHeader" 	style="text-align:center;">
        	 <a href="javascript:popupReleaseDetails(${releaseHistory.preliminaryReleaseId});"> ${releaseHistory.preliminaryRelease}</a>
        	  &nbsp;&nbsp; 
        	 <c:if test='${releaseHistory.emailPreliminaryRelease}'>
        	     <img title="Email" src="<c:url value="/img/icons/Email.png"/>"  onclick="javascript:popupReleaseEmail(${releaseHistory.preliminaryReleaseId});"/>
        	 </c:if>
        </display:column>
        
        <display:column titleKey="production.releasehistory.officialInternalQARelease" headerClass="tableHeader" 	style="text-align:center;">
        	<a href="javascript:popupReleaseDetails(${releaseHistory.officialInternalQAReleaseId});"> ${releaseHistory.officialInternalQARelease}</a>
        	  &nbsp;&nbsp; 
        	 <c:if test='${releaseHistory.emailOfficialInternalQARelease}'>  
        	      <img title="Email" src="<c:url value="/img/icons/Email.png"/>"  onclick="javascript:popupReleaseEmail(${releaseHistory.officialInternalQAReleaseId});"/>
        	 </c:if>
        </display:column>
        
        <display:column titleKey="production.releasehistory.officialRelease" headerClass="tableHeader" 	style="text-align:center;">
        	 <a href="javascript:popupReleaseDetails(${releaseHistory.officialReleaseId});"> ${releaseHistory.officialRelease}</a>
        	   &nbsp;&nbsp; 
        	 <c:if test='${releaseHistory.emailOfficialRelease}'>  
        	    <img title="Email" src="<c:url value="/img/icons/Email.png"/>" onclick="javascript:popupReleaseEmail(${releaseHistory.officialReleaseId});"/>
        	 </c:if>
        </display:column>
        
        
    </display:table>

</div>