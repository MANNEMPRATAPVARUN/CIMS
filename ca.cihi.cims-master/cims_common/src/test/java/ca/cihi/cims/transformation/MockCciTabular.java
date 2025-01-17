package ca.cihi.cims.transformation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.cihi.cims.content.cci.CciApproachTechniqueComponent;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.cci.CciTissueComponent;
import ca.cihi.cims.content.cci.CciValidation;
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.content.shared.FacilityType;

public class MockCciTabular extends CciTabular {

	private static final String LANGUAGE = "ENG";

	private String typeCode = "";
	private String code = "";
	private String userDescription = "";
	private String includeXml = "";
	private String excludeXml = "";
	private String codeAlsoXml = "";
	private String omitXml = "";
	private String noteXml = "";
	private String shortDescription = "";
	private String longDescription = "";
	private String presentationHtml = "";
	private String tableOutput = "";
	private String shortPresentationHtml = "";
	private final boolean validCodeIndicator = false;
	private int nestingLevel = 1;
	private Long elementId = 1L;

	private CciGroupComponent groupComponent;

	@Override
	public SortedSet<CciTabular> descendentBlocks() {
		SortedSet<CciTabular> sortedBlocks = new TreeSet<CciTabular>();

		if ("02".equalsIgnoreCase(code)) {
			CciTabular block1Tabular = new MockCciTabular();
			block1Tabular.setCode("2AA-2BX");
			block1Tabular.setTypeCode(BLOCK);
			block1Tabular.setUserDescription(LANGUAGE, "Diagnostic Interventions on the Nervous System");
			sortedBlocks.add(block1Tabular);

			CciTabular block2Tabular = new MockCciTabular();
			block2Tabular.setCode("2AA-2AZ");
			block2Tabular.setTypeCode(BLOCK);
			block2Tabular.setUserDescription(LANGUAGE, "Diagnostic Interventions on the Brain and Spinal Cord");
			sortedBlocks.add(block2Tabular);
		}

		return sortedBlocks;
	}

