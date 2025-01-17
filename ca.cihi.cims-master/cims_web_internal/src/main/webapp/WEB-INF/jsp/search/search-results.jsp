<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<c:set var="hasErrors" value="${false}"/>
<spring:hasBindErrors name="search">
	<c:set var="hasErrors" value="${true}"/>
	<div class="error">
		<c:forEach var="error" items="${errors.allErrors}">
			<div><spring:message message="${error}"/></div>
		</c:forEach>
	</div>
</spring:hasBindErrors>
<spring:hasBindErrors name="searchResultCount">
	<c:set var="hasErrors" value="${true}"/>
	<div class="error">
		<c:forEach var="error" items="${errors.allErrors}">
			<div><spring:message message="${error}"/></div>
		</c:forEach>
	</div>
</spring:hasBindErrors>
<c:if test="${hasErrors == false}">
	<div><fmt:message key="search.results.count"><fmt:param value="${searchResults == null ? 0 : fn:length(searchResults)}"/></fmt:message></div>
	<div id="scrollPane" style="height: 400px; overflow: auto;">
		<fmt:message key='search.result.empty' var="emptyListMessage"/>
		<c:url value="/" var="baseUrl"/>
		<display:table id="searchResults" name="searchResults" class="listTable"
			style="width: 100%;" requestURI="">
			<display:setProperty name="basic.msg.empty_list" value="${emptyListMessage}"/>
			<c:forEach items="${columns}" var="column">
				<c:if test="${fn:length(searchResults) > 0}"><c:set var="columnValue" value="${searchResults[fn:toUpperCase(column.type.modelName)]}"/></c:if>
				<c:choose>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "CHANGE_REQUEST_ID"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupChangeRequestViewer('${baseUrl}','${columnValue}');">${columnValue}</a></display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "GEN_ATTR_LIST_NEW"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/inContext.htm?contextId=${search.contextId}&disableEditing=true','${column.type.displayName}',800,500);"><fmt:message key="search.column.value.attribute.list"/></a></display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "GEN_ATTR_LIST_OLD"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link">
							<c:if test="${columnValue != 0}">
								<a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/inContext.htm?contextId=${search.priorContextId}&disableEditing=true','${column.type.displayName}',800,500);"><fmt:message key="search.column.value.attribute.list"/></a>
							</c:if>
						</display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "ATTR_NOTES_NEW_EN"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/note.htm?contextId=${search.contextId}&language=ENG','${column.type.displayName}',600,350);"><img src="<c:url value='/img/icons/Note.png'/>" title="<fmt:message key='search.column.value.note'/>"/></a></display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "ATTR_NOTES_NEW_FR"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/note.htm?contextId=${search.contextId}&language=FRA','${column.type.displayName}',600,350);"><img src="<c:url value='/img/icons/Note.png'/>" title="<fmt:message key='search.column.value.note'/>"/></a></display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "ATTR_NOTES_OLD_EN"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/note.htm?contextId=${search.priorContextId}&language=ENG','${column.type.displayName}',600,350);"><img src="<c:url value='/img/icons/Note.png'/>" title="<fmt:message key='search.column.value.note'/>"/></a></display:column>
					</c:when>
					<c:when test='${fn:toUpperCase(column.type.modelName) == "ATTR_NOTES_OLD_FR"}'>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-link"><a href="javascript:popupwindow('${baseUrl}/referenceAttributes/${columnValue}/note.htm?contextId=${search.priorContextId}&language=FRA','${column.type.displayName}',600,350);"><img src="<c:url value='/img/icons/Note.png'/>" title="<fmt:message key='search.column.value.note'/>"/></a></display:column>
					</c:when>
					<c:when test="${columnValue != null && (fn:containsIgnoreCase(columnValue.class.name, 'date') || fn:containsIgnoreCase(columnValue.class.name, 'timestamp'))}">
						<display:column title="${column.type.displayName}" class="search-result-table-cell-default nowrap"><fmt:formatDate value="${columnValue}" pattern="yyyy-MM-dd HH:mm:ss"/></display:column>
					</c:when>
					<c:otherwise>
						<display:column title="${column.type.displayName}" class="search-result-table-cell-default" style="${fn:length(columnValue) > 100 ? 'width:100ch;' : '' }">${columnValue}</display:column>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</display:table>
	</div>
</c:if>
<script>
	$(document).ready(function(){
		var searchResultsTable = $("#searchResults");
		if(searchResultsTable.length){
			searchResultsTable.floatThead({
			    scrollContainer: function($table){
			        return $table.closest("#scrollPane");
			    }
			});
		}
	});
</script>