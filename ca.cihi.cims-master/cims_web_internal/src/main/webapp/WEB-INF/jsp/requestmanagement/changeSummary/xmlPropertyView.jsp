<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<html>
<head>
<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css"  media="screen, projection" />
	
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
	<link href="css/cims.css" rel="stylesheet">
<style type="text/css">
	.language-column {
		margin: 0px;
		padding: 4px 0px 4px 5px;
		background: #CDE1E0;
	}
	textarea {
		width:100%;
		height:350px;
		word-wrap: break-word;
		background-color: white;
		font-color: #FF0000;		
	}
	ins {
	    background-color: #c6ffc6;
	    text-decoration: underline;
	}
	
	del {
	    background-color: #ffc6c6;
	}
</style>
</head>
	<body> 
		<div>		             
			<table border="0" cellpadding="0" cellspacing="0" width="100%">						
						<tr>
							<th class="language-column">${category}: ${fieldName} of ${code}</th>
						</tr>
						<c:if test="${not empty xmlString }">
						<tr>
							<td>
								<textarea>${xmlString}</textarea>
							</td>
						</tr>
						</c:if>
						<c:if test="${empty xmlString }">
						<tr>
							<td>
								${xmlDifference }
							</td>
						</tr>
						</c:if>
		    </table>	
			 
		  </div>
	</body>

</html>
