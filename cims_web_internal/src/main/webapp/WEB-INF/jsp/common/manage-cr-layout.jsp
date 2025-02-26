<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<style type="text/css" media="all">

.wrapper {
    width: 80%;
    margin: auto;
    padding: 0;
}
#changerequest-header .wrapper {
    background: #FFF;
    margin-bottom: 20px;
    padding-top: 20px;
    height: 55px;
}

#changerequest-header.fixed {
    height: 80px;
    margin-bottom: 20px;
}

#changerequest-header.fixed .wrapper {
    height: 75px;
    position: fixed;
    top: 0;
    left: 10px;
    z-index: 100;
    padding: 0;
}

</style>

<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	 
	
	<div id="changerequest-header" class="fixed" style="width: 80%; overflow: visible !important; ">
	    <div class="wrapper">
	      <c:if test="${changeRequestDTO.changeRequestId != null}">
		    <tiles:insertAttribute name="changerequest-header" />
		   </c:if>  
		</div>
	</div>
	
	  
	  
	  
     <div class="fixed" style="width: 80%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<tiles:insertAttribute name="body" />
        	 </div>
	  </div>
		    
	

  </body>


</html>