	@Override
	public CciApproachTechniqueComponent getApproachTechniqueComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CciTabular> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public SortedSet<CciTabular> getChildrenWithValidations() {
		SortedSet<CciTabular> children = new TreeSet<CciTabular>();
		if (CciTabular.RUBRIC.equalsIgnoreCase(typeCode) && "2.AF.71.^^".equalsIgnoreCase(code)) {
			children.add(new MockCciTabular());
		}

		return children;
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
	public CciTabular getContainingPage() {
		// This of course makes no sense
		return this;
	}

	@Override
	public CciDeviceAgentComponent getDeviceAgentComponent() {
		// TODO Auto-generated method stub
		return null;
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
	public CciGroupComponent getGroupComponent() {
		return groupComponent;
	}

	@Override
	public String getIncludeXml(String language) {
		return includeXml;
	}

	@Override
	public CciInterventionComponent getInterventionComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CciInvasivenessLevel getInvasivenessLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLongDescription(String language) {
		return longDescription;
	}

	@Override
	public int getNestingLevel() {
		return nestingLevel;
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
	public String getOmitCodeXml(String language) {
		return omitXml;
	}

	@Override
	public CciTabular getParent() {
		// This of course makes no sense, but it will do for now.
		return this;
	}

	@Override
	public String getPresentationHtml(String language) {
		return presentationHtml;
	}

	@Override
	public Collection<CciApproachTechniqueComponent> getSectionATC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CciDeviceAgentComponent> getSectionDAC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CciGroupComponent> getSectionGC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CciInterventionComponent> getSectionIC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<CciTissueComponent> getSectionTC() {
		// TODO Auto-generated method stub
		return null;
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
	public SortedSet<CciTabular> getSortedChildren() {
		TreeSet<CciTabular> children = new TreeSet<CciTabular>();

		if ("01".equalsIgnoreCase(code)) {
			MockCciTabular block1 = new MockCciTabular();
			block1.setCode("1AA-1BZ");
			block1.setTypeCode("Block");
			block1.setUserDescription("ENG", "Therapeutic Interventions on the Nervous System");
			children.add(block1);
		} else if ("2.AF.71.^^".equalsIgnoreCase(code)) {
			CciTabular code1Tabular = new MockCciTabular();
			code1Tabular.setCode("2.AF.71.GR");
			code1Tabular.setTypeCode(CciTabular.CCICODE);
			code1Tabular.setUserDescription(LANGUAGE, "using percutaneous transluminal approach");
			code1Tabular
					.setIncludeXml(
							LANGUAGE,
							"<qualifierlist type=\"includes\"><include><label>Petrosal sinus sampling (for elevated ACTH secretions) </label></include></qualifierlist>");

			CciTabular code2Tabular = new MockCciTabular();
			code2Tabular.setCode("2.AF.71.QS");
			code2Tabular.setTypeCode(CciTabular.CCICODE);
			code2Tabular.setUserDescription(LANGUAGE, "using open trans sphenoidal [trans ethmoidal] approach");

			CciTabular code3Tabular = new MockCciTabular();
			code3Tabular.setCode("2.AF.71.SZ");
			code3Tabular.setTypeCode(CciTabular.CCICODE);
			code3Tabular.setUserDescription(LANGUAGE, "using open transfrontal [craniotomy flap] approach");

			children.add(code1Tabular);
			children.add(code2Tabular);
			children.add(code3Tabular);
		}

		return children;
	}

	@Override
	public String getStatus() {
		return "ACTIVE";
	}

	@Override
	public String getTableOutput(String language) {
		return tableOutput;
	}

	@Override
	public CciTissueComponent getTissueComponent() {
		// TODO Auto-generated method stub
		return null;
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
	public Collection<CciValidation> getValidations() {
		Collection<CciValidation> validations = new ArrayList<CciValidation>();
		CciValidation cciValidation = mock(CciValidation.class);

		if ("2.AF.71.^^".equalsIgnoreCase(code)) {
			FacilityType facilityType = mock(FacilityType.class);
			when(cciValidation.getFacilityType()).thenReturn(facilityType);
			when(facilityType.getDescription(LANGUAGE)).thenReturn(CciXmlGenerator.DAD_DATA_HOLDING);

			CciReferenceAttribute statusRef = mock(CciReferenceAttribute.class);
			// when(cciValidation.getStatusReferenceAttribute()).thenReturn(statusRef);
			when(statusRef.getCode()).thenReturn("S32");
			when(statusRef.isMandatory()).thenReturn(true);

			CciReferenceAttribute locationRef = mock(CciReferenceAttribute.class);
			// when(cciValidation.getLocationReferenceAttribute()).thenReturn(locationRef);
			when(locationRef.getCode()).thenReturn("L22");
			when(locationRef.isMandatory()).thenReturn(false);
			CciAttributeType attributeType = mock(CciAttributeType.class);
			when(locationRef.getType()).thenReturn(attributeType);
			when(attributeType.getCode()).thenReturn("L");

			CciReferenceAttribute extentRef = mock(CciReferenceAttribute.class);
			// when(cciValidation.getExtentReferenceAttribute()).thenReturn(extentRef);
			when(extentRef.getCode()).thenReturn("");
			when(extentRef.isMandatory()).thenReturn(false);
		}

		validations.add(cciValidation);

		return validations;
	}

	@Override
	public boolean isValidCode() {
		return validCodeIndicator;
	}

	@Override
	public void setApproachTechniqueComponent(CciApproachTechniqueComponent wrapper) {
		// TODO Auto-generated method stub

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
	public void setDeviceAgentComponent(CciDeviceAgentComponent wrapper) {
		// TODO Auto-generated method stub

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
		this.elementId = elementId;
	}

	@Override
	public void setExcludeXml(String language, String xml) {
		this.excludeXml = xml;
	}

	@Override
	public void setGroupComponent(CciGroupComponent wrapper) {
		groupComponent = wrapper;
	}

	@Override
	public void setIncludeXml(String language, String xml) {
		this.includeXml = xml;
	}

	@Override
	public void setInterventionComponent(CciInterventionComponent wrapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInvasivenessLevel(CciInvasivenessLevel wrapper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLongDescription(String language, String longDescription) {
		this.longDescription = longDescription;
	}

	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	@Override
	public void setNote(String language, String note) {
		this.noteXml = note;
	}

	@Override
	public void setOmitCodeXml(String language, String xml) {
		omitXml = xml;
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
	public void setTissueComponent(CciTissueComponent wrapper) {
		// TODO Auto-generated method stub

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
