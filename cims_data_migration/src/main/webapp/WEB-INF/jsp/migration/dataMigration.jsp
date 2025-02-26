<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.menu.migrate.data.header.name"/>
</jsp:include>


<h3 class="contentTitle"><fmt:message key="cims.title.migration" /> </h3> 
<div class="content">
	<form:form method="POST" modelAttribute="viewBean">
	
      	<c:if test='${not empty errorMessage}'>         
               <span style="color: #ff0000; font-weight: bold;">   
                 <fmt:message  key="${errorMessage}" />   
               </span>  
          </c:if>  
	
		<table border="0" >	
			<tr ><td style="width: 150px"><label><fmt:message key="cims.migrationViewer.selectClassification" /></label></td>
				<td align="left" >		        
			        <form:select id="classification" path="classification">      
	                     <form:options items="${classificationList}" itemLabel="value"  
	                                     itemValue="key"/>   
	              	</form:select>		        
				</td>
			</tr>	
			<tr ><td style="width: 150px"><label><fmt:message key="cims.migrationViewer.selectYear" /></label></td>		
			 	<td align="left" >	
				 	<form:select id="fiscalYear" path="fiscalYear"> 
	                     <option value="2015" >FY2015</option>
	              	</form:select>  
				</td>
			</tr>	
			<tr>	
			  	<td colspan="2" align="center" >
					<input class="button" type="submit" name="migrate" id="migrate" value="<fmt:message key='button.migrate'/>"  />
					<input class="button" type="submit" name="viewMigrationReport" id="viewMigrationReport" value="<fmt:message key='button.viewMigrationRepot'/>"  />
				</td>
			</tr>
		</table>
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


