<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.book.lead.term"/></legend>
	<div class="span-2 inline form-row">
		<div class="label"><label><fmt:message key="search.criteria.book"/>:</label></div>
		<div class="label"><label class="mandatory"><fmt:message key="search.criteria.lead.term"/>:</label></div>
	</div>
	<div class="span-22 last inline form-row">
		<div><form:select path="bookId" id="bookId" items="${bookList}" itemValue="code" itemLabel="description"/></div>
		<div>
			<spring:bind path="leadTermId">
				<form:input id="leadTermText" path="leadTermText" class="${status.error ? 'fieldError' : 'searchbox'}"/><form:hidden path="leadTermId" id="leadTermId"/>
			</spring:bind>
		</div>
	</div>
</fieldset>
<script type="text/javascript">
$(document).ready(function(){
	var searchCallback = function(request, response) {
	    $.ajax({
	        url: "<c:url value='/getCodeSearchResult.htm'/>",
	        contentType:"application/json; charset=UTF-8",
	        cache: false, /*CSRE-890*/
	        data: {
	          term : request.term,
	          indexElementId : $("#bookId option:selected").val(),
	          contextId : $("#contextIds option:selected").val(),
	          classification : "${search.classificationName}",
	          activeOnly : false,
	          searchBy : "bookIndex"
	        },
	        success: function(data) {
	          response(data);
	        }
	      });
	};
	
	var selectCallback = function(event, ui) {
		if(typeof ui != "undefined" && ui != null) {
			var selectedItem = ui.item;
			if(typeof selectedItem != "undefined" && selectedItem != null) {
				setLeadTermId(selectedItem.conceptId);	
			}
		}
	};
	
	var setLeadTermId = function(id) {
		var leadTermId = $("#leadTermId");
		if(leadTermId.length && leadTermId.val() != id) {
			leadTermId.val(id);
		}
	};

	$("#leadTermText").autocomplete({source : searchCallback,  select: selectCallback, position: { collision : "flip none" }, delay: 500})
	.focus(function(){$(this).select();})
	.change(function(){setLeadTermId(null);});
	
	var reloadBookIndexes = function(language, contextId) {
		$.ajax({
	        url: "<c:url value='/search/bookIndexes.htm'/>",
	        contentType:"application/json; charset=UTF-8",
	        data: {
	          contextId : contextId,
	          classification : "${search.classificationName}",
	          language : language
	        },
	        success: function(data) {
	          var bookSelect = $("#bookId");
	          bookSelect.find("option").remove().end();
	          if(data != null) {
	        	  data.forEach(function(bookIndex){
	        		  bookSelect.append($("<option></option>").val(bookIndex.code).text(bookIndex.description));
	        	  });
	          }
	        }
	      });
	};
	
	var currentLanguage = $("#language option:selected").val();
	currentLanguage = currentLanguage == "FRA" ? currentLanguage : "ENG";
	
	var currentContextId = $("#contextIds option:selected").val();
	
	var onLanguageChanged = function(event, language) {
		language = language == "FRA" ? language : "ENG";
		if(language != currentLanguage) {
			reloadBookIndexes(language, currentContextId);
			clearLeadTerm();
			currentLanguage = language;
		}
	};
	var onContextChanged = function(event, contextId) {
		reloadBookIndexes(currentLanguage, contextId);
		clearLeadTerm();
		currentContextid = contextId;
	};
	var clearLeadTerm = function() {
		setLeadTermId(null);
		$("#leadTermText").val(null);
	};
	
	//clear lead term id and lead term text
	//when changing book indexes
	$("#bookId").change(function(){
		clearLeadTerm();
	});

	EventManager.subscribe("languagechanged", onLanguageChanged);
	EventManager.subscribe("contextchanged", onContextChanged);
});

</script>