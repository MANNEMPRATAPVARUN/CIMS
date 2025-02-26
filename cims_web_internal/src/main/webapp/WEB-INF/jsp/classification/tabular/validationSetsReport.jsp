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
		<h3><center>Validation Sets for ${bean.code}</center></h3>
	</td>
	<td class="no-print" width="30px" align="right">
		<a  href="javascript:window.print();"><img title="Print" src="<c:url value="/img/icons/Print.png"/>"/></a>
	</td>
</tr>
</table>

<div class="content">

<c:choose>
	<c:when test="${bean.classification eq 'CCI'}">
		<display:table
			id="validationSets" name="bean.cciValidationSets" requestURI="" class="listTable" style="width: 100%;">
			<display:setProperty name="basic.msg.empty_list" value="Code has no validation sets"/>
			<display:column title="Data Holding" property="dataHolding" headerClass="tableHeader"/>
			<display:column title="Gender" property="gender" headerClass="tableHeader"/>
			<display:column title="Age Range" property="ageRange" headerClass="tableHeader"/>
			<display:column title="Status Reference" property="statusReference" headerClass="tableHeader"/>
			<display:column title="Location Reference" property="locationReference" headerClass="tableHeader"/>
			<display:column title="Mode Of Delivery Reference"  property="modeOfDeliveryReference" headerClass="tableHeader"/>
			<display:column title="Extent Rference" property="extentReference" headerClass="tableHeader"/>	
		</display:table>
	</c:when>
	<c:otherwise>
		<display:table
			id="validationSets" name="bean.icdValidationSets" requestURI="" class="listTable" style="width: 100%;">
			<display:setProperty name="basic.msg.empty_list" value="Code has no validation sets"/>
			<display:column title="Data Holding" property="dataHolding" headerClass="tableHeader"/>
			<display:column title="Gender" property="gender" headerClass="tableHeader"/>
			<display:column title="Minimum Age" property="minimumAge" headerClass="tableHeader"/>
			<display:column title="Maximum Age" property="maximumAge" headerClass="tableHeader"/>
			<display:column title="MRDx/Main" property="MRDxMain" headerClass="tableHeader"/>
			<display:column title="Dx Type 1" property="dxType1" headerClass="tableHeader"/>
			<display:column title="Dx Type 2" property="dxType2" headerClass="tableHeader"/>
			<display:column title="Dx Type 3" property="dxType3" headerClass="tableHeader"/>
			<display:column title="Dx Type 4" property="dxType4" headerClass="tableHeader"/>
			<display:column title="Dx Type 6" property="dxType6" headerClass="tableHeader"/>
			<display:column title="Dx Type 9" property="dxType9" headerClass="tableHeader"/>
			<display:column title="Dx Type W" property="dxTypeW" headerClass="tableHeader"/>
			<display:column title="Dx Type X" property="dxTypeX" headerClass="tableHeader"/>
			<display:column title="Dx Type Y" property="dxTypeY" headerClass="tableHeader"/>
			<display:column title="New Born"  property="newBorn" headerClass="tableHeader"/>
		</display:table>
	</c:otherwise>
</c:choose>

</div>
</body>
</html>
