<%@page language="java" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  
   function generateTables(){
	  if(confirm("Are you sure to proceed?")){
		  $('form#generateReleaseTablesCriteria').serialize();
		 
		  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/generateClassificationTables.htm'/>"
		  disableAllButtons();
		  hideInfoMessage();
		  $("#generateReleaseTablesCriteria")[0].submit();
		  setTimeout(startProgress(),10000);
	  }
   }

   function notifyUsersToWrapupWork(){
		  $('form#generateReleaseTablesCriteria').serialize();
		  $("#generateReleaseTablesCriteria")[0].action="<c:url value='/notifyUsersToWrapupWork.htm'/>";
		  disableAllButtons();
		  hideInfoMessage();
		  $("#generateReleaseTablesCriteria")[0].submit();
   }
  
   function disableAllButtons(){
		  $("input[type=button]").attr("disabled", "disabled");
		  $("input[type=button]").attr("class", "disabledButton");
	  }
   
   
   function startProgress(){
	    document.getElementById('progressing').style.display = 'block';
	    var classification = $('input:radio[name=classification]:checked').val();
	    var startTime = new Date();
	    var processInfoMsg="The ${generateReleaseTablesCriteria.currentOpenYear} " + classification +" classification tables are currently in progress for  " + startTime.toString()+",  The process will take about 20 minutes after starting.  <br/>";
	    document.getElementById('processInfoMsg').innerHTML = processInfoMsg;
	    document.getElementById('progressingMsg').innerHTML = 'processing generate files...';
	    showProcessingScreen();
	    refreshProcessingMsg();
	    return true;
	}    
   
   function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}


   function refreshProcessingMsg() 	{
	   var classification = $('input:radio[name=classification]:checked').val();
	   var processInfo = function getProcessInfo() {
 			$.ajax({
 				url: "<%=request.getContextPath()%>/getGeneratingTablesState.htm?classification="+classification ,
 				type: "GET",
 				cache: false,
 				dataType: "html",
 				async: true,
 				success: function(msg){
 				    document.getElementById('progressingMsg').innerHTML =  msg ;
 				},
 				error: function(msg){
 					
 				}
 			});
 		};
 		var processInfoHandle = setInterval(processInfo, 3000);
    } 
   
   function hideInfoMessage(){
	   $("#infoMessage").hide();
    }
   
   function popupUnusedComponentAttributesReport() {
	      var link = "popupUnusedComponentAttributesReport.htm" ;
		  var newwindow = window.open(link, "UnusedComponentAttributesReport", "width=700, height=500 ,resizable=yes,scrollbars=yes ");
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
  
   
  
   <form:form id="generateReleaseTablesCriteria" modelAttribute="generateReleaseTablesCriteria"  method="post" >
    	
    	<div id="progressing"  class="info" style="display: none;" >
    	    <div id="processInfoMsg">
    	    </div>
    	     <div id="progressingMsg"></div>
        </div>
    	
    	<div id ="infoMessage">
    	 <form:errors path="*" cssClass="errorMsg" />
    	
    	 <c:if test='${notificationSent}'>
           <div  class="info"> All Content Developers, Reviewers and Administrators have been notified to wrap up their works.</div>
         </c:if> 
    	
    	 <c:if test='${isAnotherProcessRunning}'>
           <div  class="info"> 
              The ${generateReleaseTablesCriteria.currentOpenYear}  ${generateReleaseTablesCriteria.classification} classification tables is currently in progress for <fmt:formatDate pattern="yyyy-MM-dd" value="${cutDate}" /> by others. 
           </div>
         </c:if> 
         
    	 <c:if test='${generateTablesSuccess}'>
           <div  class="info"> The classification tables package for ${generateReleaseTablesCriteria.classification}
              has been successfully created for year ${generateReleaseTablesCriteria.currentOpenYear} . Please access the generated package in Review Table tab.
           </div>
         </c:if> 
    	 </div>
    	  <c:set var="cciFrozen"  value="${cciFreezingStatus !=null }"/>
    	  <c:set var="icdFrozen"  value="${icdFreezingStatus !=null }"/>
    	  
    	  
    	  
    	  
    	 <c:set var="noneClassificationFrozen"  value="${icdFreezingStatus==null && cciFreezingStatus ==null }"/>
    	 
    	<table  style="width: 800px;" >			
		    <tr ><td style="width: 20%">
		           <label><span class="required">*</span> Classification:</label>		
		         
		         </td>
		        <td style="width: 80%">
			      <form:radiobutton path="classification" value="ICD-10-CA" />ICD-10-CA &nbsp;
		          <form:radiobutton path="classification" value="CCI" />CCI &nbsp;
		          <%--
		          <form:radiobutton path="classification" value="ICD-CCI" disabled="${not noneClassificationFrozen}"/>ICD-10-CA and CCI
		          --%>
		       </td>	
		    </tr>
		     <tr ><td>
		           <label>&nbsp;&nbsp;Year:</label>		
		         </td>
		        <td >
			      ${currentOpenYear}
			      <form:hidden path="currentOpenYear"/>
		       </td>	
		    </tr>
		    <tr ><td>
		           <label>&nbsp;&nbsp;Audit Reports:</label>		
		         </td>
		        <td >
			      All (Code Description, Validations, Category / Rubric Description)
		       </td>	
		    </tr>
		    <tr ><td>
		           <label>&nbsp;&nbsp;ASCII Files:</label>		
		         </td>
		        <td >
			      All 
		       </td>	
		    </tr>
		    <tr ><td>
		           <label><span class="required">*</span> ASCII File Format:</label>		
		         </td>
		        <td >
			       <%--<form:radiobutton path="fileFormat" value="TAB" checked="checked" />Tab Delimited &nbsp;--%>
		           <form:radiobutton path="fileFormat" value="FIX" checked="checked" />Fixed-Width &nbsp;
		       </td>	
		    </tr>
		    <tr ><td style="vertical-align: top">
		           <label>&nbsp;&nbsp;Note:</label>		
		         </td>
		        <td >
			       <form:textarea path="note" />
		       </td>	
		         <script>
    			        <!-- CKEDITOR.replace( "note" ,{width : 500});-->
    			     CKEDITOR.replace( "note");
                 </script>
		    </tr>
		    <tr ><td>
		           <label>&nbsp;&nbsp;Date:</label>		
		         </td>
		        <td>
			         <fmt:formatDate pattern="yyyy-MM-dd" value="${cutDate}" />
		       </td>	
		    </tr>
		    <tr ><td>
		           <label>&nbsp;&nbsp;Run By:</label>		
		         </td>
		        <td>
			      ${currentUser.firstname}&nbsp;${currentUser.lastname} 
		       </td>	
		    </tr>
		      <tr ><td>
		           &nbsp;&nbsp;
		         </td>
		        <td >
			       <div class="btn_alignRight"> 
			      
			       <a href="javascript:popupUnusedComponentAttributesReport();">Unused Component/Attribute Report </a>&nbsp;&nbsp;
			      
			       <input type="button"  value="Notify CIMS Users" class="button" onclick="javascript:notifyUsersToWrapupWork();" >&nbsp;&nbsp; 
			       <input type="button"  value="Generate Tables" class="button" onclick="javascript:generateTables();" >&nbsp;&nbsp; 
			       </div>
		       </td>	
		    </tr>
	</table>
 
  </form:form>
 </div>