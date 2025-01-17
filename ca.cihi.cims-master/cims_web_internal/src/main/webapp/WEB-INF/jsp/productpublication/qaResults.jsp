<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  
   function addQAResult(){
       var url = "<c:url value='/updateQAResult.htm'/>";
       $("#publicationSnapShot")[0].action=url;
       $('form#publicationSnapShot').serialize();
	   $("#publicationSnapShot")[0].submit();
    }

</script> 




<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 80%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
				  <form:form id="publicationSnapShot"  modelAttribute="publicationSnapShot" method="post" >
				       <form:hidden path="structureId"/>
				       <form:hidden path="snapShotId"/>
				      <div class="alignLeft"> 
				         <b> Date : <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${publicationSnapShot.createdDate}"/> </b> <br/>
				      </div>
				      <form:textarea path="snapShotQANote"/>
				         <script>
    			          CKEDITOR.replace("snapShotQANote");
                         </script>
				       <div class="btn_alignRight"> 
			               <input type="button"  value="Add/Modify Result" class="button" onclick="javascript:addQAResult();" >&nbsp;&nbsp; 
			            </div>
				  </form:form>
	               
	               
	               <c:if test="${fn:length(snapShots) gt 0}">
	                  <c:forEach items="${snapShots}" var="snapShot" varStatus="status">
	                  
	                      <div class="alignLeft"> 
	                        <b>  Date :<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${snapShot.createdDate}"/> </b> <br/>
	                      </div>
	                       <textarea name="${status.index}">${snapShot.snapShotQANote}</textarea>
	                        <script>
                              qaResultEditor= CKEDITOR.replace( "${status.index}", {readOnly :  true });
                            </script>
                         
	                  </c:forEach>
	               </c:if>
	
	
	
	
				</div>
        	 </div>
	  </div>
		    
	

  </body>


</html>
