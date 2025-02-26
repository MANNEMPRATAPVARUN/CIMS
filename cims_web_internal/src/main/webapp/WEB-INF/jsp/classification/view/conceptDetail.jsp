<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="${CONTENT_TYPE}">
	<link href="css/cims.css" rel="stylesheet">
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
	<title>${viewBean.conceptCode}</title>
</head>
<body>	

	  
	

   

	<div id="conceptDetailPopup">
		<table width="100%" border="0">
			<tr>
				<th width="11%"></th>
				<th width="7%"></th>
				<th width="82%"></th>
			</tr>
			<td>${viewBean.shortPresentation}</td>
			</table>
	</div>
	
	<script>
      $( "a" ).click(function( event ) {
         event.preventDefault();
         var targetLink = this.href ;
         //alert (targetLink);
         window.opener.focus();
         window.opener.location.href=targetLink;
         window.close();
      });
    </script>
	
</body>
</html>
