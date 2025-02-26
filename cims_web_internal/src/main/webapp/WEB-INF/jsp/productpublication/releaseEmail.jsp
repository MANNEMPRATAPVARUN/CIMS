<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>

<script type="text/javascript">  
  
  $(document).ready(function(){    
	  closeWindowAfterEmailSent();
  });

  function sendEmail() {  
      $('form#publicationRelease').serialize();
   	  $("#publicationRelease")[0].action="<c:url value='/sendReleaseEmailNotification.htm'/>";
   	  $("#publicationRelease")[0].submit();
  }   
  function closeWindowAfterEmailSent(){
	  <c:if test="${emailSent}">
          self.close();
      </c:if>
  } 


</script> 




<html style="height:100%;">
   
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 100%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
				  <form:form id="publicationRelease"  modelAttribute="publicationRelease" method="post" >
				     Release Message
				       <form:hidden path="releaseId" />
				       <form:hidden path="releaseType" />
				       <form:hidden path="fiscalYear" />
				       <form:hidden path="status" />
				       <form:textarea path="releaseNote" />
				         <script>
    			           CKEDITOR.replace( "releaseNote",{height:350 });
                         </script>
				       
				       <div class="btn_alignRight"> 
			              <c:choose>
                            <c:when test='${!emailSent && !publicationRelease.notificationSent}'>
			                  <input type="button"  value="Send" class="button" onclick="javascript:sendEmail();" >&nbsp;&nbsp; 
			                </c:when>
			                <c:otherwise>  
			                   <input type="button"  value="Send" class="disabledButton"  disabled="disabled" >&nbsp;&nbsp; 
			                </c:otherwise>  
			               </c:choose> 
			           </div>
				  </form:form>
	               
	               
	              
	
	
	
	
				</div>
        	 </div>
	  </div>
		    
	

  </body>


</html>
