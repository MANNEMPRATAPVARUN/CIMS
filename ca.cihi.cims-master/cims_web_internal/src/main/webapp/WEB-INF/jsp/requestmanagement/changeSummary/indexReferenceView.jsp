<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<html>
<style type="text/css">
	.language-column {
		margin: 0px;
		padding: 4px 0px 4px 5px;
		background: #CDE1E0;
	}

</style>
<link href="css/cims.css" rel="stylesheet">
<title><fmt:message key="index.reference.view.title" /></title>
	<body> 
		<div class="codeText"><b><fmt:message key="index.reference.view.term" /></b>: ${indexTerm} </div>
		<div class="codeText"><b><fmt:message key="index.reference.hierarchicalPath"/></b>: ${indexPath}</div>
	</body>

</html>
