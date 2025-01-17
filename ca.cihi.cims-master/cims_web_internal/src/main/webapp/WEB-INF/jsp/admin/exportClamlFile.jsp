<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  

	function generateClamlFiles(){
    	$("#generateClamlCriteria")[0].action="<c:url value='/admin/GenerateClamlFile.htm'/>";
    	$("#generateClamlCriteria")[0].method="POST";
		$("#generateClamlCriteria")[0].submit();
	}

	function viewClamlGenerationStatus(){
    	$("#generateClamlCriteria")[0].action="<c:url value='/admin/exportClamlFileStatus.htm'/>";
    	$("#generateClamlCriteria")[0].method="GET";
		$("#generateClamlCriteria")[0].submit();
	}


	function callForContextYears(){
   	 var baseClassification= $("#baseClassification").val();
   	 $.ajax({
   		    url: "<c:url value='/admin/ExportClamlFile/GetContextYears.htm'/>?baseClassification=" +baseClassification,
   		    cache: false,
   		    dataType: "json",
   		    success: function(data) {
   		    	   $("#fiscalYear").empty();   
                   $.each(data, function(key, value) {    
                  	  var optionvalue= '<option value="' + value +'" >' + value + '</option>';
                  	  $("#fiscalYear").append(optionvalue);    
                    });    
   		    }
   		});
	}

</script>


<h4 class="contentTitle">
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.exportClamlFile" />

</h4>

<div class="content">
	<form:form method="POST" id="generateClamlCriteria" modelAttribute="generateClamlCriteria">
	
		<table border="0">

			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportClamlFile.selectClassification" /></label></td>
				<td align="left">
                   <form:select  id="baseClassification" path="baseClassification" onchange="javascript:callForContextYears();" >
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	               </c:forEach>
    			   </form:select>				
			</tr>
			 
			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportClamlFile.fiscalYear" /></label></td>
				<td align="left">
				    <form:select id="fiscalYear" path="fiscalYear">
    					 <c:forEach var="contextYear" items="${contextYears}">
  	  	                   <form:option value="${contextYear}"> ${contextYear} </form:option>
  	  	                </c:forEach>
    			    </form:select>
			</tr>

			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportClamlFile.language" /></label></td>
				<td align="left">
					<form:select id="language" path="language">
						<option value="ENG" selected>English</option>
						<option value="FRA">French</option>
					</form:select>
				</td>
			</tr>
 
			<tr>
				<td colspan="2" align="center">
					<input class="button" type="button" name="generate" id="generate" onclick="generateClamlFiles()" value="<fmt:message key='admin.exportClamlFile.button.generate'/>" /> 
					<input class="button" type="button" name="viewGenerationReport"	onclick="viewClamlGenerationStatus()" id="viewGenerationReport" value="<fmt:message key='admin.exportClamlFile.button.viewGenerationReport'/>" />
				</td>
			</tr>
		</table>

	</form:form>
</div>