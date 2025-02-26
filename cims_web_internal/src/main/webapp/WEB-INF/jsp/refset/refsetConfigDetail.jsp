<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<c:set var="ACTIVE" value="<%=ca.cihi.cims.model.Status.ACTIVE.getCode()%>" />

<script>
	$(document).ready(function(){
		$("#create").click(function(event) {
			event.preventDefault();
		});
		$("#cancel").click(function(event) {
			event.preventDefault();
		});
	});
	
	function createRefset() {	 
		$.ajax({
		        url: "<c:url value='/refset/refsetConfigDetail.htm'/>",
		        data: $("#refsetConfigDetailForm").serialize(),
		        type: "POST",
		        async: false,
		        cache: false,
		        success: function(data){
					if (data.status=='SUCCESS'){
						var editPage = "<c:url value='/refset/refsetEditDetail.htm?contextId="+data.contextId+"&elementId="+data.elementId+"&elementVersionId="+data.elementVersionId + "'/>";
				 		window.location.href = editPage;			 				 			
					}else {
						var errorMessages = "";
						for (var i = 0; i < data.errors.length; i++) {
							var item = data.errors[i];
							errorMessages += item;
							errorMessages += "<br/>";
						}
						$("#refSetSystemMessage").html(errorMessages);
					}
		        },
		        error: function(data){  	
		        	var errorMessages = "System error occurred, please contact System administrator.";
		        	$("#refSetSystemMessage").html(errorMessages);
		        }		       
		    });
	}

	function cancelRefset() {	
		var refsetCatalogPage = "<c:url value='/refset/refsetCatalog.htm'/>";
		window.location.href = refsetCatalogPage;			 
	}


</script>

 <h4 class="contentTitle">Create new Refset >> Refset Configuration Details</h4>
 <span class="required">* </span><span>Mandatory Fields</span>

 <div class="content">
	<form:form method="POST" modelAttribute="viewBean" id="refsetConfigDetailForm" >
	      <table style="width:80%;">
	      	<tr>
	      		<td style="width:80%;">&nbsp;</td>
	      		<td style="width:20%;align:right;">
		   		  	<input class="button" type="submit" name="create" id="create" value="Create" onclick="createRefset()"/>
		     		<input class="button" type="submit" name="cancel" id="cancel" value="Cancel" onclick="cancelRefset()"/>
		     	</td>
     		</tr>
	      </table>
          <div id="refSetSystemMessage" class="errorMsg">
          </div>            
         <table>	
        	<tr>
        		<td width="50%" align="left">Refset Code <span class="required">*</span>:&nbsp;<input id="refsetCode" name="refsetCode" size="10" maxlength="10"/></td>
        		<td width="50%" align="left">&nbsp;</td>
        	</tr>
        	<tr>
        		<td width="50%" align="left">Refset Name (English) <span class="required">*</span>:&nbsp;<input id="refsetNameENG" name="refsetNameENG" size="50" maxlength="100"/></td>
        		<td width="50%" align="left">Refset Name (French) :&nbsp;<input id="refsetNameFRE" name="refsetNameFRE" size="50" maxlength="100"/></td>
        	</tr>
        	<tr>
        		<td width="50%" align="left">Catalog Category <span class="required">*</span>:&nbsp;  
        			<form:select path="categoryId">
        			    <form:option value=""></form:option>
			        	<c:forEach items="${CategoryList}" var="category"  varStatus="loop">
			        		<c:if test="${category.status == ACTIVE}">
				        	<option value="${category.auxTableValueId}">${category.auxEngLable}</option>
				        	</c:if>  
				    	 </c:forEach>
			        </form:select>	 
			    </td>
        		<td width="50%" align="left">&nbsp;</td>
        	</tr>
        	<tr>
        		<td width="50%" align="left">Refset Effective Year From <span class="required">*</span>:&nbsp;  
        			<form:select path="effectiveYearFrom">
			        	<c:forEach items="${effectiveYearFromList}" var="effectiveYearFrom"  varStatus="loop">
				        	<option value="${effectiveYearFrom}" ${loop.index==5? 'selected' : ''}>${effectiveYearFrom}</option>
				    	</c:forEach>
			        </form:select>					
			    </td>
        		<td width="50%" align="left">&nbsp;</td>
        	</tr>
        	<tr>
        		<td width="50%" align="left">Refset Effective Year To:&nbsp;  
        			<form:select path="effectiveYearTo">
        			    <option value="">&nbsp;</option>
			            <c:forEach items="${effectiveYearToList}" var="effectiveYearTo"  varStatus="loop">
				        	<option value="${effectiveYearTo}">${effectiveYearTo}</option>
				    	</c:forEach>
			        </form:select>	
			    </td>
        		<td width="50%" align="left">&nbsp;</td>
        	</tr>
        </table>

        <div>	      
	        <fieldset>
	            <legend>Classification Version Year<span class="required">*</span></legend>	                                   
	            <div><label for="ICD10CAYear" class="mandatory">ICD-10-CA Classification Year:&nbsp;</label>   	                 
	                 <form:select path="ICD10CAContextInfo">
	                     <c:forEach items="${ICD10CAContextInfoList}" var="infoItemICD10CA"  varStatus="loop">
				        	<option value="${infoItemICD10CA.contextBaseInfo}">${infoItemICD10CA.versionCode}</option>
				    	 </c:forEach>
	                 </form:select>	                			                
			    </div>
			    <div><label for="CCIYear" class="mandatory">CCI Classification Year:&nbsp;</label>   
	                 <form:select path="CCIContextInfo">
	                     <c:forEach items="${CCIContextInfoList}" var="infoItemCCI"  varStatus="loop">
				        	<option value="${infoItemCCI.contextBaseInfo}">${infoItemCCI.versionCode}</option>
				    	 </c:forEach>
	                 </form:select>			                
			    </div>
			    <div><label for="SCTVersion" class="mandatory">SNOMED CT Version Year:&nbsp;</label>   
	                 <form:select path="SCTVersionCode">
	                     <form:options itemValue="versionCode" itemLabel="versionDesc" items="${SCTVersionList}" />
	                 </form:select>			                
			    </div>			
	        </fieldset>   
        </div>    
        <div>
        	<table>
        		<tr>
        			<td>Refset Purpose<span class="required">*</span>:&nbsp;</td>
        			<td><textarea id="definition" name="definition" rows="5" cols="100" maxlength="500"></textarea></td>
        		</tr>
        	</table>        	
        </div>       
	</form:form>
</div>

