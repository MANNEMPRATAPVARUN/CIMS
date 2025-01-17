<!DOCTYPE html>

<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
textarea {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>
<div class="content">
	<div id="block_container">
		<div>
			<label>Code:</label>&nbsp;&nbsp;
			<font color=red>${attrModel.code}</font>
		</div>
		<div>
			<label>English Description:</label>&nbsp;&nbsp;
			<font color=red>${attrModel.descriptionEng}</font>
		</div>
	</div>
</div>

<div class="content">	
	<table id="notes" style="width: 100%; margin-top: 20px; table-layout:fixed;" class="listTable">
		<thead>
			<tr>
				<th class="tableHeader sizeOneFifty">
					<c:if test="${language == 'ENG'}">
						<fmt:message key="common.english"/>
					</c:if>
					<c:if test="${language == 'FRA'}">
						<fmt:message key="common.french"/>
					</c:if>
				</th>
			</tr>
		</thead>
		<tbody>
			<tr class="odd">
				<td><textarea style='height:200px; word-wrap: break-word;' disabled="true">${note}</textarea></td>
		</tbody>
	</table>
</div>
</html>

