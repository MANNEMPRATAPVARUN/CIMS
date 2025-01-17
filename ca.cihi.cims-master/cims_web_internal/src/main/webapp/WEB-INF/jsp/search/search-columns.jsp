<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<div class="paletteContainer">
	<div class="searchColumnList">
		<label><fmt:message key="search.column.list.available"/>:</label>
		<select multiple="true" id="availColumns">
			<c:forEach var="entry" items="${columnTypes}">
				<option label="${entry.value.displayName}" value="${entry.value.id}"/>
			</c:forEach>
		</select>
	</div>
	<div class="paletteButtons">
		<button id="include" class="button">&gt;&gt;</button>
		<button id="exclude" class="button">&lt;&lt;</button>
	</div>
	<div class="searchColumnList">
		<label><fmt:message key="search.column.list.selected"/>:</label>
		<spring:bind path="columnTypeIds">
			<select multiple="true" id="selectedColumns" class="${status.error ? 'fieldError' : ''}">
				<c:forEach var="columnId" items="${search.columnTypeIds}">
					<option label="${columnTypes[columnId].displayName}" value="${columnId}"/>
				</c:forEach>
			</select>
			<form:hidden path="columnTypeIds" id="columnTypeIds"/>
		</spring:bind>
	</div>
	<div class="paletteButtons">
		<button id="moveUp" class="button"><fmt:message key="search.column.move.up"/></button>
		<button id="moveDown" class="button"><fmt:message key="search.column.move.down"/></button>
	</div>
</div>
<script>
$(document).ready(function(){
	var moveUp = function(event) {
		event.preventDefault();
		var selectedItems = $("#selectedColumns option:selected");
		var firstUnselected = selectedItems.first().prev();
		if(firstUnselected.length) {
			selectedItems.remove();
			firstUnselected.before(selectedItems);
			setColumnTypeIds();
		}
	};
	
	var moveDown = function(event) {
		event.preventDefault();
		var selectedItems = $("#selectedColumns option:selected");
		var firstUnselected = selectedItems.last().next();
		if(firstUnselected.length) {
			selectedItems.remove();
			firstUnselected.after(selectedItems);
			setColumnTypeIds();
		}
	};
	
	var include = function(event) {
		event.preventDefault();
		var $selected = $("#selectedColumns"); 
		$selected.append($("#availColumns option:selected").attr("selected",false).remove());
		setColumnTypeIds();
	};
	
	var exclude = function(event) {
		event.preventDefault();
		addToAvailableList($("#selectedColumns option:selected").remove());
		
		setColumnTypeIds();
	};
	
	var setColumnTypeIds = function() {
		var list = "";
		$("#selectedColumns option").each(function(i,option){
			if(i > 0) {
				list += ",";
			}
			list += $(option).val();
		});
		$("#columnTypeIds").val(list);
	};
	
	var initializeAvailableList = function() {
		var orderedOptionValues = new Array();
		var $available = $("#availColumns");
		$available.find("option").each(function(i,option){
			orderedOptionValues.push(option.value);
		});
		$available.data("orderedValues",orderedOptionValues);
		
		//remove items that exist in the selected list
		var $selected = $("#selectedColumns"); 
		$selected.find("option").each(function(i,option){
			$available.find("option[value='"+option.value+"']").remove();
		});
	};
	
	var addToAvailableList = function(options) {
		var $available = $("#availColumns");
		var options = $available.find("option").add(options.attr("selected",false));
		
		var orderedValues = $available.data("orderedValues");
		options.sort(function(a,b){
			return orderedValues.indexOf(a.value) - orderedValues.indexOf(b.value);
		});
		$available.html(options);
	};
	
	$("#moveUp").click(moveUp);
	$("#moveDown").click(moveDown);
	$("#include").click(include);
	$("#exclude").click(exclude);
	initializeAvailableList();
});
</script>