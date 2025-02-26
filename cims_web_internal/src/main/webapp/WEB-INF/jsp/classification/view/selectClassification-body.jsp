
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>


<script language="JavaScript" type="text/javascript" >
	function popup(classification, contextId, language) {
	    link = "viewClassification.htm?classification=" + classification + "&contextId=" + contextId + "&language=" + language;
		newwindow = window.open(link, contextId + language, 'resizable=yes');
		newwindow.moveTo(0,0);
		newwindow.resizeTo(screen.width, (screen.height-(screen.height/20)));
		if (window.focus) {newwindow.focus();}
		return false;
	}
</script>

<h4 class="contentTitle">${param.classification}</h4> 

<div class="content">

<fieldset>
   <legend>${param.classification}</legend>

<form:form method="GET" modelAttribute="viewerModel" >
	<table  border="0" >			
		<tr ><td style="width: 10%">
		<form:hidden path="language" value="${currentUser.languagepreference}"/>
		<label><fmt:message key="cims.icd10.conceptViewer.selectYear" /></label>		
		</td>
		<td style="width: 10%">
			<form:select path="contextId" >
			    <c:forEach var="contextIdentifier" items="${contextIdentifiers}">
  	  	                   <form:option value="${contextIdentifier.contextId}"> FY${contextIdentifier.versionCode} </form:option>
  	  	        </c:forEach>
			</form:select>
		</td>	
	  	<td style="width: 10%">
			<input class="button" type="button" value="<fmt:message key='cims.icd10.conceptViewer.submitButton'/>" 
			 onClick="popup('${param.classification}', viewerModel.contextId.value ,viewerModel.language.value)"  />
		</td>
		<td>&nbsp;</td>
		</tr>
	</table>
</form:form>
</fieldset>

</div>



