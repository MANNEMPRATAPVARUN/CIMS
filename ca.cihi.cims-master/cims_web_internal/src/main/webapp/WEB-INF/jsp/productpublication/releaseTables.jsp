<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  
   function releaseTables(){
	   if(confirm("Are you sure to proceed?")){
		  $('form#generateReleaseTablesCriteria').serialize();
		  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/releaseClassificationTables.htm'/>";
		  disableAllButtons();
		  $("#generateReleaseTablesCriteria")[0].submit();
		  setTimeout(startProgress(),10000);
	   }
   }

   function startProgress(){
	    document.getElementById('progressing').style.display = 'block';
	    document.getElementById('progressingMsg').innerHTML = 'Tables package is currently being released.';
	    showProcessingScreen();
	    return true;
	}    
  
   function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}
   
   function disableAllButtons(){
		  $("input[type=button]").attr("disabled", "disabled");
		  $("input[type=button]").attr("class", "disabledButton");
   }
   
  
   
   
 </script> 



<h4 class="contentTitle">
	Production Publication &#62; Release Tables
</h4>
 <jsp:include page="productpublication-tabs.jsp"/>

 <div class="content">
  
   <form:form id="generateReleaseTablesCriteria" modelAttribute="generateReleaseTablesCriteria"  method="post" >
    	
    	
    	<div id="progressing"  class="info" style="display: none;" >
    	    <div id="processInfoMsg">
    	    </div>
    	     <div id="progressingMsg"></div>
        </div>
    	
    	<form:errors path="*" cssClass="errorMsg" />
    	
    	 <c:if test='${needFixedWidthFiles}'>
    	    <div id="fixedWidthFileNeeded"  class="error"  >
    	      Please generate both CCI and ICD fixed width files before click release button
    	    </div>
    	 </c:if>
    	 <c:if test='${releaseTableSuccess}'>
           <div  class="info"> 
               The ${latestPublicationRelease.releaseType.releaseTypeCode} classification tables have been available for download. Please access the download in the Release History tab.
           </div>
         </c:if> 
         
         <c:if test='${releaseTableFailed}'>
           <div  class="error"> 
               The ICD-10-CA & CCI ${latestPublicationRelease.releaseType.releaseTypeCode} classification tables package fails to release. Please contact administrator.
           </div>
         </c:if> 
    	
    	
    	<table  style="width: 700px;" >			
		    <tr ><td style="width: 20%">
		           <label> Classification:</label>		
		         
		         </td>
		        <td style="width: 80%">
			       ICD-10-CA and CCI
			        <form:hidden path="classification"/>
			    </td>	
		    </tr>
		     <tr ><td>
		           <label>&nbsp;Year:</label>		
		         </td>
		        <td >
			      ${currentOpenYear}
			      <form:hidden path="currentOpenYear"/>
		       </td>	
		    </tr>
		   
		   
		   
		    <tr ><td>
		           <label><span class="required">*</span> Release Type:</label>		
		         </td>
		        <td >
		         <c:choose>
                     <c:when test='${icdBaseContext.isVersionYear}'>
                          <c:if test="${latestPublicationRelease==null || latestPublicationRelease.releaseType=='PRELIMINARY_INTERNAL_QA'  }" >
                           <form:radiobutton path="releaseType" value="Preliminary_Internal_QA" />Preliminary - Internal QA &nbsp;
		                 </c:if>
		                  <c:if test="${latestPublicationRelease!=null && (latestPublicationRelease.releaseType=='PRELIMINARY_INTERNAL_QA'||latestPublicationRelease.releaseType=='PRELIMINARY') }" >
		                    <form:radiobutton path="releaseType" value="Preliminary" />Preliminary &nbsp;
		                  </c:if>  
		                  <c:if test="${latestPublicationRelease!=null && (latestPublicationRelease.releaseType=='PRELIMINARY'||latestPublicationRelease.releaseType=='OFFICIAL') }" >
		                    <form:radiobutton path="releaseType" value="Official" />Official &nbsp;
			             </c:if>
		           
		                  
                    </c:when>
                    <c:otherwise>
                      <c:if test="${latestPublicationRelease==null || latestPublicationRelease.releaseType=='OFFICIAL_INTERNAL_QA'  }" >
                        <form:radiobutton path="releaseType" value="Official_Internal_QA" />Official - Internal QA &nbsp;
                      </c:if>  
                      <c:if test="${latestPublicationRelease!=null }" >
		                  <form:radiobutton path="releaseType" value="Official" />Official &nbsp;
			          </c:if>
                    </c:otherwise>
                 </c:choose>
			         
		           
		       </td>	
		    </tr>
		   
		      <tr ><td>
		           &nbsp;&nbsp;
		         </td>
		        <td >
		           <div class="btn_alignRight"> 
		             <c:choose>
                         <c:when test='${icdFreezingStatus !=null && cciFreezingStatus !=null && latestICDSnapShot.status=="E" && latestCCISnapShot.status=="E" && !releaseInProgress }'>
			                <input type="button"  value="Release" class="button" onclick="javascript:releaseTables();" >&nbsp;&nbsp; 
			             </c:when>
			            <c:otherwise>
			              <input type="button"  value="Release" class="disabledButton"  disabled="disabled"  >&nbsp;&nbsp; 
			            </c:otherwise>
			         </c:choose> 
			        </div>
		       </td>	
		    </tr>
	</table>
 
  </form:form>
 </div>