<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.data.migration.header.name"/>
</jsp:include>

<h3 class="contentTitle"><fmt:message key="cims.title.migration" /> </h3> 
<div class="content">
	<form:form method="POST" modelAttribute="viewBean">    
	    <fieldset class="invisible">
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
	    
		    <p>		       
	          <display:table name="sessionScope.logMessageList"  id="logMessage" requestURI="" defaultsort="2" style="width: 80%; margin-top: 20px;" class="listTable" pagesize="999"  >  
	    		   <display:column property="message" escapeXml="true" sortable="true" titleKey="cims.migrationReportViewer.message"/>  
		           <display:column property="messageDate" escapeXml="true" sortable="true" titleKey="cims.migrationReportViewer.messageDate"/>  
		           
		           <display:setProperty name="paging.banner.item_name" value="log message"/>  
		           <display:setProperty name="paging.banner.items_name" value="log messages"/>  	    
	         </display:table>
			</p>
		
		<input type='submit' name='back' id='back' value='<fmt:message key="button.back"/>' class="button" />  
	  </fieldset>
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


