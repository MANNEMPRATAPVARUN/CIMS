<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<html style="height:100%;">
<jsp:include page="../../common/common-header.jsp"/>
<style type="text/css">
   .btn_line {margin:5px 5px 5px 750px;}
 </style>
 
<div class="content">
   
   <div class="success">
      Your change request ${changeRequest.changeRequestId} has been successfully Created
   </div>
    <form:form id="changeRequest"  modelAttribute="changeRequest"  >
    </form:form>


    <div class="btn_line">
	           <input type="button"  value="Validate" class="button" onclick="javascript:validateChangeRequest(${changeRequest.changeRequestId});" >&nbsp;&nbsp; <input type="button"  value="Cancel" class="button" >&nbsp;&nbsp;
    </div>
</div>   
</html> 