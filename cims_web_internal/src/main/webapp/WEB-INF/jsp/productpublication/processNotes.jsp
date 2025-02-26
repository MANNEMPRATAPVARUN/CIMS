<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
 




<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 80%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
				   <c:if test="${fn:length(snapShots) gt 0}">
	                  <c:forEach items="${snapShots}" var="snapShot" varStatus="status">
	                     <div class="alignLeft"> 
	                     <b>  Date :<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${snapShot.createdDate}"/> </b> <br/>
	                     </div>
	                       <textarea name="${status.index}">${snapShot.snapShotNote}</textarea>
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
