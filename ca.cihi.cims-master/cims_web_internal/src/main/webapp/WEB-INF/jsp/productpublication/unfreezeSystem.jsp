
<%@page language="java" %>
<%@ page import="ca.cihi.cims.model.prodpub.GenerateFileStatus" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<style type="text/css">
   #instruction{
       height:50px;
       width:550px;
   }
 </style>

<script type="text/javascript">  
   function unfreezeICD(){
	  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/unfreezeICD.htm'/>";
	  $("#generateReleaseTablesCriteria")[0].submit();
   }
   function unfreezeCCI(){
		  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/unfreezeCCI.htm'/>";
		  $("#generateReleaseTablesCriteria")[0].submit();
	}
   
</script> 




<h4 class="contentTitle">
	Production Publication &#62; Unfreeze Systems
</h4>
 <jsp:include page="productpublication-tabs.jsp"/>
 <div class="content">
   <br/>
   
    <form:form id="generateReleaseTablesCriteria" modelAttribute="generateReleaseTablesCriteria"  method="post" >
    
      <form:hidden path="currentOpenYear"/>
      <c:if test='${unfreezeCCISuccess}'>
           <div class="info"> CCI  is unfrozen
           </div>
      </c:if> 
     <c:if test='${unfreezeICDSuccess}'>
           <div class="info"> ICD  is unfrozen
           </div>
      </c:if> 
     
      <form:errors path="*" cssClass="errorMsg" />
      
      
      <div id="instruction">
  	   To unfreeze the system to make additional changes to tabular and validations (including generic attributes, reference values, in-context generic descriptions), 
       please click the button below .	
      </div>	
	 <table  style="width: 550px;" >	
			
        <tr ><td>
		           &nbsp;&nbsp;
		     </td>
		     <td >
			     <div class="btn_alignRight"> 
			       <security:authorize access="!hasAnyRole('ROLE_IT_ADMINISTRATOR')">
			         <c:choose>
                         <c:when test='${icdFreezingStatus !=null && latestICDSnapShot!=null && latestICDSnapShot.status!="I" && !releaseInProgress}'>
			                <input type="button"  value="Unfreeze ICD-10-CA" class="button" onclick="javascript:unfreezeICD();" >&nbsp;&nbsp; 
			             </c:when>
			            <c:otherwise>
			               <input type="button"  value="Unfreeze ICD-10-CA" class="disabledButton" disabled="disabled" >&nbsp;&nbsp; 
			            </c:otherwise>
			         </c:choose> 
			         <c:choose>
                        <c:when test='${cciFreezingStatus !=null && latestCCISnapShot!=null && latestCCISnapShot.status!="I" && !releaseInProgress}'>
			               <input type="button"  value="Unfreeze CCI" class="button" onclick="javascript:unfreezeCCI();" >&nbsp;&nbsp; 
			            </c:when>
			            <c:otherwise>
			               <input type="button"  value="Unfreeze CCI" class="disabledButton" disabled="disabled" >&nbsp;&nbsp; 
			             </c:otherwise>
			          </c:choose>  
			       </security:authorize>
			        
			         <security:authorize access="hasAnyRole('ROLE_IT_ADMINISTRATOR')">
			            <c:choose>
                         <c:when test='${icdFreezingStatus !=null}'>
			                <input type="button"  value="Unfreeze ICD-10-CA" class="button" onclick="javascript:unfreezeICD();" >&nbsp;&nbsp; 
			             </c:when>
			            <c:otherwise>
			               <input type="button"  value="Unfreeze ICD-10-CA" class="disabledButton" disabled="disabled" >&nbsp;&nbsp; 
			            </c:otherwise>
			         </c:choose> 
			         <c:choose>
                        <c:when test='${cciFreezingStatus !=null}'>
			               <input type="button"  value="Unfreeze CCI" class="button" onclick="javascript:unfreezeCCI();" >&nbsp;&nbsp; 
			            </c:when>
			            <c:otherwise>
			               <input type="button"  value="Unfreeze CCI" class="disabledButton" disabled="disabled" >&nbsp;&nbsp; 
			             </c:otherwise>
			          </c:choose>  
			         </security:authorize>
			          
                </div>
             </td>   
      </tr>
    </table>         
   </form:form>
  </div>