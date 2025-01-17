/*
 * Use with DisplayTagUtilService.java
 * 
 * 1.  Include this file in your JSP
 * 2.  Include <jsp:include page="displayTagService.jsp" /> somewhere in your JSP  
 * 3.  Inside your controller, call the service method passing in the table Id
 * 	   Example: mav.addAllObjects(dtService.addForPageLinks(request, "componentTable"));
 */

function modifyPageLinks() {
	var pageJump = $("#pageJump").text();
	var resultSize = $("#resultSize").text();
	var pageSize = $("#pageSize").text();
	var displayTagPageNum = $("#displayTagPageNum").text();
	var displayTagPageKey = $("#displayTagPageKey").text();
	var pages = resultSize / pageSize;
	pages = Math.ceil(pages.toFixed(2));
	var pagesJumpDecisionNext = pages - pageJump - displayTagPageNum;
	var pagesJumpDecisionPrev = displayTagPageNum - pageJump;
	var qs = "?";
	qs += addIfAvail('vc');
	qs += addIfAvail('s');
	qs += addIfAvail('e');
	qs += addIfAvail('st');
	
	qs += "&" + $("#displayTagSortKey").text() + "=" + $("#displayTagSortNum").text();
	qs += "&" + $("#displayTagOrderKey").text() + "=" + $("#displayTagOrderNum").text();
	
	qs += "&" + displayTagPageKey + "=";

	var next = "";
	if (pagesJumpDecisionNext > 0) {
		var nextPageNum = parseInt(pageJump, 10)
				+ parseInt(displayTagPageNum, 10);
		var nextQS = qs + nextPageNum;
		next = "[<a href='" + nextQS + "' class='dtAH'>Next 10</a>]";
	} else {
		if (pages > 1) {
			next = "[Next 10]";	
		} else {
			next = "";
		}
	}

	var prev = "";
	if (pagesJumpDecisionPrev > 0) {
		var prevPageNum = parseInt(displayTagPageNum, 10)
				- parseInt(pageJump, 10);
		var prevQS = qs + prevPageNum;
		prev = "[<a href='" + prevQS + "' class='dtAH'>Prev 10</a>]";
	} else {
		if (pages > 1) {
			prev = "[Prev 10]";	
		} else {
			prev = "";
		}		
	}

	var pageLinksHtml = $('.pagelinks').html();
	$('.pagelinks').html(prev + pageLinksHtml + next);

	$('.pagelinks a').bind("click", function() {
		window.parent.showProcessingScreen();
	});
}

function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex
			.exec(location.search);
	return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g,
			" "));
}

function addIfAvail(key) {
	var id = getParameterByName(key);	
	var stringToReturn = "";
	
	if (id !== "") {
		stringToReturn = "&" + key + "=" + id;
	} 
	
	return stringToReturn;
}