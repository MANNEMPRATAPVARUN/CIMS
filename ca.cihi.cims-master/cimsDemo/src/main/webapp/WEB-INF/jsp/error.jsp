<%@ include file="common/header.jsp"%>

<SCRIPT LANGUAGE="JavaScript">
   function toggle(){
	   var errorDetails = document.getElementById('details');
	  
   	   if (errorDetails.style.display == "none"){
   			errorDetails.style.display="";
   			document.getElementById('viewDetails').style.display="none";
   			document.getElementById('hideDetails').style.display="";
   	   }else{
   			errorDetails.style.display = "none";
   			document.getElementById('viewDetails').style.display="";
   			document.getElementById('hideDetails').style.display="none";
   	   }
	   
   }
</SCRIPT>
<div id="containerContentRight">
  	<div style="padding-left: 50px; padding-top: 10px;">
		<fmt:message key="common.error.message"/> 
 		<c:if test="${not empty requestScope['javax.servlet.error.exception']}" >
 			   <A HREF="#" onClick="toggle();"><div id="viewDetails"><fmt:message key="error.view.details"/></div><div id="hideDetails" style="display:none"><fmt:message key="error.hide.details"/></div></A>
               <br></br>
               <div id="details" style="display:none")>
                <error:trace value="${requestScope['javax.servlet.error.exception']}"/>
      			</div>
		</c:if>
	</div>
</div>

<%@ include file="common/footer.jsp"%>

