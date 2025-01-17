<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta name="description" content="${project.artifactId} - ${version}" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<!-- can be any external url -->
<spring:eval var="snomedUrl" expression="@applicationProperties.getProperty('snomedservice.url')"/>
<%-- <c:import url="${snomedUrl}" /> --%>

<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
<link href="<c:url value="/resources/css/jquery.splitter.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/snomed.css"/>" rel="stylesheet">

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
<script	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
<script	src="<c:url value="/resources/core/jquery.splitter-0.20.0.js" />"></script>
<script	src="<c:url value="/resources/core/handlebars-v4.0.5.js" />"></script>

<script>

	function update(selected) {
		document.getElementById("selectedTermId").innerHTML = selected.selectedTermId;
		//document.getElementById("version").innerHTML = selected.version;
		document.getElementById("conceptId").innerHTML = selected.conceptId;
		document.getElementById("conceptType").innerHTML = selected.conceptType;


		document.getElementById("conceptFsnId").innerHTML = selected.conceptFsnId;
		document.getElementById("conceptFsn").innerHTML = selected.conceptFsn;
		document.getElementById("conceptPreferredId").innerHTML = selected.conceptPreferredId;
		document.getElementById("conceptPreferred").innerHTML = selected.conceptPreferred;

		document.getElementById("selectedTermType").innerHTML = selected.selectedTermType;
		document.getElementById("selectedTerm").innerHTML = selected.selectedTerm;
		//document.getElementById("selectedTermAcceptability").innerHTML = selected.selectedTermAcceptability;

		document.getElementById("synonymId").innerHTML = selected.synonymId;
		document.getElementById("synonym").innerHTML = selected.synonym;

		/*
		var nodes=[], values=[];
		for (var att, i = 0, atts = selected.attributes, n = atts.length; i < n; i++){
		    att = atts[i];
		    nodes.push(att.nodeName);
		    values.push(att.nodeValue);
		}
		*/
	}

	function enableBrowseClickEvent() {
		var version = $('#release_version').val();   
		var modalUrl = '${snomedUrl}'+'?version=' + version;
		window.open(modalUrl);
	}
	
</script>

</head>

<!-- <body onLoad="snomedInit();"> -->
<body>

	<div class="container">
		<h2>Snomed CT Query Tool</h2>
		
		<label for="release_select_dropdown"> <span
			class="i18n" data-i18n-id="i18n_select_release">Releases to select:</span></label> <br>
		<div class="leftSelect">
			<select id="release_version">
				<c:forEach items="${SCTVersionList}" var="version"  varStatus="loop">
		        	<option value="${version.versionCode}" ${loop.first? 'selected' : ''}>${version.versionDesc}</option>
		    	</c:forEach>
			</select>
		</div>	
		
	<!-- 	Concept ID: <input type="text" name="snomed_concept_id" id="snomed_concept_id"><br> -->
	  	<!-- Trigger the modal with a button -->
	  	<p>
	   <!-- 	<button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-backdrop="static" data-keyboard="false" id="btnSnomed" >Launch SNOMED Query Tool</button> -->
	   <button onclick="enableBrowseClickEvent()">Launch SNOMED Query Tool</button>
		<H2>Main page that will use term query tool</H2>
		
		<H2><b>You have selected:</b></H2>

		<table>
		   <tr>
		     <th>Attribute Name</th>
		     <th>Value</th>
		   </tr>		   
	<!-- 	   <tr>
		     <td>SCT Version Code</td>
		     <td id="version"></td>
		   </tr> -->
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
		  
		<!--    <tr>
		     <td>SCT Selected Term Acceptability</td>
		     <td id="selectedTermAcceptability"></td>
		   </tr> -->
		   
		   <tr>
		     <td>SCT Synonym Id</td>
		     <td id="synonymId"></td>
		   </tr>
		   <tr>
		     <td>SCT Synonym</td>
		     <td id="synonym"></td>
		   </tr>
		</table> 
	  
	</div>
		
</body>
</html>