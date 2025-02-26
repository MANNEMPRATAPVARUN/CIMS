package ca.cihi.cims.transformation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.icd.IcdValidation;
import ca.cihi.cims.content.shared.BaseConcept;

public class MockIcdTabular extends IcdTabular {

	private String typeCode = "";
	private String code = "";
	private String userDescription = "";
	private boolean caEnhancement = false;
	private String includeXml = "";
	private String excludeXml = "";
	private String codeAlsoXml = "";
	private String definitionXml = "";
	private String noteXml = "";
	private String shortDescription = "";
	private String longDescription = "";
	private String presentationHtml = "";
	private final String daggerAsterisk = "";
	private String tableOutput = "";
	private String shortPresentationHtml = "";
	private final boolean validCodeIndicator = false;
	private Long elementId = 1L;

	@Override
	public List<IcdTabular> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getCodeAlsoXml(String language) {
		return codeAlsoXml;
	}

	@Override
	public IcdTabular getContainingPage() {
		// This of course makes no sense
		return this;
	}

	@Override
	public String getDaggerAsterisk() {
		return daggerAsterisk;
	}

	@Override
	public DaggerAsterisk getDaggerAsteriskConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefinitionXml(String language) {
		return definitionXml;
	}

	@Override
	public byte[] getDiagram(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDiagramFileName(String language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getElementId() {
		return elementId;
	}

	@Override
	public String getExcludeXml(String language) {
		return excludeXml;
	}

	@Override
	public String getIncludeXml(String language) {
		return includeXml;
	}

	@Override
	public String getLongDescription(String language) {
		return longDescription;
	}

	@Override
	public int getNestingLevel() {
		return 2;
	}

	@Override
	public String getNote(String language) {
		return noteXml;
	}

	@Override
	public int getNumberOfChildrenWithValidations() {
		return 5;
	}

	@Override
	public IcdTabular getParent() {
		// This of course makes no sense, but it will do for now.
		return this;
	}

	@Override
	public String getPresentationHtml(String language) {
		return presentationHtml;
	}

	@Override
	public String getShortDescription(String language) {
		return shortDescription;
	}

	@Override
	public String getShortPresentationHtml(String language) {
		return shortPresentationHtml;
	}

	@Override
	public SortedSet<IcdTabular> getSortedBlocks() {
		TreeSet<IcdTabular> blocks = new TreeSet<IcdTabular>();

		if ("01".equalsIgnoreCase(getCode())) {
			MockIcdTabular block1 = new MockIcdTabular();
			block1.setCode("A00-A09");
			block1.setTypeCode("Block");
			block1.setUserDescription("ENG", "Intestinal infectious diseases");
			blocks.add(block1);
		}

		return blocks;
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return "ACTIVE";
	}

	@Override
	public String getTableOutput(String language) {
		return tableOutput;
	}

	@Override
	public String getTypeCode() {
		return typeCode;
	}

	@Override
	public String getUserDescription(String language) {
		return userDescription;
	}

	@Override
	public Collection<IcdValidation> getValidations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCanadianEnhancement() {
		return caEnhancement;
	}

	@Override
	public boolean isValidCode() {
		return validCodeIndicator;
	}

	@Override
	public void setCanadianEnhancement(boolean canadianEnhancement) {
		this.caEnhancement = canadianEnhancement;

	}

	@Override
	public void setCode(String code) {
		this.code = code;

	}

	@Override
	public void setCodeAlsoXml(String language, String xml) {
		this.codeAlsoXml = xml;
	}

	@Override
	public void setDaggerAsteriskConcept(DaggerAsterisk value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefinitionXml(String language, String xml) {
		this.definitionXml = xml;

	}

	@Override
	public void setDiagram(String language, byte[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDiagramFileName(String language, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setElementId(Long elementId) {
		// do nothing
		this.elementId = elementId;
	}

	@Override
	public void setExcludeXml(String language, String xml) {
		this.excludeXml = xml;
	}

	@Override
	public void setIncludeXml(String language, String xml) {
		this.includeXml = xml;
	}

	@Override
	public void setLongDescription(String language, String longDescription) {
		this.longDescription = longDescription;
	}

	@Override
	public void setNote(String language, String note) {
		this.noteXml = note;
	}

	@Override
	public void setParent(BaseConcept concept) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPresentationHtml(String language, String html) {
		this.presentationHtml = html;
	}

	@Override
	public void setShortDescription(String language, String shortDescription) {
		this.shortDescription = shortDescription;
	}

	@Override
	public void setShortPresentationHtml(String language, String value) {
		this.shortPresentationHtml = value;
	}

	@Override
	public void setStatus(String status) {
		// does nothing
	}

	@Override
	public void setTableOutput(String language, String tableOutput) {
		this.tableOutput = tableOutput;
	}

	@Override
	public void setTypeCode(String type) {
		this.typeCode = type;

	}

	@Override
	public void setUserDescription(String language, String userDesc) {
		this.userDescription = userDesc;

	}

}
