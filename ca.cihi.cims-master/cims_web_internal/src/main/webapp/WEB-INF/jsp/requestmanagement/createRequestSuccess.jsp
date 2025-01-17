
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<style type="text/css">
   .btn_line {margin:5px 5px 5px 750px;}
 </style>
 <script type="text/javascript">  
    function validateChangeRequest(changeRequestId){
    	$("#changeRequestDTO")[0].action="<c:url value='/validateChangeRequest.htm'/>?changeRequestId= "+changeRequestId;
    	$("#changeRequestDTO")[0].submit();
    }
 </script>
 
<div class="content">
   
   <div class="success">
      Your change request ${changeRequestDTO.changeRequestId} has been successfully Created
   </div>
    <form:form id="changeRequestDTO"  modelAttribute="changeRequestDTO"  >
    </form:form>


    <div class="btn_line">
	           <input type="button"  value="Validate" class="button" onclick="javascript:validateChangeRequest(${changeRequestDTO.changeRequestId});" >&nbsp;&nbsp; <input type="button"  value="Cancel" class="button" >&nbsp;&nbsp;
    </div>
</div>    