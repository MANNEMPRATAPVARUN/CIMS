<div id="subTab" class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all noborder" style="border-top: 1px solid #9cbdff">
	<c:if test="${!empty(param.tab) or (empty(param.tab) and bean.edit)}">
		<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<li class="ui-state-default ui-corner-top ${empty(param.tab)?'ui-tabs-selected ui-state-active':' '}">
				<a class="tab" href="indexes/basicInfo/edit.htm?${automaticContextParams}&id=${bean.elementId}&language=${param.language}"><span>Basic Info</span></a>
			</li>
			<c:if test="${bean.model.type.term}">
				<li class="ui-state-default ui-corner-top ${param.tab=='TERM'?'ui-tabs-selected ui-state-active':' '}">
					<a class="tab" href="indexes/termreferences/edit.htm?${automaticContextParams}&id=${bean.elementId}&tab=TERM&language=${param.language}"><span>Index Term References</span></a>
				</li>
				<c:choose>
					<c:when test="${bean.model.icd1Or2OrCci}">
						<li class="ui-state-default ui-corner-top ${param.tab=='CODE'?'ui-tabs-selected ui-state-active':' '}">
							<a class="tab" href="indexes/codereferences/editICD12CCI.htm?${automaticContextParams}&id=${bean.elementId}&tab=CODE&language=${param.language}"><span>Code Value References</span></a>
						</li>
					</c:when>
					<c:when test="${bean.model.icd3Or4}">
						<li class="ui-state-default ui-corner-top ${param.tab=='CODE'?'ui-tabs-selected ui-state-active':' '}">
							<a class="tab" href="indexes/codereferences/editICD34.htm?${automaticContextParams}&id=${bean.elementId}&tab=CODE&language=${param.language}"><span>Code Value References</span></a>
						</li>
					</c:when>
					<c:otherwise></c:otherwise>
				</c:choose>
			</c:if>
		</ul>

	</c:if>
</div>
<div style="margin-left:20px">
	<span class="dark-red-bold parent">${bean.breadCrumbs}</span>
	<span class="green parent">${bean.model.description}
	<c:if test="${bean.model.type ne 'ICD_BOOK_INDEX' and bean.model.type ne 'CCI_BOOK_INDEX' and bean.model.level ne 0}">(level ${bean.model.level})</c:if>
	</span>
</div>
<script>
	$(document).ready(function() {	    
	    $("a.tab").click(function(event){
	    	event.preventDefault();
	    	
	    	var data = {isViewMode: false, conceptId: "${bean.model.elementId}", conceptCode: "${bean.model.code}"};
			NavigationController.navigate(this.href, data);
	    });
	});
</script>


