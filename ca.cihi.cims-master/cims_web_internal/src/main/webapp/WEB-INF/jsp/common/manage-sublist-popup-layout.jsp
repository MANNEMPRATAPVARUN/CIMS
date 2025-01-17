<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	   
<body  style="height:100%;">
    <div class="container" style="width: 1000px;">
        <div class="span-24 last" style="width: 1000px; overflow: visible !important; " > 
            <div class="contentContainer" >
			    <tiles:insertAttribute name="body" />
            </div>
	    </div>
    </div>
</body>
</html>

