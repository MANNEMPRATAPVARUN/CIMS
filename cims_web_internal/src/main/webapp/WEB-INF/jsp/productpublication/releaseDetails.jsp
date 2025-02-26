<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>

<script type="text/javascript">  
   var editor;  
   function popupReleasedZipFile(releaseId) {  
	   var link = "downloadReleaseZipFile.htm?releaseId="+releaseId;
	   var newwindow = window.open(link, "downloadReleaseZipFile", "width=700,height=750,resizable=yes,scrollbars=yes ");
	   if (window.focus)  {
	     newwindow.focus();
	   }
   }   	

   function toggleDiv(divId){
	   if (editor){
		   editor.destroy(); 
	   }
	   $('#' + divId).toggle();
	   var isHidden = $('#' + divId).is(':hidden');
	   if (!isHidden){
		   editor = CKEDITOR.replace( divId, {readOnly : true, height:350} );
	   }
	     
   }   
   
</script> 




<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 100%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
				  <form:form id="publicationRelease"  modelAttribute="publicationRelease" method="post" >
				     Release Details
				     <table id="releaseDetails" style="width: 100%; margin-top: 0px;" class="listTable">
				        <tr class="odd">
                           <td>Release Type:  </td>
                           <td> ${publicationRelease.releaseType.releaseTypeCode}  </td>
                       </tr>				      
				       <tr class="even">
                           <td>Release #:  </td>
                           <td> ${publicationRelease.releaseNum}  </td>
                       </tr>	
                       <tr class="odd">
                           <td>Classification:  </td>
                           <td> ICD-10-CA &amp; CCI  </td>
                       </tr>	
                        <tr class="even">
                           <td>Year:  </td>
                           <td> ${publicationRelease.fiscalYear}  </td>
                       </tr>
                       <tr class="odd">
                           <td>Release Date:  </td>
                           <td>  <fmt:formatDate pattern="yyyy-MM-dd" value="${publicationRelease.createdDate}"/>  </td>
                       </tr>
                       <tr class="even">
                           <td>Release By:  </td>
                           <td> ${publicationRelease.releasedBy.username}  </td>
                       </tr>
                       <tr class="odd">
                           <td>Release Zip:  </td>
                           <td><a href="javascript:popupReleasedZipFile(${publicationRelease.releaseId});"> ${publicationRelease.releaseFileName} </a> </td>
                       </tr>
                       <tr class="even">
                           <td>Release Message:  </td>
                           <td> 
                                <a href="javascript:toggleDiv('releaseMsg');" title="${fn:escapeXml(publicationRelease.releaseNote)}" > Message </a>
                               
                               <div id="releaseMsg" style="display:none" >
                                   ${publicationRelease.releaseNote}
                               </div>
                            </td>
                       </tr>
                       
				     </table>
				     
				     
				  </form:form>
	               
	               
	              
	
	
	
	
				</div>
        	 </div>
	  </div>
		    
	

  </body>


</html>
