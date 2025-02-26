<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html>
<head>
<!--Blueprint Framework CSS -->
<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/screen.css" />" type="text/css" media="screen, projection" />
<link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/print.css" />" type="text/css"  media="print" />
<!--[if IE]><link rel="stylesheet" href="<cc:resUrl value="/css/blueprint/ie.css" />" type="text/css" media="screen, projection" /><![endif]-->
<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/jquery/jquery-ui.css" />">
<link rel="stylesheet" media="screen" href="<cc:resUrl value="/css/nav/superfish-dropdown.css" />">
<link rel="stylesheet" type="text/css" href="<cc:resUrl value="/css/main.css" />">
<!-- JQuery dependencies, always include -->
<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/jquery/jquery-ui-1.8.10.custom.min.js" />"></script>

<!-- sandy added for dynatree -->
<script type="text/javascript" src="<c:url value="/jquery/jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery-ui.custom.js"/>"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.cookie.js"/>"></script>
<script type="text/javascript" src="<c:url value="/jquery/jquery.dynatree.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/skin-vista/ui.dynatree.css"/>">

<!-- Javascript for menu-->
<script type="text/javascript" src="<cc:resUrl value="/js/nav/hoverIntent.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/nav/superfish.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/nav/supersubs.js" />"></script>
<script type="text/javascript" src="<cc:resUrl value="/js/nav/jquery.bgiframe.min.js" />"></script>
<script>
    $(document).ready(function(){ 
        $("ul.sf-menu").supersubs().superfish().find('ul').bgIframe({opacity:false}); 
    }); 
</script>
<title><fmt:message key="cims.title"/></title>
</head>

<body>
<div class="container"><!-- *** CIHI Logo *** -->
<div class="span-24 last"><img src="<cc:resUrl value="/images/banner.png" />" alt="CIHI/ICIS Banner"></div>
<!-- *** START Header *** -->
<div class="span-24 last">
<div class="appTitle"><fmt:message key="cims.name"/>
</div>
</div>
<!-- *** END Header *** -->

<!-- *** START Navigation Menu *** -->
<div class="span-24 last" style="overflow: visible !important">
<ul class="sf-menu">
<%@ include file="menu.jsp"%>
</ul>
</div><!-- *** END Navigation Menu *** -->

<!-- Content Container -->
<div class="span-24 last" style="overflow:auto; overflow-y: hidden;">
<div class="contentContainer">
