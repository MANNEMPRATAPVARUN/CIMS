<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="../common/header-menu.jsp">
	<jsp:param name="titleKey" value="cims.menu.transform.index.data.header.name"/>
</jsp:include>

<h3 class="contentTitle"><fmt:message key="cims.title.index.transformation" /> </h3> 
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
		                  <form:options items="${classificationList}" itemLabel="value"   itemValue="key"/>     
		             </form:select>                    
		         </td>  
             </tr>     
            <tr ><td style="width: 150px"><label><fmt:message key="cims.migrationViewer.selectYear" /></label></td>           
                     <td align="left" >        
                            <form:select id="fiscalYear" path="fiscalYear">        
				                 <form:options items="${versionList}" itemLabel="value"  itemValue="key"/>     
				             </form:select>    
                     </td>  
             </tr>
             <tr ><td style="width: 150px"><label><fmt:message key="cims.index.transformation.bookIndexType" /></label></td>           
                     <td align="left" >        
                            <form:select id="bookIndexType" path="bookIndexType"> 
		                      		<option value="A" selected>Alphabetical</option>
		                      		<option value="D">Drugs and Chemicals</option>	
		                      		<option value="E">External Causes of Injury</option>
		                      		<option value="N">Neoplasms</option>
				             </form:select>    
                     </td>  
             </tr>
             <tr ><td style="width: 150px"><label><fmt:message key="cims.index.transformation.language" /></label></td>           
                     <td align="left" >        
                            <form:select id="language" path="language">
			                      	<option value="ENG" selected>English</option>
			                      	<option value="FRA">French</option>
				             </form:select>    
                     </td>  
             </tr>      

			<tr>	
			  	<td colspan="2" align="center" >
					<input class="button" type="submit" name="transform" id="transform" value="<fmt:message key='button.transform'/>"  />
					<input class="button" type="submit" name="viewTransformationReport" id="viewTransformationReport" value="<fmt:message key='button.viewTransformationRepot'/>"  />
				</td>
			</tr>
		</table>
	</form:form>
</div>
<jsp:include page="../common/footer-menu.jsp"/>


