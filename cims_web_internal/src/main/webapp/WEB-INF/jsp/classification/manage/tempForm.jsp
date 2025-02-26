<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<html style="height:100%;">
<jsp:include page="../../common/common-header.jsp"/>
<style type="text/css">
   .btn_line {margin:5px 5px 5px 750px;}
   select {width: 100px;}
</style>
 <script type="text/javascript">  
 
     $(document).ready(function(){    
         alert('init call');
    	 callVersionList();    
     });  
     function callVersionList(){    
         alert('call version list');
    	 $.get("<c:url value='/getBaseClassificationVersions.htm'/>", 
        	  {baseClassification: $("#baseClassification").val()}, 
    		  function(data) {    
                  $("#baseContextId").empty();    
                  $.each(data, function(key, item) {    
                     var optionvalue= '<option value="' + item.contextId +'" >' + item.versionCode + '</option>';    
                     $("#baseContextId").append(optionvalue);    
                  });    
              });    
       }   
     
     function createChangeRequest(){
    	alert('get called');	
    	 $("#changeRequest")[0].action="<c:url value='/createChangeRequestFromIframe.htm'/>";
         $("#changeRequest")[0].submit();
        	
        	//$('#contents').contents().find('#changeRequest').submit();
     }
     
 </script>

   <form:form id="changeRequest"  modelAttribute="changeRequest"  >
   
      <fieldset>
           <legend class="likeHeading"><b>Basic Information</b></legend>
           
             <table class="accordion_table">
               <tr>
                 <td ><span class="required">*</span>  Language:  </td>
                 <td>
                   <form:select path="languageCode" id="languageCode"  >
    					<option value="ENG">English</option>
    					<option value="FRA">French</option>
    			   </form:select>
			    </td>
			    <td><span class="required">*</span>Classification: </td>
                 <td>
                   <select  id="baseClassification" onchange="javascript:callVersionList();" >
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <option value="${baseClassification}"> ${baseClassification}  </option>
  	  	               </c:forEach>
    			   </select>
			     </td>
			   
			     <td><span class="required">*</span>Year: </td>
                 <td>
                    <form:select path="baseContextId" id="baseContextId"  >
    					<option value=""> </option>
    			    </form:select>
			     </td>
			 </tr>
             <tr>
               <td>&nbsp;</td>
                 <td colspan="5">
                  
    			   <input name="frenchRequired" type="checkbox"> Changes to French Version Required
			     </td>
              </tr>
              <tr>
                 <td ><span class="required">*</span> Request Name:  </td>
                 <td colspan="5">
                    <form:input path="name" id="name" size="120"/>
                 </td>
              </tr>   
            
             </table>
            
          
           
      </fieldset>
           <div class="btn_line">
	           <input type="button"  value="Save" class="button" onclick="javascript:createChangeRequest();" >&nbsp;&nbsp; <input type="button"  value="Cancel" class="button" >&nbsp;&nbsp;
            </div>
    </form:form>
 </html>   