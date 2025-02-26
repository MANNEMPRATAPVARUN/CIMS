<!DOCTYPE html> 

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<html style="height:100%;">
    <%@ include file="/WEB-INF/jsp/common/common-header.jsp" %>
	
	<script>
       $(document).ready(function(){ 
    	   //extra width (in em units) so that arrows don't overlap in items with lots of text
          $("ul.sf-menu").supersubs({extraWidth: 1}).superfish().find('ul').bgIframe({opacity:false}); 
       }); 
     </script>

   
   <body  style="height:100%;">
   <div class="container" style="width: 1366px;"><!-- *** CIHI Logo *** -->
	<div class="span-24 last" style="width: 1366px;height: 102px;"><img src="<cc:resUrl value="/images/banner.png" />"
		alt="CIHI/ICIS Banner"></div>

	<!-- *** Header *** -->
	<div class="span-24 last" style="width: 1366px;">
		<div class="appTitle" style="margin-bottom:0px;">
	        <table><tr>
	          <td width="5%">  
            	<a id="icon_wiki" href="javascript:popupCimsWiki();"><img src='<c:url value="/img/InfoTeal.png" />' style="border:0 none; width: 18px; height: 18px; position: relative; bottom: 0px; left: 0px; background-color:#8dbab8; " ></a>
	          </td>  
	          <td width="85%">  
 		        <div class="appTitle" style="margin-bottom:0px;"><spring:message code="cims.common.header"/>
		        </div>
	          </td>  
	          <td width="10%">  
			    <tiles:insertAttribute name="searchCR" /> 
	          </td>  
	        </tr></table>
		</div>
	</div>

	<!-- *** Top Navigation Menu *** -->
	<div class="span-24 last" style="width: 1366px; overflow: visible !important; background-color:#cde1e0" ><!--- START MENU HTML -->
		<ul class="sf-menu">
			<tiles:insertAttribute name="menu" /> 
		</ul>
	</div>
	
	<div class="span-24 last" style="width: 1366px; overflow: visible !important; ">
	    <div class="wrapper">
		    <tiles:insertAttribute name="refset-header" />
		</div>
	</div>
		  
    <div class="span-24 last" style="width: 1366px; overflow: visible !important; " > 
        <div class="contentContainer" >
			<tiles:insertAttribute name="body" />
        </div>
	</div>
	  
	<div class="span-24 last"  style="width: 1366px;">
		<tiles:insertAttribute name="pageFooter" /> 
	</div>
	
  </div>
  </body>


</html>

