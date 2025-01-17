<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">

    $(document).ready(function(){ 
        $('#searchCrId').keypress(function(e){
        	if(e.keyCode==13){
        		//$('#searchCrButton').click();
        		var searchOption = $("#searchOption").val();
        		if(searchOption === 'requestId') {
            		popupChangeRequestViewerForSearch();
                } else if (searchOption === 'code') {
                	populateSearchResultSection('code');
                } else if (searchOption === 'leadTerm') {
                	populateSearchResultSection('leadTerm');
               }

        	} 	 
      });
    });

    function populateSearchResultSection(searchType) {
		var code = $("#searchCrId").val();
		$("#searchResultsIframe").attr('src', '/cims_web_internal/getSearchResults.htm?searchType=' + searchType + '&code=' + code);
    }


   function inputFocus(i){
	   //var btn = document.getElementById("searchCrButton");
	   if(i.value==i.defaultValue){ i.value=""; i.style.color="#000"; 
	      //btn.disabled = false;
		  //btn.enabled = true;
		  //btn.style.color="#000";
	   }
   }
   
	function inputBlur(i){
	    //var btn = document.getElementById("searchCrButton");
	    if(i.value==""){ i.value=i.defaultValue; i.style.color="#888"; 
	       //btn.disabled = true;
	       //btn.enabled = false;
	       //btn.style.color="#888";
	    }
	}


</script> 
	<select id="searchOption">
		<option></option>
		<option value = "requestId">Request Id</option>
		<option value = "code">Code</option>
		<option value = "leadTerm">Lead Term</option>
	</select>

    <input type="text" size="10" maxlength="30" id="searchCrId" name="searchCrId" title="Request ID" style="color:#888; " 
         onfocus="inputFocus(this)" onblur="inputBlur(this)" />
<%--
    <input type="image" src="${pageContext.request.contextPath}/img/InfoTeal.png" class="button" 
           style="border:0 none; width: 18px; height: 18px; position: relative; bottom: 0px; left: 0px; background-color:#8dbab8; "
           onclick="javascript:popupCimsWiki(); " > 
--%>
<%--
   	<a id="icon_wiki" href="javascript:popupCimsWiki();"><img src='<c:url value="/img/InfoTeal.png" />' style="border:0 none; width: 18px; height: 18px; position: relative; bottom: 0px; left: 0px; background-color:#8dbab8; " ></a>
--%>


<%--
    <input  type="button" id="searchCrButton" value="<fmt:message key='Legacy.Request.searchButton'/>" 
           class="button" style="visibility:hidden;" disabled onclick="javascript:popupChangeRequestViewerForSearch();"  style="color:#888;" />
--%>


<script>

    function popupChangeRequestViewerForSearch() {

    	var searchBox = document.getElementById("searchCrId"); 
    	var requestId = searchBox.value; 
	    //var btn = document.getElementById("searchCrButton");
    	searchBox.value=searchBox.defaultValue; 
    	searchBox.style.color="#888"; 
	    //btn.disabled = true;
	    //btn.enabled = false;
	    //btn.style.color="#888";
	       
    	//var requestId = document.getElementById("searchCrId").value; 
        if (isNaN(requestId) == true) { requestId = "";}
	    var link = "${pageContext.request.contextPath}/manageChangeRequest.htm?changeRequestId="+requestId ;

		var newwindow = window.open(link, "changeRequest"+requestId, "width=1200,height=750,resizable=yes,scrollbars=yes ");
		if (window.focus)  {
		 newwindow.focus();
		}

    }
    
    function popupCimsWiki() {
    	
	    var link = "http://architecture/display/CLAS/CIMS";

		var newwindow = window.open(link, "cimsWiki", "width=1200,height=750,resizable=yes,scrollbars=yes toolbar=yes menubar=yes status=yes ");
		if (window.focus)  {
		 newwindow.focus();
		}
    
    } 
</script>
