<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<sec:authentication var="user" property="principal" />
<div>
	<div class="floatRight">
		<a href="javascript:newSearch();" id="searchNew"><img src="<c:url value='/img/icons/Add.png'/>" title="<fmt:message key='search.list.btn.new'/>"/></a>
		<a id="searchRun" ><img src="<c:url value='/img/icons/RunGrey.png'/>" title="<fmt:message key='search.list.btn.run'/>"/></a>
		<a id="searchDelete"><img src="<c:url value='/img/icons/RemoveGrey.png'/>" title="<fmt:message key='search.list.btn.delete'/>"/></a>
	</div>
</div>
<div class="clear">
	<fieldset>
		<legend class="searchLegend"><fmt:message key="search.list.legend"/></legend>
		<div id="scrollPane" style="height: 400px; overflow: auto;">
			<fmt:message key='search.list.empty' var="emptyListMessage"/>
			<display:table id="search" name="searchList" class="listTable" style="width: 100%;" requestURI="">
				<display:setProperty name="basic.msg.empty_list" value="${emptyListMessage}"/>
				<display:column style="width: 10px;">
					<input type="radio" name="group" value="${search.id}" id="search_${search.id}" onclick="searchSelected(${search.id},${search.ownerId},${currentUser.userId});"/>
				</display:column>
				<display:column titleKey="search.list.col.name">
					<div onclick="selectSearch(${search.id});"><a href="<c:url value='/search/run.htm?searchId=${search.id}'/>">${search.name}</a></div>
				</display:column>
				<display:column titleKey="search.list.col.access" style="width: 100px;">
					<div onclick="selectSearch(${search.id});">
						<c:choose>
							<c:when test="${search.shared}">
								<fmt:message key="search.access.shared"/>
							</c:when>
							<c:otherwise>
								<fmt:message key="search.access.private"/>
							</c:otherwise>
						</c:choose>
					</div>
				</display:column>
				<display:column titleKey="search.list.col.creator" style="width: 100px;">
					<div onclick="selectSearch(${search.id});">${usernames[search.ownerId]}</div>
				</display:column>
			</display:table>
		</div>
	</fieldset>
</div>
<script>
	//make the table header sticky
	$(document).ready(function() {
		$("#search").floatThead({
		    scrollContainer: function($table){
		        return $table.closest("#scrollPane");
		    }
		});
	});
	
	function getSelectedSearchId() {
		return $("#search input[type='radio']:checked").val();	
	}
	
	function runSearch() {
		var searchId = getSelectedSearchId();
		if(typeof searchId != "undefined" && searchId != null) {
			window.location = "<c:url value='/search/run.htm?searchId="+searchId+"'/>";
		}
	}
	
	function newSearch() {
		window.location = "<c:url value='/search/new.htm?classification=${classification}&searchType=${searchType}'/>";
	}
	
	function deleteSearch() {
		var searchId = getSelectedSearchId();
		if(typeof searchId != "undefined" && searchId != null) {
			removeBox({
					title:"<fmt:message key='search.delete.title'/>", 
					text:"<fmt:message key='search.delete.message'/>",
					callback: function(){
						deleteSearchById(searchId);
					}
				});
		}
	}
	
	function deleteSearchById(searchId) {
		var onSuccess = function(data) {
			hideProcessingScreen();
			if(typeof data != "undefined" && data != null) {
				if(data.result) {
					Feedback.success("<fmt:message key='search.delete.result.success'/>");
					$("#search input[type='radio'][value='"+searchId+"']").closest('tr').remove();
					toggleRunButton(false);
					toggleDeleteButton(false);
				}
				else if(data.error) {
					Feedback.error(data.error);
				}
				else {
					Feedback.error("<fmt:message key='search.delete.result.error'/>");
				}
			}
		};
		var onError = function() {
			hideProcessingScreen();
			Feedback.error("<fmt:message key='search.delete.result.error'/>");
		};
		$.ajax({
		    url: "<c:url value='/search/delete.htm?searchId="+searchId+"'/>",
		    type: 'DELETE',
		    success: onSuccess,
		    error: onError,
		    beforeSend: showProcessingScreen
		});
	}
	
	function searchSelected(searchId, ownerId, currentUserId) {
		if(typeof searchId != "undefined" && searchId != null) {
			toggleRunButton(true);
			toggleDeleteButton(ownerId == currentUserId);
		}
	}
	
	function toggleRunButton(enable) {
		var link = $("#searchRun");
		if(link.length){
			link.find("img").first().attr("src", enable ? "<c:url value='/img/icons/Run.png'/>" : "<c:url value='/img/icons/RunGrey.png'/>");
			if(enable) {
				link.attr("href", "javascript:runSearch();");
			}
			else {
				link.removeAttr("href");
			}
		}
	}
	
	function toggleDeleteButton(enable) {
		var link = $("#searchDelete");
		if(link.length) {
			link.find("img").first().attr("src", enable ? "<c:url value='/img/icons/Remove.png'/>" : "<c:url value='/img/icons/RemoveGrey.png'/>");
			if(enable) {
				link.attr("href", "javascript:deleteSearch();");
			}
			else {
				link.removeAttr("href");
			}
		}
	}
	
	function selectSearch(searchId) {
		if(typeof searchId != "undefined" && searchId != null) {
			var radio = $("#search_"+searchId);
			if(radio != null && typeof radio != "undefined") {
				radio.click();
			}
		}
	}
</script>