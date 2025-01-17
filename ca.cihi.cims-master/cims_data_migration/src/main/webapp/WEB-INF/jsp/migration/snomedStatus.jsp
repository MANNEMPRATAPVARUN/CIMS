<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.data.snomed.header.name"/>
</jsp:include>

<script type="text/javascript">  
	 $(document).ready(function(){    
	     //  alert("enter ready");  
		 setTimeout(startProgress(),10000); 
	});  
 
   function startProgress(){
	    var sctVersion = $('#sctVersion').val();
	    var code = sctVersion.split("_")[0];
	    var desc = sctVersion.split("_")[1];
	    document.getElementById('progressing').style.display = 'block';
	    document.getElementById('loadingVersion').innerHTML = desc;
	    var startTime = new Date();
	    var processInfoMsg="The loading of SNOMED tables are currently in progress for  " + startTime.toString()+",  The process will take about 30 minutes after starting.  <br/>";
	    document.getElementById('processInfoMsg').innerHTML = processInfoMsg;
	    document.getElementById('progressingMsg').innerHTML = 'Processing SNOMED tables for SCTVersion '+desc;
	    showProcessingScreen();
	    refreshProcessingMsg(code,desc);
	    return true;
	}    
   
   function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}

   function refreshProcessingMsg(code,desc) 	{
	   var processInfo = function getProcessInfo() {
 			$.ajax({
 				url: "<%=request.getContextPath()%>/snomedProcessingStatus.htm?sctVersionCode="+code,
 				type: "GET",
 				cache: false,
 				dataType: "text",
 				async: true,
 				success: function(msg){
 				    document.getElementById('progressingMsg').innerHTML =  msg ;
 				    document.getElementById('loadingVersion').innerHTML = desc; 				    
 				    document.getElementById('loadingStatus').innerHTML = msg; 	
 					if (msg=="Completed" || msg=="Failed in loading file"){
 	 					hideProcessingScreen()
 	 					clearInterval(processInfoHandle); 	 				
 				    }			    	
 				}, 
 				error: function(msg){
 					hideProcessingScreen()
 					clearInterval(processInfoHandle); 	 
 				}
 			});
 		};
 		 var processInfoHandle = setInterval(processInfo, 60000);
    }

	function hideProcessingScreen() {
		$("*").css("cursor", "auto");
		$('.modal').remove();
	}
    
 </script> 

<h3 class="contentTitle"><fmt:message key="cims.data.snomed.header.name" /> </h3> 
 <div class="content">
		
      	<c:if test='${not empty exception}'>         
               <span style="color: #ff0000; font-weight: bold;">   
               	${exception}                    
               </span>  
          </c:if>  
          
         <c:if test='${not empty successMessage}'>         
               <span class="lsSuccessMsg">     
                 ${successMessage}
               </span>  
          </c:if> 
	
	     <div id="progressing"  class="info" style="display: none;" >
    	    <div id="processInfoMsg">
    	    </div>
    	     <div id="progressingMsg"></div>
         </div>

        <table class="lsTable">	
        	<tr>
        		<td width="25%" align="left" class="title1">SNOMED CT Release</td>
        		<td width="15%" align="left" class="title1">Load Status</td>
        	</tr>
		    <c:forEach var='sctVersionActive' items='${sctVersionsActive}'>	
		        <tr>	
		     	  <td width="25%" align="left" class="content1">
		     		${sctVersionActive.versionDesc}
		     	  </td>
		     	  <td width="15%" align="left" class="content1">
		     		<%-- ${sctVersionActive.statusCode} --%>
		     		Completed
		     	  </td>
		         </tr>	   	
		     </c:forEach>
		     <tr>
		         <td width="25%" align="left" class="content1"><span id="loadingVersion"></span></td><td width="15%" align="left" class="content1"><span id="loadingStatus"></span>&nbsp;</td>
		    </tr>
        </table>
        
        <input type="hidden" id="sctVersion" value='${sctVersionLoading}'/>
        
     
</div>
<jsp:include page="../common/footer-menu.jsp"/>


