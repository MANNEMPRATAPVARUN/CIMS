
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<c:if test="${viewBean.contextId!=null }">
	<div
		class="appTab ui-tabs ui-widget ui-widget-content ui-corner-all noborder">
		<ul
			class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
			<li
				class="${activeTab=='refsetConfig'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
				<a
				href='<c:url value="/refset/refsetEditDetail.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${viewBean.elementId}" /><c:param name="elementVersionId" value="${viewBean.elementVersionId}" /></c:url> '>
					<span>Refset Configuration Details</span>
			</a>
			</li>
			<li
				class="${activeTab=='supplement'? 'ui-state-default ui-corner-top ui-tabs-selected ui-state-active':'ui-state-default ui-corner-top'}">
				<a
				href='<c:url value="/refset/supplement.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${viewBean.elementId}" /><c:param name="elementVersionId" value="${viewBean.elementVersionId}" /></c:url> '>
					<span>Supplement</span>
			</a>
			</li>
			<li
				class="ui-state-default ui-corner-top ${activeTab == 'picklist' ? 'ui-state-active ui-tabs-selected' : ''}">
				<a
				    href='<c:url value="/refset/picklist.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${viewBean.elementId}" /><c:param name="elementVersionId" value="${viewBean.elementVersionId}" /></c:url>'
				    <c:if test="${not empty activePicklistSubTab}">style="cursor: pointer;"</c:if>>
				  	<span>Picklist</span>
			    </a>
			</li>
            <li
                class="ui-state-default ui-corner-top ${activeTab == 'productOutputConfig' ? 'ui-state-active ui-tabs-selected' : ''}">
                <a
                    href='<c:url value="/refset/productOutputConfig.htm"><c:param name="contextId" value="${viewBean.contextId}" /><c:param name="elementId" value="${viewBean.elementId}" /><c:param name="elementVersionId" value="${viewBean.elementVersionId}" /></c:url>'>
                    <span>Refset Product Output Configuration</span>
                </a>
            </li>
		</ul>
	</div>
</c:if>



