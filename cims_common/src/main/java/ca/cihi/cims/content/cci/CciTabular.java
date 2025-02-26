package ca.cihi.cims.content.cci;

import static ca.cihi.cims.bll.query.FindCriteria.eq;
import static ca.cihi.cims.bll.query.FindCriteria.link;
import static ca.cihi.cims.bll.query.FindCriteria.linkTrans;
import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.UsesContextAccess;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGClassDeterminant;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper
@HGBaseClassification("CCI")
public abstract class CciTabular extends TabularConcept implements Comparable<CciTabular>, UsesContextAccess {

	public static final String SECTION = "Section";
	public static final String BLOCK = "Block";
	public static final String GROUP = "Group";
	public static final String RUBRIC = "Rubric";
	public static final String CCICODE = "CCICODE";

	private static CciTabular create(ContextAccess access, String code, String typeCode) {
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, typeCode, code);
		CciTabular cciTabular = access.createWrapper(CciTabular.class, typeCode, businessKey);
		cciTabular.setCode(code);
		return cciTabular;
	}

	public static CciTabular createBlock(ContextAccess access, String blockCode) {
		return create(access, blockCode, CciTabular.BLOCK);
	}

	public static CciTabular createCode(ContextAccess access, String section, CciGroupComponent group,
			CciInterventionComponent intervention, CciApproachTechniqueComponent approach,
			CciDeviceAgentComponent deviceAgent, CciTissueComponent tissue) {
		String approachCode = approach == null ? null : approach.getCode();
		String deviceAgentCode = deviceAgent == null ? null : deviceAgent.getCode();
		String tissueCode = tissue == null ? null : tissue.getCode();
		if (approachCode == null) {
			approachCode = "XX";
		}
		if (deviceAgentCode == null && tissueCode != null) {
			deviceAgentCode = "XX";
		}
		String code = generateCode(section, group.getCode(), intervention.getCode(), approachCode, deviceAgentCode,
				tissueCode);
		CciTabular newConcept = create(access, code, CciTabular.CCICODE);
		newConcept.setGroupComponent(group);
		newConcept.setInterventionComponent(intervention);
		newConcept.setApproachTechniqueComponent(approach);
		newConcept.setDeviceAgentComponent(deviceAgent);
		newConcept.setTissueComponent(tissue);
		return newConcept;
	}

	public static CciTabular createGroup(ContextAccess access, String section, CciGroupComponent group) {
		String code = generateCode(section, group.getCode(), null, null, null, null);
		CciTabular newConcept = create(access, code, CciTabular.GROUP);
		newConcept.setGroupComponent(group);
		return newConcept;
	}

	public static CciTabular createRubric(ContextAccess access, String section, CciGroupComponent group,
			CciInterventionComponent intervention) {
		String code = generateCode(section, group.getCode(), intervention.getCode(), null, null, null);
		CciTabular newConcept = create(access, code, CciTabular.RUBRIC);
		newConcept.setGroupComponent(group);
		newConcept.setInterventionComponent(intervention);
		return newConcept;
	}

	public static CciTabular createSection(ContextAccess access, String sectionCode) {
		return create(access, sectionCode, CciTabular.SECTION);
	}

	/**
	 * Format appears to be: SECTION + "." + GROUP_CODE + "." + INTERVENTION_CODE + "." + APPROACH_TECHNIQUE_CODE + "-"
	 * + DEVICE_AGENT_CODE + "-" + TISSUE_CODE
	 * 
	 * Example: 1.GA.35.BA-L7, 1.GA.83.LA-XX-Q
	 * 
	 * If the code does not exist, replace with ^^ except for the last two, which seems you just ignore Example:
	 * 1.GB.78.^^, 1.GB.^^.^^
	 * 
	 * @param section
	 * @param groupCode
	 * @param interventionCode
	 * @param approachTechniqueCode
	 * @param deviceAgentCode
	 * @param tissueCode
	 * @return
	 */
	public static String generateCode(String section, String groupCode, String interventionCode,
			String approachTechniqueCode, String deviceAgentCode, String tissueCode) {
		if (section == null) {
			throw new IllegalArgumentException("Mandatory Section string not found");
		}
		String code = section;
		if (groupCode != null) {
			code = code + "." + groupCode;
		} else {
			return code;
		}
		if (interventionCode != null) {
			code = code + "." + interventionCode;
		} else {
			code = code + ".^^";
		}
		if (approachTechniqueCode != null && interventionCode != null) {
			code = code + "." + approachTechniqueCode;
		} else {
			code = code + ".^^";
			return code;
		}
		if (deviceAgentCode != null) {
			code = code + "-" + deviceAgentCode;
		} else {
			return code;
		}
		if (tissueCode != null) {
			code = code + "-" + tissueCode;
		} else {
			return code;
		}
		return code;
	}

	private ContextAccess contextAccess;

	@Override
	public int compareTo(CciTabular other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
				.toComparison();
	}

	public SortedSet<CciTabular> descendentBlocks() {
		SortedSet<CciTabular> sortedBlocks = new TreeSet<CciTabular>();
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		// TODO: Add a new find criterion for having the right element ID, which
		// would be slightly faster than searching for a code
		Ref<CciTabular> me = ref(CciTabular.class);
		Iterator<CciTabular> iterator = contextAccess.find(cciTab, eq(me, "code", getCode()), eq(cciTab, "typeCode",
				CciTabular.BLOCK), linkTrans(cciTab, "parent", me));
		while (iterator.hasNext()) {
			sortedBlocks.add(iterator.next());
		}
		return sortedBlocks;
	}

	@HGConceptProperty(relationshipClass = "ApproachTechniqueCPV")
	public abstract CciApproachTechniqueComponent getApproachTechniqueComponent();

	@HGConceptProperty(relationshipClass = "Narrower", inverse = true)
	public abstract Collection<CciTabular> getChildren();

	public SortedSet<CciTabular> getChildrenWithValidations() {
		// TODO: Add a new find criterion for having the right element ID, which
		// would be slightly faster than searching for a code
		Ref<CciTabular> me = ref(CciTabular.class);
		Ref<CciTabular> children = ref(CciTabular.class);
		Ref<CciValidation> validation = ref(CciValidation.class);
		Iterator<CciTabular> find3 = contextAccess.find(children, eq(me, "code", getCode()), linkTrans(me, "children",
				children), link(children, "validations", validation));
		SortedSet<CciTabular> withValidations = new TreeSet<CciTabular>();
		while (find3.hasNext()) {
			withValidations.add(find3.next());
		}
		return withValidations;
	}

	public CciTabular getContainingPage() {
		/*
		 * CciTabular current = this; while (current != null) { if (current.getTypeCode().equals(SECTION)) { return
		 * current; } current = current.getParent(); } return null;
		 */
		Long sectionId = getContainingPageId();
		if (sectionId == null) {
			return null;
		}
		CciTabular current = contextAccess.load(sectionId);
		return current;
	}

	/**
	 * Return the id of the containing page.
	 * 
	 * @return Long
	 */
	public Long getContainingPageId() {
		return contextAccess.determineContainingPage(getElementId());
	}

	@HGConceptProperty(relationshipClass = "DeviceAgentCPV")
	public abstract CciDeviceAgentComponent getDeviceAgentComponent();

	@Override
	public abstract Long getElementId();

	@HGConceptProperty(relationshipClass = "GroupCompCPV")
	public abstract CciGroupComponent getGroupComponent();

	@HGConceptProperty(relationshipClass = "InterventionCPV")
	public abstract CciInterventionComponent getInterventionComponent();

	@HGConceptProperty(relationshipClass = "InvasivenessLevelIndicator")
	public abstract CciInvasivenessLevel getInvasivenessLevel();

	public int getNestingLevel() {
		Long nl = contextAccess.retrieveNestingLevel(getElementId());
		return nl.intValue();
	}

	public int getNumberOfChildrenWithValidations() {
		Long nl = contextAccess.retrieveNumberOfChildrenWithValidation(getCode());
		return nl.intValue();
	}

	@HGProperty(className = "OmitCodePresentation", elementClass = XmlPropertyVersion.class)
	public abstract String getOmitCodeXml(@HGLang String language);

	@HGConceptProperty(relationshipClass = "Narrower")
	public abstract BaseConcept getParent();

	@HGConceptProperty(relationshipClass = "ApproachTechniqueToSectionCPV", inverse = true)
	public abstract Collection<CciApproachTechniqueComponent> getSectionATC();

	@HGConceptProperty(relationshipClass = "DeviceAgentToSectionCPV", inverse = true)
	public abstract Collection<CciDeviceAgentComponent> getSectionDAC();

	@HGConceptProperty(relationshipClass = "GroupCompToSectionCPV", inverse = true)
	public abstract Collection<CciGroupComponent> getSectionGC();

	@HGConceptProperty(relationshipClass = "InterventionToSectionCPV", inverse = true)
	public abstract Collection<CciInterventionComponent> getSectionIC();

	@HGConceptProperty(relationshipClass = "TissueToSectionCPV", inverse = true)
	public abstract Collection<CciTissueComponent> getSectionTC();

	public SortedSet<CciTabular> getSortedChildren() {
		return new TreeSet<CciTabular>(getChildren());
	}

	public SortedSet<CciApproachTechniqueComponent> getSortedSectionApproachTechniqueComponents() {
		return new TreeSet<CciApproachTechniqueComponent>(getContainingPage().getSectionATC());
	}

	public SortedSet<CciDeviceAgentComponent> getSortedSectionDeviceAgentComponents() {
		return new TreeSet<CciDeviceAgentComponent>(getContainingPage().getSectionDAC());
	}

	public SortedSet<CciGroupComponent> getSortedSectionGroupComponents() {
		return new TreeSet<CciGroupComponent>(getContainingPage().getSectionGC());
	}

	public SortedSet<CciInterventionComponent> getSortedSectionInterventionComponents() {
		return new TreeSet<CciInterventionComponent>(getContainingPage().getSectionIC());
	}

	public SortedSet<CciTissueComponent> getSortedSectionTissueComponents() {
		return new TreeSet<CciTissueComponent>(getContainingPage().getSectionTC());
	}

	@HGConceptProperty(relationshipClass = "TissueCPV")
	public abstract CciTissueComponent getTissueComponent();

	@Override
	@HGClassDeterminant(classes = { SECTION, BLOCK, GROUP, RUBRIC, CCICODE })
	public abstract String getTypeCode();

	@HGConceptProperty(relationshipClass = "ValidationCCICPV", inverse = true)
	public abstract Collection<CciValidation> getValidations();

	@Override
	public boolean isValidCode() {
		boolean isValidCode = true;
		Collection<CciTabular> children = getChildren();
		for (CciTabular cciTabular : children) {
			if (ConceptStatus.ACTIVE.name().equals(cciTabular.getStatus())) {
				isValidCode = false;
				break;
			}
		}
		return isValidCode;
	}

	public abstract void setApproachTechniqueComponent(CciApproachTechniqueComponent wrapper);

	@Override
	public void setContextAccess(ContextAccess access) {
		this.contextAccess = access;
	}

	public abstract void setDeviceAgentComponent(CciDeviceAgentComponent wrapper);

	@Override
	public abstract void setElementId(Long elementId);

	public abstract void setGroupComponent(CciGroupComponent wrapper);

	public abstract void setInterventionComponent(CciInterventionComponent wrapper);

	public abstract void setInvasivenessLevel(CciInvasivenessLevel wrapper);

	public abstract void setOmitCodeXml(@HGLang String language, String xml);

	public abstract void setTissueComponent(CciTissueComponent wrapper);
}
