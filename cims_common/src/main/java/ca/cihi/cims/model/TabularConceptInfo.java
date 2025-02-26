package ca.cihi.cims.model;

import java.util.List;

public class TabularConceptInfo {

	private String code;
	private String typeCode;
	private List<AsteriskBlockInfo> blockList;
	private List<AsteriskBlockInfo> asteriskList;
	private boolean isValidCode;
	private int nestingLevel;
	private boolean hasValidation;
	private AttributeInfo attributeInfo;
	private boolean isCanadianEnhancement;
	private String conceptCodeWithDecimalDagger;

	public List<AsteriskBlockInfo> getAsteriskList() {
		return asteriskList;
	}

	public AttributeInfo getAttributeInfo() {
		return attributeInfo;
	}

	public List<AsteriskBlockInfo> getBlockList() {
		return blockList;
	}

	public String getCode() {
		return code;
	}

	public String getConceptCodeWithDecimalDagger() {
		return conceptCodeWithDecimalDagger;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public boolean hasValidation() {
		return hasValidation;
	}

	public boolean isCanadianEnhancement() {
		return isCanadianEnhancement;
	}

	public boolean isValidCode() {
		return isValidCode;
	}

	public void setAsteriskList(final List<AsteriskBlockInfo> asteriskList) {
		this.asteriskList = asteriskList;
	}

	public void setAttributeInfo(final AttributeInfo attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

	public void setBlockList(final List<AsteriskBlockInfo> blockList) {
		this.blockList = blockList;
	}

	public void setCanadianEnhancement(final boolean isCanadianEnhancement) {
		this.isCanadianEnhancement = isCanadianEnhancement;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setConceptCodeWithDecimalDagger(final String conceptCodeWithDecimalDagger) {
		this.conceptCodeWithDecimalDagger = conceptCodeWithDecimalDagger;
	}

	public void setHasValidation(final boolean hasValidation) {
		this.hasValidation = hasValidation;
	}

	public void setNestingLevel(final int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public void setTypeCode(final String typeCode) {
		this.typeCode = typeCode;
	}

	public void setValidCode(final boolean isValidCode) {
		this.isValidCode = isValidCode;
	}

}
