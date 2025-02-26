<!DOCTYPE html> 
<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>

<script type="text/javascript">  
var lastDetailedLogSeqNum = -1;
window.setInterval(function() {
	$.ajax({
		    url: "<c:url value='/admin/getExportFolioDetailLog.htm?lastDetailedLogSeqNum='/>" + lastDetailedLogSeqNum,
		    cache: false,
		    type: "GET",
		    dataType: "json",
		    success: function(data) {
		    	 $.each(data, function(key, value) {  
		    		 lastDetailedLogSeqNum = key;
                 	 $("#detailedLogs").append(value + "\n\r");

                 	  //move the cursor to the bottom of the textarea
                 	  $('#detailedLogs').scrollTop($('#detailedLogs')[0].scrollHeight);
                   });    
			}
		});
	}, 5000);

</script> 




<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	<body  style="height:100%;">
	
	 <div class="fixed" style="width: 100%; overflow: visible !important; " >
    		 <div class="contentContainer" >
				<div class="content">
	               
	               
	              
				   <form:form id="queryCriteria"  modelAttribute="queryCriteria" method="post" >
				     HTML Generation Detail Log
				     <table id="releaseDetails" style="width: 100%; margin-top: 0px;" class="listTable">
				        <tr class="odd">
                           <td>Classification:  </td>
                           <td> ${queryCriteria.classification}  </td>
                       </tr>				      
				       <tr class="even">
                           <td>Fiscal Year:  </td>
                           <td> ${queryCriteria.year}  </td>
                       </tr>	
                       <tr class="odd">
                           <td>Language:  </td>
                           <td> ${queryCriteria.language}  </td>
                       </tr>	
                       <tr class="even">
                           <td>Detailed Logs:  </td>
                           <td> 
                               <textarea id="detailedLogs" style="height: 280px; width: 500px;" readonly wrap="off"></textarea>                                 
                            </td>
                       </tr>
                       
				     </table>
				     
				     
				  </form:form>

	
	
	
				</div>
        	 </div>
	  </div>
		    
	

  </body>


</html>
