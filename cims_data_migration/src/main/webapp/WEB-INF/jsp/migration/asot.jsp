<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.menu.create.context.header.name"/>
</jsp:include>
 <script type="text/javascript">  
   function generateTables(){
	  if(confirm("Are you sure to proceed?")){
		  
		  hideInfoMessage();
		  $("#asot")[0].submit();
		  setTimeout(startProgress(),10000);
	  }
   }
   
   
   function startProgress(){
	    document.getElementById('progressing').style.display = 'block';
	    var startTime = new Date();
	    var processInfoMsg="The generation of ASOT tables are currently in progress for  " + startTime.toString()+",  The process will take about 15 minutes after starting.  <br/>";
	    document.getElementById('processInfoMsg').innerHTML = processInfoMsg;
	    document.getElementById('progressingMsg').innerHTML = 'generating tables...';
	    showProcessingScreen();
	    refreshProcessingMsg();
	    return true;
	}    
   
   function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	}


   function refreshProcessingMsg() 	{
	   var fiscalYear = $('#fiscalYear').val();
	   var processInfo = function getProcessInfo() {
 			$.ajax({
 				url: "<%=request.getContextPath()%>/asot/status.htm?fiscalYear="+fiscalYear ,
 				type: "GET",
 				cache: false,
 				dataType: "text",
 				async: true,
 				success: function(msg){
 				    document.getElementById('progressingMsg').innerHTML =  msg ;
 				},
 				error: function(msg){
 					
 				}
 			});
 		};
 		var processInfoHandle = setInterval(processInfo, 10000);
    } 
   
   function hideInfoMessage(){
	   $("#infoMessage").hide();
    }
   
   
 </script> 

<h3 class="contentTitle"><fmt:message key="cims.menu.create.context.header.name" /> </h3> 
<div class="content">
	<form:form method="POST" modelAttribute="viewBean" id="asot">
	
      	<c:if test='${not empty errorMessage}'>         
               <span style="color: #ff0000; font-weight: bold;">   
                 <fmt:message  key="${errorMessage}" />   
               </span>  
          </c:if>  
          
         <c:if test='${not empty successMessage}'>         
               <span style="color: #00ff00; font-weight: bold;">   
                 <fmt:message  key="${successMessage}" />   
               </span>  
          </c:if> 
          
        <div id="progressing"  class="info" style="display: none;" >
    	    <div id="processInfoMsg">
    	    </div>
    	     <div id="progressingMsg"></div>
        </div>
    	
    	<div id ="infoMessage">
    	 <form:errors path="*" cssClass="errorMsg" />
    	
    	 <c:if test='${isAnotherProcessRunning}'>
           <div  class="error"> 
              ${exception.message }
           </div>
         </c:if> 
         
    	 <c:if test='${generateTablesSuccess}'>
           <div  class="info"> 
           		${message }
           </div>
         </c:if> 
         
         <c:if test='${generateTablesFail}'>
           <div  class="error"> 
           		${exception.message }
           </div>
         </c:if> 
    	 </div>
	
		<table border="0" >	
			<tr ><td style="width: 150px"><label><fmt:message key="cims.migrationViewer.selectYear" /></label></td>		
			 	<td align="left" >	
				 	<form:select id="fiscalYear" path="fiscalYear">
				 		<c:forEach var="versionYear" items="${versionYears}">
			              <form:option value="${versionYear}"> FY${versionYear}</form:option>
			            </c:forEach>
	              	</form:select>  
				</td>
			</tr>	
			<tr>	
			  	<td colspan="2" align="center" >
					<input class="button" type="button" name="generate" id="generate" value="<fmt:message key='button.generateasot'/>" onclick="generateTables()"/>
				</td>
			</tr>
		</table>
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


