<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style>
.hidden {
 display: none;
}
</style>

<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  

function popupDetailedLog(htmlOutputLogId, classification, fiscalYear, language){
    var link = "exportFolioFileDetailLog.htm?htmlOutputLogId=" +htmlOutputLogId +"&classification="+classification+"&fiscalYear="+fiscalYear+"&language=" +language;
	  var newwindow = window.open(link, "detaillog", "width=700, height=500 ,resizable=yes,scrollbars=yes");
	  if (window.focus)  {
		  newwindow.focus();
	  }
}

window.setInterval(function() {
    //get status
	if($("#genteration_status").length >0 && $("#genteration_status").text() != "Done"){
		$.ajax({
			    url: "<c:url value='/admin/getExportFolioFileStatus.htm'/>",
			    cache: false,
			    type: "GET",
			    success: function(data) {
			    	   $("#genteration_status").empty();   
			    	   $("#genteration_status").html(data);  		    	   
			    }
		});
    }

    //get zip file name
    if($("#last_download_url").length >0 && $("#genteration_status").length >0 && $("#genteration_status").text() == "Done"){ 
		$.ajax({
			    url: "<c:url value='/admin/getExportFolioZipFileName.htm'/>",
			    cache: false,
			    type: "GET",
			    success: function(data) {
				    if(data != null && data.length > 0){
			    	   $("#last_download_url").empty();   
			    	   $("#last_download_url").html("Download");  		    	   
			    	   $("#last_download_url").attr("href", "ExportFolioFile/Download.htm?zipFileName="+data);
				    }  	
			    }
		});
    }

	
	}, 5000);

</script>

<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.exportFolioFileStatus" />
	
</h4>

<div class="content">
   <display:table name="allGenerationStatuses" id="genStatus"  requestURI="" class="listTable" style="width: 35%; margin-top: 0px;">
		
		<display:column property="htmlOutputLogId" title="" class="hidden" headerClass="hidden" /> 
		
		<display:column  titleKey="admin.exportFolioFileStatus.date" headerClass="tableHeader" >
			<fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${genStatus.generatedDate}" />
        </display:column>
        
        <display:column titleKey="admin.exportFolioFileStatus.classification" headerClass="tableHeader" style="text-align:center;">
        	${genStatus.classification}
        </display:column>
        
        <display:column titleKey="admin.exportFolioFileStatus.year" headerClass="tableHeader"  style="text-align:center;">
        	${genStatus.year}
        </display:column>

        <display:column titleKey="admin.exportFolioFileStatus.language" headerClass="tableHeader"  style="text-align:center;">
        	${genStatus.language}
        </display:column>
        
        <display:column  titleKey="admin.exportFolioFileStatus.status" headerClass="tableHeader" style="text-align:center;">
          <c:choose>
            <c:when test="${genStatus.lastGeneration == true}">
                <a id="genteration_status" href="javascript:popupDetailedLog(${genStatus.htmlOutputLogId}, '${genStatus.classification}', '${genStatus.year}', '${genStatus.language}');">${genStatus.status}</a>
        	</c:when>
        	<c:otherwise>
        	   ${genStatus.status}
        	</c:otherwise>	        
          </c:choose>
        </display:column>
        
        <display:column  titleKey="admin.exportFolioFileStatus.url" headerClass="tableHeader" style="text-align:center;">
          <c:choose>
            <c:when test="${genStatus.lastGeneration == true && (genStatus.status != 'Done' || genStatus.downloadUrl == nul)}">
        	   <a id = "last_download_url"></a>
        	</c:when>
            <c:when test="${genStatus.status == 'Done' && genStatus.downloadUrl != nul}">
        	   <a href="<c:out value='ExportFolioFile/Download.htm?zipFileName=${genStatus.downloadUrl}'/>">Download</a>
        	</c:when>
        	<c:otherwise>
        	</c:otherwise>	        
          </c:choose> 
        </display:column>
        
    </display:table>

</div>