<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.menu.transform.tabular.data.header.name"/>
</jsp:include>

<h3 class="contentTitle"><fmt:message key="cims.title.tabular.transformation" /> </h3> 
<div class="content">
	<form:form method="POST" modelAttribute="viewBean" >	
	      <c:if test='${not empty errorMessage}'>         
               <span style="color: #ff0000; font-weight: bold;">   
                 <fmt:message  key="${errorMessage}" />   
               </span>  
          </c:if> 
	    <div id="summary" style="" >
	    	<table>	
				<tr >
					<td style="width: 150px"><label><fmt:message key="cims.migrationReportViewer.classification" /></label>	</td>
					<td align="left" ><form:input path="classification" disabled="disabled"></form:input></td>
			    </tr>	
			    <tr >
					<td style="width: 150px"><label><fmt:message key="cims.migrationReportViewer.fiscalYear" /></label>	</td>
					<td align="left" ><form:input path="fiscalYear" disabled="disabled"></form:input></td>
			    </tr>
			    <tr >
					<td style="width: 150px"><label><fmt:message key="cims.migrationReportViewer.startTime" /></label>	</td>
					<td align="left" ><form:input path="startTime" disabled="disabled"></form:input></td>
			    </tr>
			    <tr >
					<td style="width: 150px"><label><fmt:message key="cims.migrationReportViewer.endTime" /></label>	</td>
					<td align="left" ><form:input path="endTime" disabled="disabled"></form:input></td>
			    </tr>
			</table>
	    </div>
	    
	    <p>
	       <display:table name="sessionScope.errorList"  id="transformationError" requestURI="" defaultsort="1" style="width: 80%; margin-top: 20px;" class="listTable" >  
	           <display:column property="errorId" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.errorId"/>  
    		   <display:column property="conceptCode" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.conceptCode"/>  
	           <display:column property="conceptTypeCode" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.conceptTypeCode"/>  
	           <display:column property="errorMessage" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.errorMessage"/>  
	           <display:column property="xmlString" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.xmlString"/>  
	           <display:column property="createDate" escapeXml="true" sortable="true" titleKey="cims.transformationReportViewer.createDate"/>  
	           	          
	           <display:setProperty name="paging.banner.item_name" value="error"/>  
	           <display:setProperty name="paging.banner.items_name" value="errors"/>  
	   
	      </display:table>
		</p>
		
		<input type='submit' name='back' id='back' value='<fmt:message key="button.back"/>' class="button" />  
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


