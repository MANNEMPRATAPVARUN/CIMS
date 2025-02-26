<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>

<meta http-equiv="X-UA-Compatible" content="IE=edge">

<!-- can be any external url -->
<spring:eval var="snomedUrl" expression="@applicationProperties.getProperty('snomedservice.url')"/>
<c:import url="${snomedUrl}" />

<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link href="<c:url value="/css/jquery.splitter.0.20.0.css" />" rel="stylesheet">
<link href="<c:url value="/css/snomed.css"/>" rel="stylesheet">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script	src="<c:url value="/js/jquery.splitter-0.20.0.js" />"></script>
<script	src="<c:url value="/js/handlebars-v4.0.5.js" />"></script>

<script>
	function update(selected) {
		document.getElementById("selectedTermId").innerHTML = selected.attributes['data-selectedTermId'].value;
		document.getElementById("version").innerHTML = selected.attributes['data-version'].value;
		document.getElementById("conceptId").innerHTML = selected.attributes['data-conceptId'].value;
		document.getElementById("conceptType").innerHTML = selected.attributes['data-conceptType'].value;


		document.getElementById("conceptFsnId").innerHTML = selected.attributes['data-conceptFsnId'].value;
		document.getElementById("conceptFsn").innerHTML = selected.attributes['data-conceptFsn'].value;
		document.getElementById("conceptPreferredId").innerHTML = selected.attributes['data-conceptPreferredId'].value;
		document.getElementById("conceptPreferred").innerHTML = selected.attributes['data-conceptPreferred'].value;

		document.getElementById("selectedTermType").innerHTML = selected.attributes['data-selectedTermType'].value;
		document.getElementById("selectedTerm").innerHTML = selected.attributes['data-selectedTerm'].value;
		document.getElementById("selectedTermAcceptability").innerHTML = selected.attributes['data-selectedTermAcceptability'].value;

		var nodes=[], values=[];
		for (var att, i = 0, atts = selected.attributes, n = atts.length; i < n; i++){
		    att = atts[i];
		    nodes.push(att.nodeName);
		    values.push(att.nodeValue);
		}
	}
	
</script>

</head>

<body onLoad="snomedInit();">

	<div class="container">
		<h2>Snomed CT Query Tool</h2>
		
		<label for="release_select_dropdown"> <span
			class="i18n" data-i18n-id="i18n_select_release">Releases to select:</span></label> <br>
		<div class="leftSelect">
			<select id="release_version">
				
		        	<option value="IE20160131" >International Edition 20160131</option>
		    	
			</select>
			<%-- <form:select path="release_version">
	        	<form:options itemValue="versionCode" itemLabel="versionDesc" items="${SCTVersionList}" />
	        </form:select> --%>		
		</div>	
		
		
		Concept ID: <input type="text" name="snomed_concept_id" id="snomed_concept_id"><br>
	  	<!-- Trigger the modal with a button -->
	  	<p>
		<button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-backdrop="static" data-keyboard="false" id="btnSnomed" >Launch SNOMED Query Tool</button>
	<!-- 	<button id="btnSnomed" data-toggle="modal" data-backdrop="static" data-keyboard="false">Launch SNOMED Query Tool</button> -->
	<!--     <button id="btnSnomed" class="modal hide fade in" data-keyboard="false" data-backdrop="static">Launch SNOMED Query Tool</button> -->
	  
		<H2>Main page that will use term query tool</H2>
		
		<H2><b>You have selected:</b></H2>

		<table>
		   <tr>
		     <th>Attribute Name</th>
		     <th>Value</th>
		   </tr>
		   <tr>
		     <td>SCT Version Code</td>
		     <td id="version"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept ID</td>
		     <td id="conceptId"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept Type</td>
		     <td id="conceptType"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept FSN ID</td>
		     <td id="conceptFsnId"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept FSN</td>
		     <td id="conceptFsn"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept Preferred ID</td>
		     <td id="conceptPreferredId"></td>
		   </tr>
		   <tr>
		     <td>SCT Concept Preferred</td>
		     <td id="conceptPreferred"></td>
		   </tr>
		   <tr>
		     <td>SCT Selected Term ID</td>
		     <td id="selectedTermId"></td>
		   </tr>
		   <tr>
		     <td>SCT Selected Term Type</td>
		     <td id="selectedTermType"></td>
		   </tr>
		   <tr>
		     <td>SCT Selected Term</td>
		     <td id="selectedTerm"></td>
		   </tr>
		   <tr>
		     <td>SCT Selected Term Acceptability</td>
		     <td id="selectedTermAcceptability"></td>
		   </tr>
		</table> 
	  
	</div>
		
</body>
</html>