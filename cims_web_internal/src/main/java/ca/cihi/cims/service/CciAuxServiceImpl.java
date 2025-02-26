package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciAttribute;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.data.mapper.CciAuxMapper;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributeReferenceRefLink;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;
import ca.cihi.cims.model.SimpleMap;

@Component
public class CciAuxServiceImpl implements CciAuxService {

	private static final Log LOGGER = LogFactory.getLog(CciAuxServiceImpl.class);

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private CciAuxMapper auxMapper;

	@Override
	public String getAttributeNote(Long contextId, Long attributeId, Language language) {
		return auxMapper.getAttributeNote(contextId, attributeId, language.getCode());
	}

	@Override
	public Map<String, String> getCCISections(String baseClassification, String versionCode) {

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		List<SimpleMap> sMap = auxMapper.getCCISections(context.getContextId().getContextId());

		Map<String, String> sections = new TreeMap<String, String>();
		for (SimpleMap s : sMap) {
			sections.put(s.getKey(), s.getKey() + " " + s.getValue());
		}

		return sections;
	}

	@Override
	public List<CciComponentRefLink> getComponentReferences(String baseClassification, String versionCode,
			long componentId) {
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("componentId", componentId);

		return auxMapper.getComponentReferences(params);
	}

	@Override
	public <T> List<CciComponentModel> getComponents(String baseClassification, String versionCode, String sectionCode,
			String status, Class<T> clazz, String componentRefLink) {

		Set<T> componentMap = getComponentsFromFramework(baseClassification, versionCode, sectionCode, status, clazz,
				componentRefLink);

		// From here, you need to convert from map to model
		List<CciComponentModel> compModels = new ArrayList<CciComponentModel>();

		// If things start to go bad, you have to do instanceof here to find out what exactly the type is
		// This most likely wont work in other places where wrappers dont have a base class like components do
		// Also if there are unique properties it gets bad as well...
		for (T c : componentMap) {
			CciComponentModel model = CciComponentModel.convert((CciComponent) c);
			compModels.add(model);
		}

		return compModels;

	}

