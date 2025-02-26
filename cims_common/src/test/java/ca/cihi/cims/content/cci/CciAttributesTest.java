package ca.cihi.cims.content.cci;

import static ca.cihi.cims.bll.query.FindCriteria.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class CciAttributesTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	private ContextAccess buildContext() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		if (contextId == null) {
			LOGGER.warn("Test skipping due to lack of test data.");
			return null;
		}

		return provider.findContext(contextId);
	}

	@Test
	public void testGetAllAttributeTypes() {
		ContextAccess context = buildContext();
		Iterator<CciAttributeType> attributeTypeIterator = context.findAll(CciAttributeType.class);

		while (attributeTypeIterator.hasNext()) {
			CciAttributeType attr = attributeTypeIterator.next();
			LOGGER.info(attr.getCode());
		}
	}

	@Test
	public void testGetAllGenericAttributes() {
		ContextAccess context = buildContext();
		Iterator<CciGenericAttribute> attributesIterator = context.findAll(CciGenericAttribute.class);

		while (attributesIterator.hasNext()) {
			CciGenericAttribute attr = attributesIterator.next();
			LOGGER.info(attr.getCode());
		}

	}

	@Test
	public void testGetAllReferenceAttributeInContextReferences() {

		// Seriously.. can this method name get any longer??

		ContextAccess context = buildContext();

		Ref<CciReferenceAttribute> ra = ref(CciReferenceAttribute.class);
		Ref<CciAttribute> a = ref(CciAttribute.class);
		// Ref<CciGenericAttribute> ga = ref(CciGenericAttribute.class);

		Iterator<CciAttribute> results = context.find(a, ra.eq("code", "S01"), a.link("referenceAttribute", ra));

		while (results.hasNext()) {
			CciAttribute attr = results.next();
			CciGenericAttribute genAttr = attr.getGenericAttribute();
			LOGGER.info(genAttr.getCode() + " " + attr.getDescription("ENG") + " " + attr.getDescription("FRA"));
		}

	}

	@Test
	public void testGetGenericAttribute() {
		ContextAccess context = buildContext();
		Ref<CciGenericAttribute> attribute = ref(CciGenericAttribute.class);
		Iterator<CciGenericAttribute> attributesIterator = context.find(attribute, attribute.eq("code", "PD"));

		while (attributesIterator.hasNext()) {
			CciGenericAttribute attr = attributesIterator.next();
			LOGGER.info(attr.getCode() + " " + attr.getType().getCode());
		}

	}

	@Test
	public void testGetGenericAttributesByAttributeType() {
		ContextAccess context = buildContext();
		Ref<CciGenericAttribute> attribute = ref(CciGenericAttribute.class);
		Ref<CciAttributeType> attributeType = ref(CciAttributeType.class);

		Iterator<CciGenericAttribute> attributesIterator = context.find(attribute, attributeType.eq("code", "S"),
				attribute.link("type", attributeType));

		while (attributesIterator.hasNext()) {
			CciGenericAttribute attr = attributesIterator.next();
			LOGGER.info(attr.getCode() + " " + attr.getType().getCode());
		}

	}

	@Test
	public void testGetReferenceAttributesViaSearch() {
		ContextAccess context = buildContext();

		Ref<CciReferenceAttribute> ra = ref(CciReferenceAttribute.class);
		Ref<CciAttribute> a = ref(CciAttribute.class);
		Ref<CciGenericAttribute> ga = ref(CciGenericAttribute.class);

		// long elementId = 1933623L;
		long elementId = Long.valueOf("1933623");

		Iterator<CciReferenceAttribute> results = context.find(ra, ga.eq("elementId", elementId),
				a.link("referenceAttribute", ra), a.link("genericAttribute", ga));

		while (results.hasNext()) {
			CciReferenceAttribute refAttr = results.next();
			LOGGER.info(refAttr.getCode() + " " + refAttr.getStatus() + " " + refAttr.getDescription("ENG"));

		}

	}

	@Test
	public void testGetReferenceAttributeValidations() {

		// Pretend we passed in the Attribute type, ex: S/L/E/M
		// Using this we can determine which prop to use
		String attributeType = "S";

		ContextAccess context = buildContext();

		Ref<CciReferenceAttribute> ra = ref(CciReferenceAttribute.class);
		Ref<CciValidation> v = ref(CciValidation.class);

		ArrayList<FindCriterion> criteria = new ArrayList<FindCriterion>();
		criteria.add(ra.eq("code", "S45"));
		criteria.add(ra.link("statusValidation", v));
		// extentValidation
		// locationValidation

		Iterator<CciValidation> results = context.find(v, criteria.toArray(new FindCriterion[criteria.size()]));

		while (results.hasNext()) {
			CciValidation validation = results.next();

			LOGGER.info(validation.getTabularConcept().getCode() + " "
					+ validation.getTabularConcept().getShortDescription("ENG") + "  "
					+ validation.getTabularConcept().getStatus());
		}

	}
}
