<div id="subTab" class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all noborder" style="border-top: 1px solid #9cbdff">
	<c:if test="${!empty(param.tab) or (empty(param.tab) and bean.edit)}">
		<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<li class="ui-state-default ui-corner-top ${empty(param.tab)?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="tabulars/basicInfo/edit.htm?${automaticContextParams}&id=${bean.elementId}"><span>Basic Info</span></a>
			</li>

			<c:if test="${bean.classification=='ICD'}">
				<li class="ui-state-default ui-corner-top ${param.tab=='DEFINITION'?'ui-tabs-selected ui-state-active':' '}">
					<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=DEFINITION"><span>Definition</span></a>
				</li>
			</c:if>
			
			<li class="ui-state-default ui-corner-top ${param.tab=='NOTE'?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=NOTE"><span>Note</span></a>
			</li>
			<li class="ui-state-default ui-corner-top ${param.tab=='INCLUDE'?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=INCLUDE"><span>Includes</span></a>
			</li>
			<li class="ui-state-default ui-corner-top ${param.tab=='EXCLUDE'?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=EXCLUDE"><span>Excludes</span></a>
			</li>
			<li class="ui-state-default ui-corner-top ${param.tab=='CODE_ALSO'?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=CODE_ALSO"><span>Code Also</span></a>
			</li>
			
			<c:if test="${bean.classification=='CCI'}">
				<li class="ui-state-default ui-corner-top ${param.tab=='OMIT_CODE'?'ui-tabs-selected ui-state-active':' '}">
					<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=OMIT_CODE"><span>Omit Code</span></a>
				</li>
				<c:if test="${bean.type=='CCI_RUBRIC'}">
					<li class="ui-state-default ui-corner-top ${param.tab=='TABLE'?'ui-tabs-selected ui-state-active':' '}">
						<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=TABLE"><span>Rubric Table</span></a>
					</li>
				</c:if>
			</c:if>
			<c:if test="${bean.type=='ICD_CATEGORY'}">
				<li class="ui-state-default ui-corner-top ${param.tab=='TABLE'?'ui-tabs-selected ui-state-active':' '}">
					<a class="tab" href="tabulars/xml/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=TABLE"><span>Category Table</span></a>
				</li>
			</c:if>
			<c:if test="${bean.type=='CCI_RUBRIC' or bean.type=='CCI_CCICODE' or bean.type=='ICD_CATEGORY'}">
				<li class="ui-state-default ui-corner-top ${param.tab=='VALIDATION'?'ui-tabs-selected ui-state-active':' '}">
					<a class="tab" href="tabulars/validation/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=VALIDATION"><span>Validation</span></a>
				</li>
			</c:if>
			
		</ul>
		
	</c:if>
</div>

<table width="100%" style="margin-bottom:0px">
	<tr>
		<td width="7">&nbsp;</td>
		<td width="120">
			<c:if test="${not empty bean.parentCode}">
				<span class="dark-red-bold parent">Parent: ${bean.parentCode}</span>
			</c:if>
		</td>
		<td>
			<h3 class="contentHeader">
				<c:choose>
					<c:when test="${empty(param.tab)}">
						<c:choose>
							<c:when test="${bean.edit}">Code Value: ${bean.code}</c:when>
							<c:otherwise>Add New Code</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>Code Value: ${bean.code}</c:otherwise>
				</c:choose>
			</h3>
		</td>
		<td width="107">&nbsp;</td>
	</tr>
</table>

<script>
	$(document).ready(function() {
	    $("a.tab").click(function(event){
	    	event.preventDefault();
	    	
	    	var data = {isViewMode: false, conceptId: "${bean.model.elementId}", conceptCode: "${bean.code}"};
	    	NavigationController.navigate(this.href, data);
	    });
	    
	});
</script>


