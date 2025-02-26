package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.*;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.LinkTrans;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.icd.IcdValidation;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class SearchIntegrationTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(SearchIntegrationTest.class);

	@Test(expected = IllegalArgumentException.class)
	public void findByBogusProperty() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> tab = ref(IcdTabular.class);
		ctxtx.find(tab, eq(tab, "noSuchProperty", "some value"));
	}

	@Test
	public void findByMultipleCriteria() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		String expectedCode = "01";
		String expectedTypeCode = IcdTabular.CHAPTER;
		String expectedShortDescSubstring = "parasitic";

		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, eq(icdTab, "code", expectedCode),
				eq(icdTab, "typeCode", expectedTypeCode),
				like(icdTab, "shortDescription", "%" + expectedShortDescSubstring + "%"));

		while (iterator.hasNext()) {
			IcdTabular result = iterator.next();

			Assert.assertEquals(expectedCode, result.getCode());
			Assert.assertEquals(expectedTypeCode, result.getTypeCode());
			Assert.assertTrue(result.getShortDescription("ENG").contains(expectedShortDescSubstring));

			System.err.println(result);
		}
	}

	@Test
	public void printBlocksOfChapter() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> chapter = ref(IcdTabular.class);
		Ref<IcdTabular> blocks = ref(IcdTabular.class);

		Iterator<IcdTabular> children = ctxtx.find(blocks, eq(chapter, "code", "02"), new LinkTrans(blocks, "parent",
				chapter), eq(blocks, "typeCode", "Block"));

		while (children.hasNext()) {
			System.err.println(children.next());
		}
	}

	@Test
	public void simpleTransitiveTest() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> concept = ref(IcdTabular.class);
		Ref<IcdTabular> parent = ref(IcdTabular.class);

		Iterator<IcdTabular> parents = ctxtx.find(parent, eq(concept, "code", "A00"),
				linkTrans(concept, "parent", parent));

		while (parents.hasNext()) {
			System.err.println(parents.next());
		}
	}

	@Test
	public void testFindByCode() {
		String keyword = "A01";

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, eq(icdTab, "code", keyword));

		Assert.assertTrue("There must be at least one result", iterator.hasNext());

		iterator.next();

		Assert.assertFalse("We should only find one entity.", iterator.hasNext());
	}

	@Test
	public void testFindByCode2() {
		String keyword = "Z96";

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, eq(icdTab, "code", keyword));

		Assert.assertTrue("There must be at least one result", iterator.hasNext());

		IcdTabular z96 = iterator.next();

		Assert.assertFalse("We should only find one entity.", iterator.hasNext());
		System.err.println(z96.getElementId());
		System.err.println(z96.getUserDescription("ENG"));
	}

	@Test
	public void testFindByProperty() {

		String keyword = "tonsils";

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, like(icdTab, "shortDescription", "%" + keyword + "%"));

		Assert.assertTrue("There must be at least one result", iterator.hasNext());

		int MAX_ENTITIES = 100;
		for (int i = 0; i < MAX_ENTITIES && iterator.hasNext(); i++) {

			IcdTabular icdTabular = iterator.next();
			String shortDesc = icdTabular.getShortDescription("ENG");

			Assert.assertNotNull("There should be a short description.", shortDesc);

			Assert.assertTrue("The property should contain the keyword, since that's what we searched for.",
					shortDesc.contains(keyword));
		}
	}

	@Test
	public void testFindByTypeCodeProperty() {
		String type = "Chapter";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		if (contextId == null) {
			return;
		}
		ContextAccess ctxtx = provider.findContext(contextId);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);

		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, eq(icdTab, "typeCode", type));

		Assert.assertTrue("There must be at least one result", iterator.hasNext());

		int MAX_ENTITIES = 100;
		for (int i = 0; i < MAX_ENTITIES && iterator.hasNext(); i++) {

			IcdTabular icdTabular = iterator.next();

			Assert.assertEquals("Results must be of type " + type, type, icdTabular.getTypeCode());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testFindOneReturnsMultiple() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		// List<FindCriterion> criteria = new ArrayList<FindCriterion>();

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);

		// This should throw an exception because there will be more than one
		// result
		IcdTabular icd = ctxtx.findOne(icdTab);
	}

	@Test
	public void testIcdValidations() {
		ContextAccess c = provider.findContext(ContextDefinition
				.forVersion("ICD-10-CA", CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);

		Iterator<IcdTabular> find = c.find(icdTab, eq(icdTab, "code", "C48.0"));
		IcdTabular next = find.next();

		// System.err.println(next);

		for (IcdValidation validation : next.getValidations()) {
			System.err.println(validation);
		}

	}

	@Test
	public void testReadingIcdTabular() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);

		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, icdTab.eq("typeCode", "Category"),
				icdTab.like("code", "A0%"));
		int max = 5;

		int count = 0;
		while (iterator.hasNext() && count++ < max) {
			System.err.println(iterator.next());
		}
	}

	@Test
	public void testSearchByStatus() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		String expectedStatus = "ACTIVE";
		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> find = ctxtx.find(icdTab, eq(icdTab, "status", expectedStatus));

		Assert.assertTrue(find.hasNext());

		IcdTabular tabular = find.next();
		Assert.assertEquals(expectedStatus, tabular.getStatus());
	}

	@Test
	public void testSearchingByEnum() {

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		// TODO! COME BACK AND FIX THIS, SINCE IT COULD WORK NOW!

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Ref<DaggerAsterisk> da = ref(DaggerAsterisk.class);

		// Iterator<IcdTabular> results = ctxtx.find3(icdTab,
		// link(icdTab, "daggerAsteriskConcept", da), eq(da, "code", "*"));

		Iterator<IcdTabular> results = ctxtx.find(icdTab, link(icdTab, "daggerAsteriskConcept", da),
				eq(da, "code", "*"));

		IcdTabular concept = results.next();

		Assert.assertEquals("*", concept.getDaggerAsterisk());
	}
}
