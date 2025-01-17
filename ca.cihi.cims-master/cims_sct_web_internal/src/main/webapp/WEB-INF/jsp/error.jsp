<%@ page isErrorPage="true" import="java.io.*" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<h3 class="contentTitle">Error</h3>
<div class="content">
<% request.getSession().setAttribute("javax.servlet.error.exception",request.getAttribute("javax.servlet.error.exception"));%>

<h2>error happened</h2>

<span>
  Error happened in cims sct system, Please click the view button and paste the error message to CIMS Administrator, then close the browser and try a new session 
  <c:if test="${not empty requestScope['javax.servlet.error.exception']}" >
    <div id="viewDetails">
      <input type="button" value='view' class="button" name="view"  onClick="toggle();"/>
    </div>
    <div id="hideDetails" style="display:none">
      <input type="button" value='hide' class="button" name="hide"  onClick="toggle();"/>
    </div>
    <br/>
    <div id="details" style="display:none")>
     <%
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         Exception ex =(Exception) request.getAttribute("exception");
         ex.printStackTrace(pw);
         out.print(sw);
         sw.close();
         pw.close();
     %>
    </div>
  </c:if>
</span>
</div>
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
