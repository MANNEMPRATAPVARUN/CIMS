<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.data.snomed.header.name"/>
</jsp:include>
 <script type="text/javascript">  
   function loadSnomedTables(){
	  if(confirm("Are you sure to proceed?")){
		  
		  $("#snomed")[0].submit();
		  setTimeout(startProgress(),10000);
	  }
   }
      
   function startProgress(){
	    var sctVersion = $('#sctVersion').val();
	    var code = sctVersion.split("_")[0];
	    var desc = sctVersion.split("_")[1];
	    document.getElementById('progressing').style.display = 'block';
	    document.getElementById('loadingVersion').innerHTML = desc;
	    var startTime = new Date();
	    var processInfoMsg="The loading of SNOMED tables are currently in progress for  " + startTime.toString()+",  The process will take about 30 minutes after starting.  <br/>";
	    document.getElementById('processInfoMsg').innerHTML = processInfoMsg;
	    document.getElementById('progressingMsg').innerHTML = 'Uploading SNOMED tables for SCTVersion '+desc;
	    showProcessingScreen();
	    //refreshProcessingMsg(code,desc);
	    return true;
	}    
   
   function showProcessingScreen() {
		$("*").css("cursor", "progress");
		$('body').append('<div class="modal">');
	} 
    
 </script> 

<h3 class="contentTitle"><fmt:message key="cims.data.snomed.header.name" /> </h3> 
 <div class="content">
	<form:form method="POST" modelAttribute="viewBean" id="snomed" enctype="multipart/form-data" >
	
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

        <div>
	        <div align="right">
		        <input class="button" type="submit" name="load" id="load" value="<fmt:message key='button.loadSnomed'/>" onclick="loadSnomedTables()"/>
	        </div>
	        <fieldset>
	            <legend>Load SNOMED CT Release</legend>
	            <div class="title2">Select files to Load SNOMED CT Release</div>                           
	            <div><label for="releaseName" class="mandatory">Release Name :&nbsp;</label>   
	                         <form:select path="sctVersion">
			                     <form:options itemValue="version" itemLabel="versionDesc" items="${sctVersionsPending}" />
			                 </form:select>			                
			                </div>
			    <div>&nbsp;</div>
	            <div><label for="conceptFile" class="mandatory">Concept File :&nbsp;</label><input type="file" name="conceptFile"/></div>
	            <div>&nbsp;</div>
				<div><label for="descFile" class="mandatory">Description File :&nbsp;</label><input type="file" name="descFile"/></div>
				<div>&nbsp;</div>
				<div><label for="descFile" class="mandatory">Relationship File :&nbsp;</label><input type="file" name="relationshipFile"/></div>
				<div>&nbsp;</div>
			    <div><label for="refsetLangFile" class="mandatory">Refset Language File :&nbsp;</label><input type="file" name="refsetLangFile"/></div>
	        </fieldset>    
        </div>
           
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


