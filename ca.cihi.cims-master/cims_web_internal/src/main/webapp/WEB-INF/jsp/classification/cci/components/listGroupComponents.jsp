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

<script src="js/displayTagService.js"></script>
<script type="text/javascript">

	$(document).ready(function() {
		parent.changeEditMode(false);			
		window.parent.hideMessage();
		window.parent.hideProcessingScreen();
		window.parent.greyButtons();
		window.parent.showPostSuccessMessage();
		modifyPageLinks();
	});

	function definitionPopup(elementId) {
		var editMode = window.parent.editMode;
		
		if (editMode == true) {
			return false;
		}
		
		var iframe = $("#diagramIframe");
		iframe.attr("src", iframe.data("src") + "e=" + elementId);
	
		$("#definitionDialog").dialog("open");
	}


</script>

<div class="content">

	<display:table name="components" id="componentTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="paging.banner.group_size" value="10" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>"/>
					
		<display:column headerClass="tableHeader sizeThirty">
			<input name="radioSelection" id="${componentTable.elementId}" type="radio">
		</display:column>
				
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

		<display:column titleKey="component.definition" headerClass="tableHeader sizeEighty" style="text-align:center;">
			<img title="Definition" src="img/icons/Note.png" onclick="definitionPopup(${componentTable.elementId});"/>			
		</display:column>		
		
		<display:column sortable="true" titleKey="component.status" headerClass="tableHeader sizeEighty" 
			style="text-align:center;">
			
			${cims:capitalizeFully(componentTable.status)}
		</display:column>			       	
	</display:table>

	<c:forEach var="component" items="${components}">
    	<label id="${component.elementId}_isNewlyCreated" style="display: none;">${component.isNewlyCreated}</label>
	</c:forEach>
	
	<input type='text' id='componentModelType' value='group' style="display: none;"/>		
		
	<c:import url="/WEB-INF/jsp/common/displayTagService.jsp"/>
</div>

<div id="definitionDialog" style="display:none; overflow:hidden;" title="Dialog Title">
	<iframe name="diagramIframe" id="diagramIframe"
		data-src="groupComponents/diagram.htm?"				
		src="about:blank" width="100%" height="100%"></iframe>
</div>

<script>
	$("#definitionDialog").dialog({
		//resizable : true,		
	    position: { my: "center", at: "top", of: window } ,
	    title: 'Group Definition',
	    draggable: false, width : 1000, height : 500, autoOpen: false, modal : true
	});
		
	/*********************************************************************************************************
	 * NAME:          Radio Button clicked
	 * DESCRIPTION:   
	 *********************************************************************************************************/	
	 $("input[type='radio']").click(function() {		 
		 window.parent.colorButtons();		 
	 });
	
	 $(".tableHeader a").click(function() {		 
		 window.parent.showInfoMessage('Loading');
		 window.parent.showProcessingScreen();
	 });
</script>
</html>

