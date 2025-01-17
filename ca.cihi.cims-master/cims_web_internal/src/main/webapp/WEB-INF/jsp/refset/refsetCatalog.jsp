<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<style type="text/css">
#auxTableValue input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>
<script type="text/javascript">
	function submitForm(){
		showLoading("<fmt:message key='progress.msg.save'/>");
		document.refsetCatalog.submit();
	}
	
</script>
<h4 class="contentTitle"> 
	<fmt:message key="cims.menu.refset" /> &#62;
			<fmt:message key="cims.menu.refset.sub.catalog" />
</h4>
<div class="content">
<form:form modelAttribute="refsetCatalogViewBean" name="refsetCatalog" action="refsetCatalog.htm">
<fieldset>

<legend>
	<fmt:message key="refset.catalog.manage" />
</legend> 


<div style="width: 100%; overflow: hidden;">
    <div style="float: left;">
    	<div id="loadingInfo" class="info" style="display: none; width: 800px; padding-top: 0.5em;padding-bottom: 0.5em;">Loading</div>
    </div>
    <div style="float: right;">&nbsp;</div>
</div>

	<table border="0" style="margin-bottom:0px; ">
	     <tr>
	     <security:authorize access="hasAnyRole('ROLE_ADMINISTRATOR','ROLE_REFSET_DEVELOPER')"> 
	  		<td class="fieldlabel">
	  			<fmt:message key="refset.category.select" />
	  			&nbsp;&nbsp;
	  			<form:select path="refsetCategory" items="${refSetCategoryList}" itemLabel="value" itemValue="key" />
	  			&nbsp;&nbsp;               
	        	<input id="refsetCategory" class="button" type="button" onclick="submitForm();" 
	        		value="<fmt:message key='refset.catalog.get.button'/>" />
	        </td>
	        	<td>
					<input id="disableOrEnableRefset" class="button" type="button" onclick="window.location.href='<c:url value='refsetStatus.htm'/>'"
					value="<fmt:message key='refset.disable.enable.refset'/>"/>
				</td>
				<td>
					<input id="creatNewRefset" class="button" type="button" onclick="window.location.href='<c:url value='refsetConfigDetail.htm'/>'"
					value="<fmt:message key='refset.create.new.refset'/>"/>
				</td>
			</security:authorize>
			<td>
				<input id="Close" class="button" type="button" style="float: right;" onclick="window.location.href='<c:url value='/myhome.htm'/>'"
				value="<fmt:message key='close.refset.category.list'/>"/>
			</td>
		</tr>  
	</table>
	
    <c:if test="${not empty refsetCatalogViewBean.refsetCatalogBeanList}">
	<display:table name="refsetCatalogViewBean.refsetCatalogBeanList" id="refsetCatalog" class="listTable" defaultsort="2"
		style="width: 100%; table-layout:fixed;" requestURI="" excludedParams="*">
		<display:column property="refsetName" class="refsetName" sortable="true" titleKey="refset.catalog.refset.name" headerClass="tableHeader" />
		<display:column sortable="false" titleKey="refset.catalog.refset.version.name" headerClass="tableHeader" > 
		<a href="refsetEditDetail.htm?contextId=${refsetCatalog.contextId}&elementId=${refsetCatalog.elementId}&elementVersionId=${refsetCatalog.elementVersionId}">${refsetCatalog.refsetVersionName}</a>
		</display:column>	
		<display:column property="category" class="category" sortable="true" titleKey="refset.catalog.refset.category" headerClass="tableHeader" />
	</display:table>
    </c:if>
</fieldset> 

</form:form>
</div>