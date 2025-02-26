<!DOCTYPE html> 
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>

<div class="content">
<fieldset>
    <legend><fmt:message key="home.subtitle.my.assigned.refsets"/></legend>
    <c:if test="${fn:length(myAssignedRefsets) gt 0}">
	    <display:table name="myAssignedRefsets" id="refset" requestURI="" pagesize="${pageSize}" partialList="true" size="resultSize" class="listTable" style="width: 100%;">
		    <display:column titleKey="home.refsets.catalog.category" headerClass="tableHeader">
                ${refset.category}
            </display:column>
   
            <display:column titleKey="home.refsets.version.name" headerClass="tableHeader">   
                <a href='<c:url value='/refset/picklist.htm?contextId=${refset.contextId}&elementId=${refset.elementId}&elementVersionId=${refset.elementVersionId}' />' target="_parent">           
                    ${refset.refsetVersionName}   
                </a>             
            </display:column>            
        </display:table>
    </c:if>
    
    <c:if test="${fn:length(myAssignedRefsets) eq 0}">
         You have no assigned refsets 
    </c:if>
</fieldset>
</div>

</html>

