<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"> 
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html>
<head>

	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<!--Blueprint Framework CSS -->
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
	<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
	<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">
	<link rel="stylesheet" media="screen" href="<cc:resUrl value="/css/nav/superfish-dropdown.css" />">
	<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">

	<link rel="stylesheet" type="text/css" href="<c:url value="/css/skin-vista/ui.dynatree.css"/>">
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/cims.css"/>" >
	
	<!-- JQuery dependencies, always include -->
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.cookie.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/jquery/jquery.dynatree.js"/>"></script>


	<!-- Javascript for menu-->
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/hoverIntent.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/superfish.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/supersubs.js" />"></script>
	<script type="text/javascript" src="<cc:resUrl value="/js/nav/jquery.bgiframe.min.js" />"></script>

  <%-- Add in the Javascript that triggers the jQuery code. --%>    
  <script type="text/javascript">    
       
  // jQuery - Bind a function to call once the document is loaded and Ready    
    $(document).ready(function(){    
                //  alert("enter ready");  
           callVersionList();    
    });  
     
    function callVersionList(){    
           //   alert("enter callVersionList");  
                        
          $.get("<c:url value='/getAjaxVersions.htm'/>", {classification: $("#classification").val()}, function(data) {    
                    $("#fiscalYear").empty();    
                   $.each(data, function(key, val) {    
                         var optionvalue= '<option value="' + key +'" >' + val + '</option>';    
                          $("#fiscalYear").append(optionvalue);    
                    });    
          });    
          }   
     
          //jQuery - Bind a function to call whenever the classification input value changes    
          $("#classification").change(function(){    
                  alert("classification change");  
                  callVersionList();    
          });  
      
    </script> 

	<%
		// If the "titleKey" parameter has been set, then we should use that, otherwise set a default
		
		String TITLE_KEY_PARAM_NAME = "titleKey";
		String DEFAULT_TITLE_KEY = "cims.common.header";
		
		String keyParam = request.getParameter(TITLE_KEY_PARAM_NAME);
	
		if(keyParam == null || keyParam.trim().equals("")) {
			request.setAttribute(TITLE_KEY_PARAM_NAME, DEFAULT_TITLE_KEY);
		} else {
			request.setAttribute( TITLE_KEY_PARAM_NAME, keyParam );
		}
	%>
	<title><fmt:message key="${titleKey}" /></title>
</head>

<body>
