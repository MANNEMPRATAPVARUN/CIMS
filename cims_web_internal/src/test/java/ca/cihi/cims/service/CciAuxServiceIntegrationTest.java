package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributeReferenceRefLink;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CciAuxServiceIntegrationTest {

	private static final String CCI_GENERIC_ATTRIBUTE_CODE = "ZU";
	private static final String CCI_REFERENCE_ATTRIBUTE_CODE = "S22";

	private final String baseClassification = "CCI";
	private final String versionCode = CIMSTestConstants.TEST_VERSION;

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private ContextProvider contextProvider;

	// ------------------------------------------------------

	private CciAttributeGenericModel getCciAttributeGenericModelbyCode(String code) {
		List<CciAttributeGenericModel> list = auxService.getGenericAttributes(baseClassification, versionCode, "M",
				"ACTIVE");
		assertTrue(list.size() > 0);
		for (CciAttributeGenericModel model : list) {
			if (model.getCode().equals(code)) {
				return model;
			}
		}
		return null;
	}

	private CciAttributeReferenceModel getCciReferenceAttributeByCode(String code) {
		List<CciAttributeReferenceModel> list = auxService.getReferenceAttributesSQL(baseClassification, versionCode,
				"S");
		assertTrue(list.size() > 0);
		for (CciAttributeReferenceModel model : list) {
			if (model.getCode().equals(code)) {
				return model;
			}
		}
		return null;
	}

	@Test
	public void getComponentReferencesTest() {
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, versionCode);
		ContextAccess context = contextProvider.findContext(cd);

		Ref<CciGroupComponent> compRef = ref(CciGroupComponent.class);
		Ref<CciTabular> cciTab = ref(CciTabular.class);

		// Iterator<CciGroupComponent> comps = context.find(compRef, compRef.eq("code", "BA"));
		Iterator<CciGroupComponent> comps = context.find(compRef, cciTab.eq("typeCode", CciTabular.SECTION), cciTab.eq(
				"code", "2"), cciTab.link("sectionGC", compRef), compRef.eq("code", "BA"));

		assertTrue(comps.hasNext());

		CciGroupComponent comp = comps.next();
		List<CciComponentRefLink> compRefs = auxService.getComponentReferences(baseClassification, versionCode, comp
				.getElementId());

		Iterator<CciComponentRefLink> compRefsIterator = compRefs.iterator();
		assertTrue(compRefsIterator.hasNext());
	}

	@Test
	public void getComponentsTest() {
		List<CciComponentModel> compModels = auxService.getComponents(baseClassification, versionCode, "5", "ACTIVE",
				CciGroupComponent.class, "sectionGC");
		assertTrue(compModels.size() > 0);
		for (CciComponentModel model : compModels) {
			assertTrue(model.getStatus().equalsIgnoreCase("ACTIVE"));
		}
	}

	@Test
	public void getGenericAttributesTest() {
		List<CciAttributeGenericModel> genAttrs = auxService.getGenericAttributes(baseClassification, versionCode, "M",
				"ACTIVE");
		assertTrue(genAttrs.size() > 0);
		for (CciAttributeGenericModel genAttr : genAttrs) {
			assertTrue(genAttr.getStatus().equalsIgnoreCase("ACTIVE"));
		}
	}

	@Test
	public void getGenericAttrReferencesTest() {
		CciAttributeGenericModel m = getCciAttributeGenericModelbyCode(CCI_GENERIC_ATTRIBUTE_CODE);
		assertNotNull(m);
		List<CciAttributeGenericRefLink> genAttrRefLinks = auxService.getGenericAttributeReferencesSQL(
				baseClassification, versionCode, m.getElementId(), "");
		assertTrue(genAttrRefLinks.size() > 0);
		for (CciAttributeGenericRefLink genAttrRefLink : genAttrRefLinks) {
			assertTrue(genAttrRefLink.getStatus().equalsIgnoreCase("ACTIVE"));
		}
	}

	@Test
	public void getReferenceAttributeInContextTest() {
		CciAttributeReferenceModel m = getCciReferenceAttributeByCode(CCI_REFERENCE_ATTRIBUTE_CODE);
		assertNotNull(m);
		List<CciAttributeReferenceInContextModel> refInContextAttrs = auxService.getReferenceAttributeInContext(
				baseClassification, versionCode, m.getElementId());
		assertTrue(refInContextAttrs.size() > 0);
		for (CciAttributeReferenceInContextModel refInContextAttr : refInContextAttrs) {
			assertTrue(refInContextAttr.getStatus().equalsIgnoreCase("ACTIVE"));
		}
	}

	@Test
	public void getReferenceAttributeReferences() {
		CciAttributeReferenceModel m = getCciReferenceAttributeByCode(CCI_REFERENCE_ATTRIBUTE_CODE);
		List<CciAttributeReferenceRefLink> refLinks = auxService.getReferenceAttributeReferences(baseClassification,
				versionCode, m.getElementId(), "M01", "M");
		assertTrue(refLinks.size() > 0);
		for (CciAttributeReferenceRefLink refLink : refLinks) {
			assertTrue(refLink.getReferenceAttributeCode().equalsIgnoreCase("M01"));
		}
	}

	@Test
	public void getReferenceAttributes() {
		List<CciAttributeReferenceModel> refAttrs = auxService.getReferenceAttributes(baseClassification, versionCode,
				"M", "ACTIVE");
		assertTrue(refAttrs.size() > 0);
		for (CciAttributeReferenceModel refAttr : refAttrs) {
			assertTrue(refAttr.getStatus().equalsIgnoreCase("ACTIVE"));
		}
	}

	@Test
	public void getSectionsTest() {
		Map<String, String> sections = auxService.getCCISections(baseClassification, versionCode);
		assertTrue(sections.size() > 0);
		// for (String section : sections.values()) {
		// LOGGER.debug(section);
		// }
	}
}
