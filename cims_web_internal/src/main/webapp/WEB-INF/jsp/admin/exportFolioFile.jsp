<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<script type="text/javascript" src="<c:url value="/ckeditor/ckeditor.js"/>"></script>
<script type="text/javascript">  

	function generateFolioFiles(){
    	$("#generateFolioCriteria")[0].action="<c:url value='/admin/GenerateFolioFile.htm'/>";
    	$("#generateFolioCriteria")[0].method="POST";
		$("#generateFolioCriteria")[0].submit();
	}

	function viewFolioGenerationStatus(){
    	$("#generateFolioCriteria")[0].action="<c:url value='/admin/exportFolioFileStatus.htm'/>";
    	$("#generateFolioCriteria")[0].method="GET";
		$("#generateFolioCriteria")[0].submit();
	}


	function callForContextYears(){
   	 var baseClassification= $("#baseClassification").val();
   	 $.ajax({
   		    url: "<c:url value='/admin/ExportFolioFile/GetContextYears.htm'/>?baseClassification=" +baseClassification,
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
	<fmt:message key="cims.menu.administration" /> &#62; <fmt:message key="admin.exportFolioFile" />

</h4>

<div class="content">
	<form:form method="POST" id="generateFolioCriteria" modelAttribute="generateFolioCriteria">
	
		<table border="0">

			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportFolioFile.selectClassification" /></label></td>
				<td align="left">
                   <form:select  id="baseClassification" path="baseClassification" onchange="javascript:callForContextYears();" >
    				   <c:forEach var="baseClassification" items="${baseClassifications}">
  	  	                  <form:option value="${baseClassification}"> ${baseClassification}  </form:option>
  	  	               </c:forEach>
    			   </form:select>				
			</tr>
			 
			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportFolioFile.fiscalYear" /></label></td>
				<td align="left">
				    <form:select id="fiscalYear" path="fiscalYear">
    					 <c:forEach var="contextYear" items="${contextYears}">
  	  	                   <form:option value="${contextYear}"> ${contextYear} </form:option>
  	  	                </c:forEach>
    			    </form:select>
			</tr>

			<tr>
				<td style="width: 150px"><label><fmt:message key="admin.exportFolioFile.language" /></label></td>
				<td align="left">
					<form:select id="language" path="language">
						<option value="ENG" selected>English</option>
						<option value="FRA">French</option>
					</form:select>
				</td>
			</tr>
 
			<tr>
				<td colspan="2" align="center">
					<input class="button" type="button" name="generate" id="generate" onclick="generateFolioFiles()" value="<fmt:message key='admin.exportFolioFile.button.generate'/>" /> 
					<input class="button" type="button" name="viewGenerationReport"	onclick="viewFolioGenerationStatus()" id="viewGenerationReport" value="<fmt:message key='admin.exportFolioFile.button.viewGenerationReport'/>" />
				</td>
			</tr>
		</table>

	</form:form>
</div>

