<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<fieldset>
	<legend class="searchLegend"><fmt:message key="search.criteria.legend.set"/></legend>
	<div class="span-8 form-row">
		<div class="span-3 label">
			<div><label for="year"><fmt:message key="search.criteria.year"/>:</label></div>
			<div><label for="priorYear"><fmt:message key="search.criteria.year.prior"/>:</label></div>
			<div><label for="priorYear"><fmt:message key="search.criteria.attribute.type"/>:</label></div>
		</div>
		<div class="span-5 last">
			<div><form:select id="year" path="contextId" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" cssErrorClass="fieldError"></form:select></div>
			<div><form:select id="priorYear" path="priorContextId" items="${contextIds}" itemLabel="versionCode" itemValue="contextId" cssErrorClass="fieldError"></form:select></div>
			<div>
				<form:select id="priorYear" path="attributeTypeId"  cssErrorClass="fieldError">
					<form:option value="${null}"></form:option>
					<form:options items="${attributeTypes}" itemLabel="value" itemValue="key"></form:options>
				</form:select>
			</div>
		</div>
	</div>
	<div class="span-18 form-row">
		<div class="span-4 label">
			<label><fmt:message key="search.criteria.comparative.type"/>:</label>
		</div>
		<div class="span-14 last">
			<div class="span-4">
				<div><form:radiobutton path="comparativeType" id="newCode" name="comparativeType" value="NewRefValue"/><label for="newCode" class="simple"><fmt:message key="search.criteria.comparative.type.ref.value.new"/></label></div>
				<div><form:radiobutton path="comparativeType" id="disabledCode" name="comparativeType" value="DisabledRefValue"/><label for="disabledCode" class="simple"><fmt:message key="search.criteria.comparative.type.ref.value.disabled"/></label></div>
			</div>
			<div class="span-10 last">
				<div><form:radiobutton path="comparativeType" id="modifiedMandatoryInd" name="comparativeType" value="ModifiedMandatoryInd"/><label for="modifiedMandatoryInd" class="simple"><fmt:message key="search.criteria.comparative.type.mandatory.indicator.modified"/></label></div>
				<div><form:radiobutton path="comparativeType" id="modifiedInContextDesc" name="comparativeType" value="ModifiedInContextDesc"/><label for="modifiedInContextDesc" class="simple"><fmt:message key="search.criteria.comparative.type.context.description.modified"/></label></div>
			</div>
		</div>
	</div>
</fieldset>