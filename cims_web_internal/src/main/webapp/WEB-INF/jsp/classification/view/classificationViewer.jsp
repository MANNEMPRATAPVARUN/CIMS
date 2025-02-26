<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<span style="display:none;" id="activateNode">${activateNode}</span>
<div id="splitterContainer">
	<div id="leftPane">
		<div id="tree"></div>
	</div>
	<div id="rightPane">
		<div id="content"></div>
	</div>
</div>
<script type="text/javascript">
	$(window).load(function() {
		//Initialize the vertical screen splitter
		$('#splitterContainer').split({
			orientation : 'vertical',
			limit : 100,
			position : '30%'
		});
		
		//Initialize the classification explorer tree
		var options = {
			classification : "${param.classification}",
			contextId : "${param.contextId}",
			language : "${param.language}",
			changeRequestId : "${param.changeRequestId}",
			viewMode : Boolean("${param.viewMode}")
		};
		initializeTree("tree", options);
		
		//initialize the content pane controller
		ContentPaneController.setContentMarkupId("content");
	});
	
	//splitter fails to automatically resize when shrinking
	//viwer window, thus we do it ourselves
	var lastWindowWidth = $(window).width();
	var readjustSplitter = function(){
		var splitter = $('#splitterContainer').split();
		var windowWidth = $(window).width();
		if(typeof splitter != "undefined" && splitter != null) {
			var splitterPosition = splitter.position();
			var splitPercent = 0.3;
			if(typeof splitterPosition != "undefined") {
				splitPercent = splitterPosition/lastWindowWidth;
			}
			splitterPosition = windowWidth * splitPercent;
			splitter.position(splitterPosition);
		}
		lastWindowWidth = windowWidth;
	};
	$(window).resize(function(){
		setTimeout(readjustSplitter,0);
	});
	
	function getGroupContent(languageCode, contextId, sectionCode, firstLetter, id){
		var url = "supplements/content/groupContent.htm?language="+languageCode+"&contextId="+contextId+"&sectionCode="+sectionCode+"&groupCode="+firstLetter+"&elid="+id;
		AjaxUtil.replaceContent('supplementContent',url);
	}
	
	function getRubricContent(languageCode, contextId, sectionCode, firstLetter, id){
		var url = "supplements/content/rubricContent.htm?language="+languageCode+"&contextId="+contextId+"&sectionCode="+sectionCode+"&groupCode="+firstLetter+"&id="+id;
		AjaxUtil.replaceContent('supplementContent',url);
	}
	//# sourceURL=classificationViewer.jsp
</script>