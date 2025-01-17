<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<%@ page import="ca.cihi.cims.web.bean.ConceptViewBean" %>
<%@ page import="ca.cihi.cims.model.ContentViewerModel" %>

<script type="text/javascript">
	$(document).ready(function() {
		var currentContextId = $("#currentContextId").attr("value");
		// Retrieve graphics
		$(".graphicDiv").each(function() {
			var graphicEle = $( this );
			var src = graphicEle.attr("src");
			var style = graphicEle.attr("style");
			$.ajax({
				  url: "${pageContext.request.contextPath}/getDiagram.htm?diagramFileName="+src+"&contextId="+currentContextId,
				  type: "POST",
				  contentType: "image/gif",
				  success: function(result){
					  graphicEle.html('<img id="' + src + '" src="data:image/gif;base64,' + result + '" style="' + style + '"/>');					  
				  }
			});
		});
	});
</script>

<hidden id="currentContextId" name="currentContextId" value="${viewBean.contextId}"/>

<!-- TODO: Need to check the concept type and not show table content for alphabetical and external index books.
     Currently this is working, but it would be nice to be addressed when we get time in the future.
-->
	    <%
			ConceptViewBean cViewBean = (ConceptViewBean)request.getAttribute("viewBean");
            StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(cViewBean.getJsonText());
			String finalHtmlString = stringBuffer.toString();
		%>
				<%= finalHtmlString %> 