	private <T> Set<T> getComponentsFromFramework(String baseClassification, String versionCode, String sectionCode,
			String status, Class<T> clazz, String componentRefLink) {

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<CciTabular> cciRef = ref(CciTabular.class);
		Ref<T> componentRef = ref(clazz);

		List<FindCriterion> crit = new ArrayList<FindCriterion>();
		crit.add(cciRef.eq("typeCode", CciTabular.SECTION));
		crit.add(cciRef.eq("code", sectionCode));
		crit.add(cciRef.link(componentRefLink, componentRef));

		status = StringUtils.upperCase(status);

		if (status.equals(ConceptStatus.ACTIVE.name())) {
			crit.add(componentRef.eq("status", status));
		} else if (status.equals(ConceptStatus.DISABLED.name())) {
			crit.add(componentRef.eq("status", status));
		}

		Iterator<T> components = context.find(componentRef, crit.toArray(new FindCriterion[crit.size()]));

		Set<T> componentMap = new TreeSet<T>();
		while (components.hasNext()) {
			componentMap.add(components.next());
		}

		return componentMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CciComponentModel> getComponentsSQL(String baseClassification, String versionCode, String sectionCode,
			String status, String clazz, String componentRefLink) {

		List<CciComponentModel> compModels = new ArrayList<CciComponentModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("sectionCode", sectionCode);
		params.put("componentRefLink", componentRefLink);
		params.put("clazz", clazz);

		auxMapper.getComponents(params);

		compModels = (ArrayList<CciComponentModel>) params.get("myData");

		// Remove the status we dont want
		List<CciComponentModel> compModelWithStatus = new ArrayList<CciComponentModel>();
		for (CciComponentModel compModel : compModels) {
			if (status.equalsIgnoreCase(compModel.getStatus()) || status.equalsIgnoreCase("ALL")) {
				compModelWithStatus.add(compModel);
			}
		}

		return compModelWithStatus;

	}

	@Override
	public CciAttributeGenericModel getGenericAttribute(Long contextId, Long attributeId) {
		return auxMapper.getGenericAttribute(contextId, attributeId);
	}

	@Override
	public List<CciAttributeGenericRefLink> getGenericAttributeReferences(String baseClassification,
			String versionCode, long genAttrElementId, String genAttrCode) {

		List<CciAttributeGenericRefLink> refList = new ArrayList<CciAttributeGenericRefLink>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		CciGenericAttribute genAttr = context.load(genAttrElementId);
		for (CciAttribute attr : genAttr.getAttributes()) {
			CciReferenceAttribute refAttr = attr.getReferenceAttribute();

			refList.add(new CciAttributeGenericRefLink(genAttrCode, refAttr.getCode(), refAttr
					.getDescription(Language.ENGLISH.getCode()), attr.getDescription(Language.ENGLISH.getCode()),
					refAttr.getStatus()));

			LOGGER.debug("Added " + refAttr.getCode());
		}

		// Ref<CciReferenceAttribute> ra = ref(CciReferenceAttribute.class);
		// Ref<CciAttribute> a = ref(CciAttribute.class);
		// Ref<CciGenericAttribute> ga = ref(CciGenericAttribute.class);
		//
		// Iterator<CciReferenceAttribute> results = context.find(ra, ga.eq("elementId", genAttrElementId),
		// a.link("referenceAttribute", ra), a.link("genericAttribute", ga));
		//
		// while (results.hasNext()) {
		// CciReferenceAttribute refAttr = results.next();
		//
		//
		//
		// refList.add(new CciAttributeGenericRefLink(genAttrCode, refAttr.getCode(), refAttr
		// .getDescription(Language.ENGLISH.getCode()), refAttr.getStatus()));
		// }

		return refList;
	}

	@Override
	public List<CciAttributeGenericRefLink> getGenericAttributeReferencesSQL(String baseClassification,
			String versionCode, long genAttrElementId, String genAttrCode) {

		List<CciAttributeGenericRefLink> refList = new ArrayList<CciAttributeGenericRefLink>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("genAttrElementId", genAttrElementId);

		// List<CciAttributeGenericRefLink> genRefLinks = auxMapper.getGenericAttributeReferences(params);
		// List<CciAttributeGenericRefLink> genRefLinks = auxMapper.getGenericAttributeReferences(params);
		// List<CciAttributeGenericRefLink> genRefLinks = auxMapper.getGenericAttributeReferences(params);

		auxMapper.getGenericAttributeReferences(params);

		List<CciAttributeGenericRefLink> genRefLinks = (ArrayList<CciAttributeGenericRefLink>) params.get("myData");

		return genRefLinks;
	}

	@Override
	public List<CciAttributeGenericModel> getGenericAttributes(String baseClassification, String versionCode,
			String attributeType, String status) {

		List<CciAttributeGenericModel> attrModels = new ArrayList<CciAttributeGenericModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<CciGenericAttribute> attrRef = ref(CciGenericAttribute.class);
		Ref<CciAttributeType> attributeTypeRef = ref(CciAttributeType.class);

		List<FindCriterion> crit = new ArrayList<FindCriterion>();
		crit.add(attributeTypeRef.eq("code", attributeType));
		crit.add(attrRef.link("type", attributeTypeRef));

		status = StringUtils.upperCase(status);

		if (status.equals(ConceptStatus.ACTIVE.name())) {
			crit.add(attrRef.eq("status", status));
		} else if (status.equals(ConceptStatus.DISABLED.name())) {
			crit.add(attrRef.eq("status", status));
		}

		Iterator<CciGenericAttribute> genericAttributes = context.find(attrRef,
				crit.toArray(new FindCriterion[crit.size()]));

		while (genericAttributes.hasNext()) {
			CciGenericAttribute genericAttribute = genericAttributes.next();
			CciAttributeGenericModel model = CciAttributeGenericModel.convert(genericAttribute);
			attrModels.add(model);
		}

		return attrModels;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CciAttributeGenericModel> getGenericAttributesSQL(String baseClassification, String versionCode,
			String attributeType) {

		List<CciAttributeGenericModel> attrModels = new ArrayList<CciAttributeGenericModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("attributeType", attributeType);

		// status = StringUtils.upperCase(status);
		// params.put("status", status); // ALL, ACTIVE, DISABLED

		// if (status.equals(ConceptStatus.ACTIVE.name())) {
		// params.put("status", status);
		// } else if (status.equals(ConceptStatus.DISABLED.name())) {
		// params.put("status", status);
		// }

		auxMapper.getGenericAttributes(params);

		attrModels = (ArrayList<CciAttributeGenericModel>) params.get("myData");

		return attrModels;
	}

	@Override
	public CciAttributeReferenceModel getReferenceAttribute(Long contextId, Long referenceAttributeId) {
		return auxMapper.getReferenceAttribute(contextId, referenceAttributeId);
	}

	@Override
	public List<CciAttributeReferenceInContextModel> getReferenceAttributeInContext(String baseClassification,
			String versionCode, long refAttrElementId) {

		List<CciAttributeReferenceInContextModel> refList = new ArrayList<CciAttributeReferenceInContextModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<CciReferenceAttribute> ra = ref(CciReferenceAttribute.class);
		Ref<CciAttribute> a = ref(CciAttribute.class);

		Iterator<CciAttribute> results = context.find(a, ra.eq("elementId", refAttrElementId),
				a.link("referenceAttribute", ra));

		while (results.hasNext()) {
			CciAttribute attr = results.next();
			CciGenericAttribute genAttr = attr.getGenericAttribute();

			refList.add(new CciAttributeReferenceInContextModel(/* refAttrCode, */attr.getElementId(), genAttr
					.getCode(), attr.getDescription(Language.ENGLISH.getCode()), attr.getDescription(Language.FRENCH
					.getCode()), attr.getStatus()));
		}

		return refList;
	}

	@Override
	public List<CciAttributeReferenceInContextModel> getReferenceAttributeInContextSQL(String baseClassification,
			String versionCode, long refAttrElementId) {
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);
		return auxMapper.getReferenceAttributesInContext(context.getContextId().getContextId(), refAttrElementId);
	}

	@Override
	public String getReferenceAttributeNoteDescription(Long contextId, Long referenceAttributeId, Language language) {
		return auxMapper.getReferenceAttributeNoteDescription(contextId, referenceAttributeId, language.getCode());
	}

	@Override
	public List<CciAttributeReferenceRefLink> getReferenceAttributeReferences(String baseClassification,
			String versionCode, long refAttrElementId, String refAttrCode, String attributeType) {

		String attributeCodeXML = "";

		if (attributeType.equals("S")) {
			attributeCodeXML = "%<STATUS_REF>" + refAttrCode + "</STATUS_REF>%";
		} else if (attributeType.equals("L")) {
			attributeCodeXML = "%<LOCATION_REF>" + refAttrCode + "</LOCATION_REF>%";
		} else if (attributeType.equals("M")) {
			attributeCodeXML = "%<LOCATION_REF>" + refAttrCode + "</LOCATION_REF>%";
		} else if (attributeType.equals("E")) {
			attributeCodeXML = "%<EXTENT_REF>" + refAttrCode + "</EXTENT_REF>%";
		}

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("attributeCode", refAttrCode);
		params.put("attributeCodeXML", attributeCodeXML);

		List<CciAttributeReferenceRefLink> attributeReferenceLinks = auxMapper.getReferenceAttributeReferences(params);

		return attributeReferenceLinks;
	}

	@Override
	public List<CciAttributeReferenceModel> getReferenceAttributes(String baseClassification, String versionCode,
			String attributeType, String status) {

		List<CciAttributeReferenceModel> attrModels = new ArrayList<CciAttributeReferenceModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<CciReferenceAttribute> attrRef = ref(CciReferenceAttribute.class);
		Ref<CciAttributeType> attributeTypeRef = ref(CciAttributeType.class);

		List<FindCriterion> crit = new ArrayList<FindCriterion>();
		crit.add(attributeTypeRef.eq("code", attributeType));
		crit.add(attrRef.link("type", attributeTypeRef));

		status = StringUtils.upperCase(status);

		if (status.equals(ConceptStatus.ACTIVE.name())) {
			crit.add(attrRef.eq("status", status));
		} else if (status.equals(ConceptStatus.DISABLED.name())) {
			crit.add(attrRef.eq("status", status));
		}

		Iterator<CciReferenceAttribute> referenceAttributes = context.find(attrRef,
				crit.toArray(new FindCriterion[crit.size()]));

		while (referenceAttributes.hasNext()) {
			CciReferenceAttribute referenceAttribute = referenceAttributes.next();
			CciAttributeReferenceModel model = CciAttributeReferenceModel.convert(referenceAttribute);
			attrModels.add(model);
		}

		return attrModels;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CciAttributeReferenceModel> getReferenceAttributesSQL(String baseClassification, String versionCode,
			String attributeType) {

		List<CciAttributeReferenceModel> attrModels = new ArrayList<CciAttributeReferenceModel>();

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", context.getContextId().getContextId());
		params.put("attributeType", attributeType);

		// status = StringUtils.upperCase(status);
		// params.put("status", status); // ALL, ACTIVE, DISABLED

		// if (status.equals(ConceptStatus.ACTIVE.name())) {
		// params.put("status", status);
		// } else if (status.equals(ConceptStatus.DISABLED.name())) {
		// params.put("status", status);
		// }

		auxMapper.getReferenceAttributes(params);

		attrModels = (ArrayList<CciAttributeReferenceModel>) params.get("myData");

		return attrModels;
	}

	@Override
	public String getReferenceAttributeType(Long contextId, Long referenceAttributeId) {
		return auxMapper.getReferenceAttributeType(contextId, referenceAttributeId);
	}

}
