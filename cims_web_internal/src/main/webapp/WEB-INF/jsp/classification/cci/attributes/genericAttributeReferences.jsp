<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

	$(document).ready(function() {
		modifyPageLinks();
		window.parent.parent.hideMessage();
		window.parent.parent.hideProcessingScreen();
	});
	
</script>

<h2 style="text-align:center;">Reference Links</h2>

<div class="content">
	<div id="block_container">
		<spring:url value="/img/icons/Print.png" var="printIconUrl" />			
		<div style="display:inline-block; width:10%;">
			<label>Year:</label>&nbsp;&nbsp;
			<font color=red>${cciAttributesForViewer.versionCode}</font>
		</div>	
		<div style="display:inline-block; width:20%;">
			<label>Attribute Type:</label>&nbsp;&nbsp;
			<font color=red><c:if test="${cciAttributesForViewer.attributeType eq 'S'}">Status</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'L'}">Location</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'M'}">Mode of Delivery</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'E'}">Extent</c:if></font>
		</div>
		<div style="display:inline-block; width:20%;">
			<label>Attribute View Type:</label>&nbsp;&nbsp;
			<font color=red>${cims:capitalizeFully(cciAttributesForViewer.attributeViewType)}</font>
		</div>
		<div style="display:inline-block; width:20%;">
			<label>Status:</label>&nbsp;&nbsp;
			<font color=red>${cciAttributesForViewer.status}</font>
		</div>
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">					
			<img id="print" class="viewMode" title="Print" src="${printIconUrl }" onclick="window.print();" /> 
		</div>		
	</div>
	
	<display:table name="attributes" id="attrRefsTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		class="listTable" style="width: 100%; margin-top: 20px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />

		<display:column titleKey="attribute.reference.generic.code" headerClass="tableHeader sizeOneSixty">
			${attrRefsTable.genericAttributeCode}
		</display:column>
				
		<display:column titleKey="attribute.reference.generic.reference.code" headerClass="tableHeader sizeOneSixty" sortable="true">
			${attrRefsTable.referenceAttributeCode}
		</display:column>
		
		<display:column titleKey="attribute.reference.generic.description.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attrRefsTable.descriptionEng}
		</display:column>
		
		<display:column titleKey="attribute.reference.generic.incontextdescription.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attrRefsTable.inContextDescriptionEng}
		</display:column>

		<display:column titleKey="attribute.reference.generic.status" headerClass="tableHeader sizeEighty" sortable="true"
			style="text-align:center;">
			
			${cims:capitalizeFully(attrRefsTable.status)}
		</display:column>
       	
	</display:table>	

	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
	
</div>
</html>

