<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height:100%;">
<%@ include file="/WEB-INF/jsp/common/common-print-header.jsp"%>
<body>
<h2 style="text-align: center ">Group Components</h2>
<div class="content">
	<div id="block_container">
		<div style="display:inline-block; width:10%;">
			<label>Year:</label>&nbsp;&nbsp;
			<font color=red>${cciComponentsForViewer.versionCode}</font>
		</div>	
		<div style="display:inline-block; width:40%;">
			<label>Section:</label>&nbsp;&nbsp;
			<font color=red>${cciComponentsForViewer.sectionTitle }</font>
		</div>
		<div style="display:inline-block; width:20%;">
			<label>Status:</label>&nbsp;&nbsp;
			<font color=red>${cciComponentsForViewer.status}</font>
		</div>
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;" class="no-print">		
			<img id="print" class="viewMode" title="Print" src="<c:url value="/img/icons/Print.png"/>" onclick="window.print();" />			
		</div>	
	</div>
	<display:table name="components" id="componentTable" defaultsort="1" requestURI=""
		class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
						
		<display:column sortable="true" titleKey="component.code" headerClass="tableHeader sizeEighty">
			${componentTable.code}
		</display:column>

		<display:column titleKey="component.shortDescription.eng" headerClass="tableHeader sizeOneFifty"
			style="word-wrap:break-word;">
			
			${componentTable.shortDescriptionEng}
		</display:column>

		<display:column titleKey="component.shortDescription.fra" headerClass="tableHeader sizeOneFifty"
			style="word-wrap:break-word;">
			
			${componentTable.shortDescriptionFra}
		</display:column>

		<display:column titleKey="component.longDescription.eng" headerClass="tableHeader sizeTwoFifty" 
			style="word-wrap:break-word;">
			
			${componentTable.longDescriptionEng}			
		</display:column>

		<display:column titleKey="component.longDescription.fra" headerClass="tableHeader sizeTwoFifty" 
			style="word-wrap:break-word;">
			
			${componentTable.longDescriptionFra}			
		</display:column>
		
		<display:column sortable="true" titleKey="component.status" headerClass="tableHeader sizeEighty" 
			style="text-align:center;">
			
			${cims:capitalizeFully(componentTable.status)}
		</display:column>			       	
	</display:table>
</div>

</body>
</html>

