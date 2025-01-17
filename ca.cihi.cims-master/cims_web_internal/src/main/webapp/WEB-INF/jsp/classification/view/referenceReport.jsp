<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
		<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link href="css/cims.css" rel="stylesheet">
	
	<title><c:out value="${title}"/></title>
</head>
<body> 
	<div class="content">
		<form:form method="POST" modelAttribute="viewBean" >
		     <table width="100%" border="0">  
                 <tr>  
                          <th width="10%"></th>  
                          <th width="90%"></th>   
                  </tr>  
                 <c:if test="${viewBean.refNote != null}"> 
			    	 <tr>
			    		<td valign="top">
			    			 <b>
			    			     
									 	<fmt:message key="code.reference.value.notes" />
									
			    			 </b>	
			    		</td>
			    		<td>
			    			${viewBean.refNote}	
			    		</td>
			    	</tr>
		    	</c:if>
		    	<tr/>
		    	<c:forEach items="${viewBean.attributes}" var="attribute"> 
		    		<tr>
			    		<td valign="top">${attribute.code}</td>
			    		<td>${attribute.description} </td>	
		    		</tr>
		    		<c:if test="${attribute.note != null}">
		    		    <tr>
			    		    <td/>
				    		<td>${attribute.note} </td>	
		    		    </tr>
		    		</c:if>
				</c:forEach> 
		    </table>		   
		</form:form>
	</div>
</body>

</html>
