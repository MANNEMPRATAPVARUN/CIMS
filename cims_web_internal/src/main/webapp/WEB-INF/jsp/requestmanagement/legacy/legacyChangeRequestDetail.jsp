<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<html style="height: 100%;">
<%@ include file="/WEB-INF/jsp/common/common-header.jsp"%>

<style type="text/css" media="all">
input[type="text"] {
     width: 100%; 
     box-sizing: border-box;
     -webkit-box-sizing:border-box;
     -moz-box-sizing: border-box;
}
</style>

<script src="<c:url value="/js/displayTagService.js"/>"></script>
<script type="text/javascript">

</script>

<div class="content" style="border: 0;">
<c:choose>
<c:when test="${not empty errorMessage}">
  <table style="width: 98%; margin-top: 20px; margin-left:10px;">
    <tr><td>  ${errorMessage} </td></tr>
    <tr><td> <FORM><INPUT Type="button" VALUE="Back" onClick="history.go(-1);return true;"></FORM> </td></tr>
  </table>
</c:when>
<c:otherwise>
<fieldset>
<legend>Change Request Details</legend>

	<table class="listTable" style="width: 98%; margin-top: 20px; margin-left:10px; border-collapse: collapse;">
      <tr>
        <th class="listTableHead" style="width: 20%" >Property</th>
        <th class="listTableHead">Value</th>
      </tr>
	  <tr style="border: 1px solid grey;">
        <td>Record Name</td>
        <td>${legacyRequestDetailModel.requestName}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Classification</td>
        <td>${legacyRequestDetailModel.classificationTitleCode}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Version</td>
        <td>${legacyRequestDetailModel.versionCode}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Language</td>
        <td>${legacyRequestDetailModel.language}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Nature of Change</td>
        <td>${legacyRequestDetailModel.changeNature}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Type of Change</td>
        <td>${legacyRequestDetailModel.changeType}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Disposition</td>
        <td>${legacyRequestDetailModel.requestStatus}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Section</td>
        <td>${legacyRequestDetailModel.sectionDesc}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Creation Date</td>
        <td>${legacyRequestDetailModel.requestDate}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Created By</td>
        <td>${legacyRequestDetailModel.requestByUser}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Requestor Type</td>
        <td>${legacyRequestDetailModel.requestorType}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Change Description</td>
        <td>${legacyRequestDetailModel.requestDescTxt}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Change Description Francais</td>
        <td>${legacyRequestDetailModel.requestFDescTxt}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Review Required</td>
        <td>${legacyRequestDetailModel.reviewDesc}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Maintenance Notes</td>
        <td>${legacyRequestDetailModel.maintenanceNote}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Rationale</td>
        <td>${legacyRequestDetailModel.requestRationalTxt}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Disposed By</td>
        <td>${legacyRequestDetailModel.disposeByUser}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Rationale for Decision</td>
        <td>${legacyRequestDetailModel.disposeRationalTxt}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Last Modified Date</td>
        <td>${legacyRequestDetailModel.lastModifiedDate}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Last Modified By</td>
        <td>${legacyRequestDetailModel.lastModifiedUser}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Conversion Evolution Notes</td>
        <td>${legacyRequestDetailModel.conversionEvolutionNote}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Notes</td>
        <td>${legacyRequestDetailModel.note}</td>
	  </tr>
	  <tr style="border: 1px solid grey;">
        <td>Query Reference Number</td>
        <td>
            <c:forEach var="queryRefNum" items="${legacyRequestDetailModel.queryRefNums}" varStatus="count">
              <c:if test="${count.count > 1}">  
              ,&nbsp;
              </c:if>
              ${queryRefNum}
            </c:forEach>
        </td>
	  </tr>
      <c:choose>
      <c:when test="${not empty legacyRequestDetailModel.attachmentFileNames}">
      <c:forEach var="attachment" items="${legacyRequestDetailModel.attachmentFileNames}" varStatus="count">
	  <tr style="border: 1px solid grey;">
            <td>Attachment ${count.count}</td>
            <td>
            <a href="<c:url value="/downloadAttachmentFile.htm?attachmentFileName=${attachment}"/>">${attachment}</a>
            </td>
	  </tr>
      </c:forEach>
      </c:when>
      <c:otherwise>
	  <tr style="border: 1px solid grey;">
            <td>Attachment</td>
            <td>&nbsp; </td>
	  </tr>
      </c:otherwise>
      </c:choose>

	</table>

</fieldset>
</c:otherwise>
</c:choose>
</div>

</html>
