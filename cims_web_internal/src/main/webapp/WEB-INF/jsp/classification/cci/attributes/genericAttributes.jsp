<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

	$(document).ready(function() {
		parent.changeEditMode(false);			
		window.parent.hideMessage();
		window.parent.hideProcessingScreen();
		window.parent.greyButtons();
		window.parent.displayIcons();
		modifyPageLinks();
		showMessageBox();
	});
	
	function showMessageBox() {
		if (window.parent.attributeAdded == true) {
			window.parent.attributeAdded = false;
			
			window.parent.showSuccessMessage("Attribute " + window.parent.attributeCodeToDisplay + " successfully added");			
			window.parent.attributeCodeToDisplay = '';
		}

		if (window.parent.attributeRemoved == true) {
			window.parent.attributeRemoved = false;
			window.parent.showSuccessMessage("Attribute " + window.parent.attributeCodeToDisplay + " successfully removed");
			window.parent.attributeCodeToDisplay = '';
		}		
	}		
</script>


<div class="content">
	<display:table name="genericAttributes" id="attributeTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td><td style='display:none;'></td><td style='display:none;'></td><td></td></tr>"/>
		
		<display:column headerClass="tableHeader sizeThirty no-print" class="no-print">
			<input name="radioSelection" id="${attributeTable.elementId}" type="radio">
		</display:column>
		
		<display:column sortable="true" titleKey="attribute.code" headerClass="tableHeader sizeEighty">
			${attributeTable.code}
		</display:column>

		<display:column titleKey="attribute.description.eng" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionEng}
		</display:column>

		<display:column titleKey="attribute.description.fra" headerClass="tableHeader" style="word-wrap:break-word;">
			${attributeTable.descriptionFra}
		</display:column>

		<display:column headerClass="tableHeaderHidden" style="display:none;"/>
		<display:column headerClass="tableHeaderHidden" style="display:none;"/>
		
		<display:column sortable="true" titleKey="attribute.status" headerClass="tableHeader sizeEighty" 
			style="text-align:center;">
			
			${cims:capitalizeFully(attributeTable.status)}
		</display:column>			
       	
		<display:column headerClass="tableHeaderHidden" style="display:none;"/>
		
	</display:table>

	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
</div>

<c:forEach var="attr" items="${genericAttributes}">
    <label id="${attr.elementId}_isNewlyCreated" style="display: none;">${attr.isNewlyCreated}</label>
</c:forEach>

<script>
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/	
	 $("input[type='radio']").click(function(){
		 window.parent.colorButtons();
	});
	
	 $(".tableHeader a").click(function() {		 
		 window.parent.showInfoMessage('Loading');
		 window.parent.showProcessingScreen();
	 });
</script>
</html>

