<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge">
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
	<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/cims.css" />">
	
	<!-- JQuery dependencies, always include -->
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.cookie.js"/>"></script>	
	<script type="text/javascript" src="<c:url value="/jquery/jquery.floatThead.js"/>"></script>
    <script>
		$(document).ready(function(){
			var validationTable = $("#validation");
			if(validationTable.length){
				validationTable.floatThead({
				    scrollContainer: function($table){
				        return $table.closest("#scrollPane");
				    }
				});
			}
		});
	 </script>
	 
	 <title>
	 	 	<fmt:message key="code.icd.validation.report.title" />
	</title>
</head>
<body> 
	<div class="content">
		<form:form method="POST" modelAttribute="viewBean" >
		    
		    <p align="center">
		          <b>
		    	 	 	<fmt:message key="code.icd.validation.report.header" />
			            <c:out value="${viewBean.conceptCode}"/>
			      </b>
			</p>
		    <p>		    
			    <div id="scrollPane" style="height:350px; overflow: auto;">
			       <display:table name="viewBean.validations"  id="validation" requestURI=""  style="width:100%" class="listTable" >
			      	 	   <display:column property="code" escapeXml="true" titleKey="code.icd.validation.report.codeValue"/>  
				   		   <display:column property="dataHolding" escapeXml="true"  titleKey="code.icd.validation.report.dataHolding"/>  
					       <display:column property="gender" escapeXml="true"  titleKey="code.icd.validation.report.gender"/>  
				    	   <display:column property="ageRange" escapeXml="true"  titleKey="code.icd.validation.report.ageRange"/>  
					           
					       <display:column property="decoratedMrdxMain" escapeXml="true"  titleKey="code.icd.validation.report.mrdxMain"/>  
				           <display:column property="decoratedDxType1" escapeXml="true"  titleKey="code.icd.validation.report.dxType1"/>  
				    	   <display:column property="decoratedDxType2" escapeXml="true"  titleKey="code.icd.validation.report.dxType2"/> 
				    	   <display:column property="decoratedDxType3" escapeXml="true"  titleKey="code.icd.validation.report.dxType3"/> 
				    	   <display:column property="decoratedDxType4" escapeXml="true"  titleKey="code.icd.validation.report.dxType4"/> 
				    	   <display:column property="decoratedDxType6" escapeXml="true"  titleKey="code.icd.validation.report.dxType6"/> 
				    	   <display:column property="decoratedDxType9" escapeXml="true"  titleKey="code.icd.validation.report.dxType9"/> 
				    	   <display:column property="decoratedDxTypeW" escapeXml="true"  titleKey="code.icd.validation.report.dxTypeW"/> 
				    	   <display:column property="decoratedDxTypeX" escapeXml="true"  titleKey="code.icd.validation.report.dxTypeX"/> 
				    	   <display:column property="decoratedDxTypeY" escapeXml="true"  titleKey="code.icd.validation.report.dxTypeY"/> 
					       <display:column property="decoratedNewBorn" escapeXml="true"  titleKey="code.icd.validation.report.newBorn"/>
			          </display:table>
			      </div>
			</p>
		</form:form>
	</div>
</body>

</html>
