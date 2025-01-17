<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<script src="js/displayTagService.js"></script>
<script type="text/javascript">

	$(document).ready(function() {
		modifyPageLinks();
		window.parent.parent.hideMessage();
		window.parent.parent.hideProcessingScreen();
	});
	
</script>

<h2 style="text-align:center;">Reference Links for Component</h2>

<div class="content">

	<div id="block_container">
		<div style="display:inline-block; width:10%;">
			<label>Year:</label>&nbsp;&nbsp;
			<font color=red>${cciComponentsForViewer.versionCode}</font>
		</div>	
		<div style="display:inline-block; width:40%;">
			<label>Section:</label>&nbsp;&nbsp;
			<font color=red">${cciComponentsForViewer.sectionTitle}</font>
		</div>
		<div style="display:inline-block; width:10%;">
			<label>Status:</label>&nbsp;&nbsp;
			<font color=red>${cciComponentsForViewer.status}</font>
		</div>
		<div style="display:inline-block; width:25%;">
			<label>Tab:</label>&nbsp;&nbsp;
			<font color=red>${tabName}</font>
		</div>
		<div class="no-print" style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;">		
			<img id="print" class="viewMode" title="Print" src="img/icons/Print.png" onclick="window.print();" />
		</div>		
	</div>
	
	<display:table name="components" id="compRefsTable" defaultsort="0" requestURI="" pagesize="${pageSize}" 
		class="listTable" style="width: 100%; margin-top: 20px;">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td></tr>"/>
							
		<display:column titleKey="component.code" headerClass="tableHeader">
			${compRefsTable.componentCode}
		</display:column>
		
		<display:column titleKey="concept.code" headerClass="tableHeader" sortable="true">
			${compRefsTable.code}
		</display:column>

		<display:column titleKey="component.shortDescription.eng" headerClass="tableHeader">
			${compRefsTable.shortDescriptionEng}
		</display:column>

		<display:column titleKey="component.shortDescription.fra" headerClass="tableHeader">
			${compRefsTable.shortDescriptionFra}
		</display:column>
       	
	</display:table>	

	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
	
</div>

<script>
	 $(".tableHeader a").click(function() {		 
		 window.parent.showInfoMessage('Loading');
	 });
</script>
</html>

