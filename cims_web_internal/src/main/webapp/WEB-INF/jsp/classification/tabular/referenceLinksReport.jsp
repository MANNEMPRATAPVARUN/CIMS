<html>
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>
<body>
<style>
	@media print{    
	    .no-print, .no-print * {display: none !important;}
	}
</style>

<table width="100%" cellpadding="0" cellspacing="0">
<tr>
	<td>
		<h3><center>Related Active Reference Links Report for Code Value ${bean.code}</center></h3>
	</td>
	<td class="no-print" width="30px" align="right">
		<a href="javascript:window.print();"><img title="Print" src="<c:url value="/img/icons/Print.png"/>"/></a>
	</td>
</tr>
</table>

<div class="content">
	<legend>Tabular List</legend>
	<display:table
		id="tabularReferencedLinks"
		name="bean.tabularReferencedLinks"
		requestURI=""
		class="listTable"
		style="width: 100%;"
	>
		<display:setProperty name="basic.msg.empty_list" value="Code has no Tabular List references"/>
		<display:column
			title="Code Value"
			property="code"
			headerClass="tableHeader"
		/>
		<display:column
			title="Hierarchical Level"
			property="level"
			headerClass="tableHeader"
		/>
		<display:column
			title="Location"
			property="location"
			headerClass="tableHeader"
		/>
		<display:column
			title="Language"
			property="language"
			headerClass="tableHeader"
		/>
	</display:table>

<br/><br/>

	<legend>Index Book</legend>
	<display:table
		id="indexReferencedLinks"
		name="bean.indexReferencedLinks"
		requestURI=""
		class="listTable"
		style="width: 100%;"
	>
		<display:setProperty name="basic.msg.empty_list" value="Code has no Index Book references"/>
		<display:column
			title="Index Term"
			property="indexTerm"
			headerClass="tableHeader"
		/>
	</display:table>

<br/><br/>

	<legend>Supplements</legend>
	<display:table
		id="supplementReferencedLinks"
		name="bean.supplementReferencedLinks"
		requestURI=""
		class="listTable"
		style="width: 100%;"
	>
		<display:setProperty name="basic.msg.empty_list" value="Code has no Supplement references"/>
		<display:column
			title="Supplement"
			property="description"
			headerClass="tableHeader"
		/>
	</display:table>
	
</div>
</body>
</html>
