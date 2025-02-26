<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<jsp:include page="header.jsp">
	<jsp:param name="titleKey" value="${titleKey}"/>
</jsp:include>

<script>
    $(document).ready(function(){ 
        $("ul.sf-menu").supersubs().superfish().find('ul').bgIframe({opacity:false}); 
    }); 
</script>


<div class="container">
	<!-- *** CIHI Logo *** -->
	<div class="span-24 last"><img src="<cc:resUrl value="/images/banner.png" />" alt="CIHI/ICIS Banner"></div>
	
	<!-- *** START Header *** -->
	<div class="span-24 last">
		<div class="appTitle"> <fmt:message key="cims.common.header" /> </div>
	</div>
	<!-- *** END Header *** -->

	<!-- *** START Left Navigation Menu *** -->
	<div class="span-24 last" style="overflow: visible !important">
		<ul class="sf-menu">
			<%@ include file="/WEB-INF/jsp/common/menu.jsp"%>
		</ul>
	</div><!-- *** END Left Navigation Menu *** -->
	
	<!-- Content Container -->
	<div class="span-24 last" style="overflow:auto; overflow-y: hidden;">
		<div class="contentContainer">
	
