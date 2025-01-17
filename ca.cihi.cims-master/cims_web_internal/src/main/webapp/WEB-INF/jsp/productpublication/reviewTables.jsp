<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  
   $(function(){ 
  	 $('a.dialog_detail').each(function() {  
  		   $.data(this, 'dialog', 
  		         $(this).next('.dialog_box').dialog({
  		              autoOpen: false,  
  		              modal: true,  
  		              width: 600,  
  		              height: 300 
  		          })
  		     );  
  		  }).click(function() {  
  		      $.data(this, 'dialog').dialog('open');  
  		       return false;  
  		  });  
  	
   });
    
   function popupQAResults(contextId) {
	      var link = "popupQAResults.htm?contextId="+contextId ;
		  var newwindow = window.open(link, "contextId"+contextId, "width=700, height=500 ,resizable=yes,scrollbars=yes ");
		  if (window.focus)  {
			  newwindow.focus();
		  }
   }
		  
   function popupProcessNotes(contextId) {
	      var link = "popupProcessNotes.htm?contextId="+contextId ;
		  var newwindow = window.open(link, "contextId"+contextId, "width=700, height=500 ,resizable=yes,scrollbars=yes ");
		  if (window.focus)  {
			  newwindow.focus();
		  }
    }
   function popupSnapShotZipFile(snapShotId) {  
			var link = "downloadSnapShotZipFile.htm?snapShotId="+snapShotId;
			var newwindow = window.open(link, "downloadSnapShotZipFileFile", "width=700,height=750,resizable=yes,scrollbars=yes ");
			if (window.focus)  {
			     newwindow.focus();
			}
	}   	
   
   
 </script> 



<h4 class="contentTitle">
	Production Publication &#62; Generate Classification Tables
</h4>
 <jsp:include page="productpublication-tabs.jsp"/>

<div class="content">
   <display:table name="allLatestSnapShots" id="snapShot"  requestURI="" class="listTable" 
		style="width: 35%; margin-top: 0px;">
		
		<display:column  titleKey="production.reviewtables.year" headerClass="tableHeader" >
            ${snapShot.contextIdentifier.versionCode}
        </display:column>
        
        <display:column titleKey="production.reviewtables.latesttables" headerClass="tableHeader"
        	style="text-align:center;">
        	<a href="javascript:popupSnapShotZipFile(${snapShot.snapShotId});">  ${snapShot.contextIdentifier.versionCode}_${snapShot.classification} </a>
        </display:column>
        <display:column titleKey="production.reviewtables.processnotes" headerClass="tableHeader"  style="text-align:center;">
            <a href="javascript:popupProcessNotes(${snapShot.contextIdentifier.contextId});"> Notes </a>
           
            
            
        </display:column>
        <display:column  titleKey="production.reviewtables.qaresult" headerClass="tableHeader" 
        	style="text-align:center;">
          <a href="javascript:popupQAResults(${snapShot.contextIdentifier.contextId});"> Result </a>
        </display:column>
        
        
    </display:table>

</div>