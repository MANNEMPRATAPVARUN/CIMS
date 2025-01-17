<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-print-header.jsp"%>
<body>
<h2 style="text-align:center;">CCI Generic Attributes</h2>
<div class="content">
	<div id="block_container">
		<div style="display:inline-block; width:20%;">
			<label>Year:</label>&nbsp;&nbsp;
			<font color=red>${cciAttributesForViewer.versionCode}</font>
		</div>	
		<div style="display:inline-block; width:40%;">
			<label>Attribute Type:</label>&nbsp;&nbsp;
			<font color=red><c:if test="${cciAttributesForViewer.attributeType eq 'S'}">Status</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'L'}">Location</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'M'}">Mode of Delivery</c:if><c:if test="${cciAttributesForViewer.attributeType eq 'E'}">Extent</c:if></font>
		</div>
		<div style="display:inline-block; width:20%;">
			<label>Status:</label>&nbsp;&nbsp;
			<font color=red>${cciAttributesForViewer.status}</font>
		</div>
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">		
			<img id="print" class="viewMode" title="Print" src="<c:url value="/img/icons/Print.png"/>" onclick="window.print();" />			
		</div>	
	</div>

	<display:table name="genericAttributes" id="attributeTable" defaultsort="1" requestURI="" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:column sortable="true" titleKey="attribute.code" headerClass="tableHeader sizeEighty">
			${attributeTable.code}
		</display:column>

		<display:column titleKey="attribute.description.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionEng}
		</display:column>

		<display:column titleKey="attribute.description.fra" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionFra}
		</display:column>
		
		<display:column sortable="true" titleKey="attribute.status" headerClass="tableHeader sizeEighty" 
			style="text-align:center;">
			
			${cims:capitalizeFully(attributeTable.status)}
		</display:column>			
       	
		
	</display:table>
</div>
</body>
</html>

