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
	
	function notePopup(elementId) {
		var editMode = window.parent.editMode;
		var postPage = "<c:url value='/referenceAttributes/" + elementId + "/notes.htm?'/>";

		if (editMode == true) {
			console.log('Cannot perform while in edit mode');
			return false;
		}
		
		var iframe = $("#notesIframe");
						
		iframe.attr("src", postPage + "e=" + elementId);
	
		$("#notesDialog").dialog("open");		
	}	
	
	function inContextPopup(elementId,versionCode) {
		var editMode = window.parent.editMode;
		var postPage = "<c:url value='/referenceAttributes/" + elementId + "/inContext.htm?versionCode="+versionCode+"&disableEditing=false'/>";

		if (editMode == true) {
			console.log('Cannot perform while in edit mode');
			return false;
		}
		
		var newwindow=window.open(postPage, 'Associated Generic Attributes', "width=1100,height=800,resizable=yes,scrollbars=yes ");
        if (window.focus)  {
			  newwindow.focus();
		  }	
	}	
</script>


<div class="content">
	<display:table name="referenceAttributes" id="attributeTable" defaultsort="2" requestURI="" pagesize="${pageSize}" 
		size="resultSize" class="listTable" style="width: 100%; margin-top: 0px; table-layout:fixed;" sort="list">
		
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="basic.empty.showtable" value="true" />
		<display:setProperty name="basic.msg.empty_list_row" 
			value="<tr class='odd'><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>"/>
				
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

		<display:column sortable="true" titleKey="attribute.mandatory" headerClass="tableHeader sizeEighty" style="text-align:center;">
			<c:choose>
				<c:when test="${fn:toUpperCase(attributeTable.mandatory) == 'Y'}"><fmt:message key="common.label.yes"/></c:when>
				<c:otherwise><fmt:message key="common.label.no"/></c:otherwise>
			</c:choose>
		</display:column>
		
		<display:column titleKey="attribute.notes" headerClass="tableHeader sizeEighty no-print" style="text-align:center;" class="no-print">
			<img title="Notes" src="img/icons/Note.png" onclick="notePopup(${attributeTable.elementId});"/>
		</display:column>
		
		<display:column sortable="true" titleKey="attribute.status" headerClass="tableHeader sizeEighty" style="text-align:center;">
			${cims:capitalizeFully(attributeTable.status)}
		</display:column>			
       	
		<display:column titleKey="attribute.associated" headerClass="tableHeader sizeTwoFifty no-print" style="text-align:center;" class="no-print">
			<security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR')">
			<label style="font-weight: normal; text-decoration:underline;" 
				onclick="inContextPopup(${attributeTable.elementId},${cciAttributesForViewer.versionCode});">View/Modify List</label>
			</security:authorize> 
			
			<security:authorize access="!hasAnyRole('ROLE_ADMINISTRATOR')">
			<label style="font-weight: normal; text-decoration:underline;" 
				onclick="inContextPopup(${attributeTable.elementId},${cciAttributesForViewer.versionCode});">View List</label>			
			</security:authorize>
		</display:column>
		
	</display:table>
</div>

<c:forEach var="attr" items="${referenceAttributes}">
    <label id="${attr.elementId}_isNewlyCreated" style="display: none;">${attr.isNewlyCreated}</label>
</c:forEach>

<div id="notesDialog" style="display:none; overflow:hidden;" title="Notes">
	<iframe name="notesIframe" id="notesIframe"						
		src="about:blank" width="100%" height="100%"></iframe>
</div>

<script>
	$("#notesDialog").dialog({	
	    position: { my: "top", at: "top", of: window } ,
	    title: 'Notes',
	    draggable: false, width : 900, height : 500, autoOpen: false, modal : true, 
	    close: function( event, ui ) {
	    	var iframe = $("#notesIframe");
			iframe.attr("src", "");
	    }
	});
	

	
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

