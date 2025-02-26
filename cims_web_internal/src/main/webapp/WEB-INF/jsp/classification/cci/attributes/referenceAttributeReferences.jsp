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

	<div id="block_container" class="no-print">
		<div style="display:inline-block; text-align: right; float:right; top:0px; border:0px; background: #ffffff;">
			<img id="print" class="viewMode" title="Print" src="<c:url value="/img/icons/Print.png"/>" onclick="window.print();" /> 
		</div>		
	</div>
	
	<display:table name="attributes" id="attrRefsTable" defaultsort="2" requestURI=""
		class="listTable" style="width: 100%; margin-top: 20px; table-layout:fixed;" sort="list">

		<display:column titleKey="attribute.reference.reference.code" headerClass="tableHeader sizeOneEighty">
			${attrRefsTable.referenceAttributeCode}
		</display:column>
				
		<display:column titleKey="attribute.reference.reference.reference.code" headerClass="tableHeader sizeOneEighty" sortable="true">
			${attrRefsTable.tabularCode}
		</display:column>
		
		<display:column titleKey="attribute.reference.reference.description.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attrRefsTable.descriptionEng}
		</display:column>

		<display:column titleKey="attribute.reference.reference.status" headerClass="tableHeader sizeEighty" sortable="true"
			style="text-align:center;">
			
			${cims:capitalizeFully(attrRefsTable.status)}
		</display:column>
       	
	</display:table>
	
</div>

<script>
	 $(".tableHeader a").click(function() {		 
		 window.parent.showInfoMessage('Loading');
	 });
</script>
</html>

