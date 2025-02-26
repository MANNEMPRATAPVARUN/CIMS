package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Ignore;
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
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.util.timer.Perf;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( { "classpath:spring/applicationContext-test.xml" })
public class HgContextAccessIntegrationTest {

	private final Logger LOGGER = LogManager.getLogger(HgContextAccessIntegrationTest.class);

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Test
	public void findAllAsteriskOptions() {

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Iterator<DaggerAsterisk> iterator = ctxtx.findAll(DaggerAsterisk.class);
		while (iterator.hasNext()) {
			LOGGER.debug(iterator.next().getCode());
		}
	}

	@Test
	@Ignore
	public void iterateEverything() {
		for (String classification : provider.findBaseClassifications()) {
			Collection<String> versionCodes = provider.findVersionCodes(classification);
			for (String versionCode : versionCodes) {

				LOGGER.debug("Starting " + classification + " / " + versionCode);

				ContextAccess context = provider.findContext(ContextDefinition.forVersion(classification, versionCode));

				popAll(context.findAll(IcdTabular.class));
				popAll(context.findAll(DaggerAsterisk.class));
				// popAll(context.findAll(CciTabular.class));

			}
		}
	}

	private String nullsafe(String asterisk) {
		if (asterisk == null) {
			asterisk = "";
		}
		return asterisk;
	}

	private <T> void popAll(Iterator<T> iterator) {

		long count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			LOGGER.debug(++count);
		}
	}

	@Test
	@Ignore
	public void printAsterisks() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		for (String code : asList("A02.2", "D63", "D64", "D77")) {
			Ref<IcdTabular> icdTab = ref(IcdTabular.class);
			Iterator<IcdTabular> find = ctxtx.find(icdTab, icdTab.eq("code", code));
			IcdTabular concept = find.next();
			LOGGER.debug(concept.getCode() + concept.getDaggerAsterisk() + "\t"
					+ concept.getConceptCodeWithDecimalDagger());
		}
	}

	@Test
	@Ignore
	public void printAsterisks2() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> find = ctxtx.find(icdTab, icdTab.like("code", "E35%"));

		while (find.hasNext()) {

			IcdTabular concept = find.next();

			LOGGER.debug(concept.getCode() + nullsafe(concept.getDaggerAsterisk()));
		}

	}

	@Test
	@Ignore
	public void printOutAllCodesWithAsterisks() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = ctxtx.find(icdTab, icdTab.link("daggerAsteriskConcept",
				ref(DaggerAsterisk.class)));
		while (iterator.hasNext()) {
			IcdTabular concept = iterator.next();
			if (concept.getDaggerAsterisk() != null) {
				LOGGER.debug(concept.getCode() + " " + concept.getDaggerAsterisk());
			}
		}
	}

	@Test
	public void testCacheMaintenanceForNewConcepts() {
		ContextAccess context = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		ContextAccess change = context.createChangeContext(null);

		Ref<IcdTabular> icd = ref(IcdTabular.class);

		IcdTabular a00 = change.findOne(icd, icd.eq("code", "A00"));

		IcdTabular child = IcdTabular.create(change, "A000.000", IcdTabular.CATEGORY);

		child.setParent(a00);

		assertTrue(a00.getChildren().contains(child));

		child.setParent(null);
		assertNull(child.getParent());

		assertTrue("A parentless child should not appear in its former parent's children list.", a00.getChildren()
				.contains(child));

		child.setParent(a00);
		assertTrue(a00.getChildren().contains(child));
	}

	@Test
	public void testCaching() {
		// Find something that should have a parent
		// find its parent
		long blockId = template.queryForObject("Select elementid from element e, class c "
				+ "where e.classid=c.classid and c.classname='Category' and rownum=1", Long.class);

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Perf.start("firstInvocation");
		IcdTabular block = ctxtx.load(blockId);
		block.getShortDescription("ENG");
		block.getShortDescription("ENG");
		Perf.stop("firstInvocation");

		for (int i = 0; i < 10; i++) {
			Perf.start("subsequentInvocation");
			block = ctxtx.load(blockId);
			block.getShortDescription("ENG");
			Perf.stop("subsequentInvocation");
		}

		Perf.displayAll();
	}

	@Test
	public void testConceptPropertyCacheMaintenance() {
		ContextAccess context = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		ContextAccess change = context.createChangeContext(null);

		Ref<IcdTabular> icd = ref(IcdTabular.class);
		IcdTabular a00 = change.findOne(icd, icd.eq("code", "A00"));

		BaseConcept concept = a00.getParent();
		if (concept instanceof IcdTabular) {
			IcdTabular parent = (IcdTabular) concept;
			assertNotNull(parent);

			assertTrue(parent.getChildren().contains(a00));

			a00.setParent(null);
			assertNull(a00.getParent());

			assertTrue("A parentless child should not appear in its former parent's children list.", parent
					.getChildren().contains(a00));

			a00.setParent(parent);
			assertTrue(parent.getChildren().contains(a00));
		}

	}

	@Test
	public void testConceptPropertyRead() {

		// Find something that should have a parent
		// find its parent
		long blockId = template.queryForObject("Select elementid from element e, class c "
				+ "where e.classid=c.classid and c.classname='Category' and rownum=1", Long.class);

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		IcdTabular block = ctxtx.load(blockId);

		Assert.assertNotNull(block);

		// IcdTabular parent = block.getParent();
		// Assert.assertNotNull(parent);

		IcdTabular tab = block;
		long startTime = System.currentTimeMillis();

		while (tab != null) {
			LOGGER.debug(tab.getTypeCode() + " " + ((Identified) tab).getElementId() + " :" + tab.getCode() + " "
					+ tab.getUserDescription("ENG"));

			BaseConcept concept = tab.getParent();
			if (!(concept instanceof IcdTabular)) {
				fail("Expecting the parent should be IcdTabular!");
			}

			tab = (IcdTabular) concept;

			if (tab.getTypeCode().equals(IcdTabular.CHAPTER)) {
				break;
			}
		}
		long endTime = System.currentTimeMillis();

		LOGGER.debug(endTime - startTime);
	}

	@Test
	public void testCreatingNewIcdTabular() {
		ContextAccess context = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		ContextAccess change = context.createChangeContext(null);

		Ref<IcdTabular> parent = ref(IcdTabular.class);
		IcdTabular a00 = change.findOne(parent, parent.eq("code", "A00"));

		String newCode = "A00.111";
		IcdTabular newChild = IcdTabular.create(change, newCode, IcdTabular.BLOCK);
		newChild.setShortDescription("ENG", "testValue");
		newChild.setParent(a00);
		change.persist();

		// Reload the change context
		change = provider.findContext(change.getContextId());
		Ref<IcdTabular> newConceptRef = ref(IcdTabular.class);
		newChild = change.findOne(newConceptRef, newConceptRef.eq("code", newCode));

		assertNotNull(newChild);
		assertEquals("testValue", newChild.getShortDescription("ENG"));

		BaseConcept concept = newChild.getParent();
		if (concept instanceof IcdTabular) {
			IcdTabular icdParent = (IcdTabular) concept;
			assertEquals("A00", icdParent.getCode());
		} else {
			fail("Expecting the parent should be IcdTabular!");
		}

	}

	@Test
	public void testDetermineContainingIdPath() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		String idPath = ctxtx.determineContainingIdPath("A00-A09");
		Assert.assertTrue(idPath.contains("/"));
	}

	@Test
	public void testGetNestingLevelReturns() {

		long categoryId = template.queryForObject("Select elementid from element e, class c where e.classid=c.classid "
				+ "and c.classname='Category' and c.baseClassificationName='ICD-10-CA' and rownum=1", Long.class);

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		IcdTabular find = ctxtx.load(categoryId);

		LOGGER.debug(find);

		Assert.assertTrue(find.getNestingLevel() >= 1);
	}

	@Test
	public void testInverseConceptPropertyRead() {

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> chapters = ctxtx.find(icdTab, icdTab.eq("typeCode", "Chapter"));

		Assert.assertTrue(chapters.hasNext());

		IcdTabular chapter = chapters.next();

		Assert.assertFalse("The chapter we loaded should have children.", chapter.getChildren().isEmpty());
	}

	@Test
	public void testNestingLevelPerformance() {
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA", "2015"));

		Iterator<IcdTabular> find = ctxtx.findAll(IcdTabular.class);

		int max = 10;// 120;
		int i = 0;

		while (find.hasNext() && i++ < max) {
			IcdTabular tabular = find.next();
			int nestingLevel = tabular.getNestingLevel();

			LOGGER.debug(tabular.getCode() + " nesting level: " + nestingLevel);
		}
	}

	@Test
	public void testReadingIcdTabular() {

		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Iterator<IcdTabular> iterator = ctxtx.findAll(IcdTabular.class);

		int MAX_ENTITIES = 1;

		for (int i = 0; i < MAX_ENTITIES && iterator.hasNext(); i++) {

			Perf.start("readAllEntities");
			IcdTabular icdTabular = iterator.next();
			LOGGER.debug(icdTabular);

			icdTabular.getElementId();

			Assert.assertNotNull(icdTabular);

			Assert.assertNotNull(icdTabular.getCode());

			Perf.start("shortDescription1");
			icdTabular.getShortDescription("ENG");
			Perf.stop("shortDescription1");
			Perf.start("shortDescription2");
			icdTabular.getShortDescription("ENG");
			Perf.stop("shortDescription2");

			icdTabular.getCodeAlsoXml("FRA");
			icdTabular.getCodeAlsoXml("FRA");

			icdTabular.getUserDescription("FRA");
			icdTabular.getTypeCode();

			icdTabular.getDefinitionXml("FRA");
			icdTabular.getIncludeXml("FRA");
			icdTabular.getExcludeXml("FRA");
			icdTabular.getLongDescription("ENG");
			icdTabular.getTableOutput("FRA");
			icdTabular.getDaggerAsterisk();

			Perf.stop("readAllEntities");
		}

		Perf.displayAll();
	}

	@Test
	public void testSortedBlocksAndChildrenWithAsterisks() {
		ContextAccess context = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icd = ref(IcdTabular.class);
		Iterator<IcdTabular> chapters = context.find(icd, icd.eq("typeCode", IcdTabular.CHAPTER));

		// while (chapters.hasNext()) {
		IcdTabular chapter = chapters.next();

		LOGGER.info("Examining chapter " + chapter);

		for (IcdTabular block : chapter.getSortedBlocks()) {
			Assert.assertEquals(chapter, block.getContainingPage());
		}

		for (IcdTabular hasAsterisk : chapter.getChildrenWithAsterisks()) {
			Assert.assertEquals(chapter, hasAsterisk.getContainingPage());
		}
		// }
	}

	@Test
	public void testUpdateHtmlProperty() {
		String code = "O08.99";

		Random random = new Random();
		String testValue = "<!-- " + random.nextLong() + " -->";

		ContextAccess tx1 = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> result = tx1.find(icdTab, icdTab.eq("code", code));

		IcdTabular concept = result.next();
		LOGGER.debug(concept.getPresentationHtml("ENG"));
		concept.setPresentationHtml("ENG", testValue);
		tx1.persist();

		ContextAccess tx2 = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> icdTab2 = ref(IcdTabular.class);
		Iterator<IcdTabular> result2 = tx2.find(icdTab2, icdTab2.eq("code", code));

		Assert.assertEquals(testValue, result2.next().getPresentationHtml("ENG"));

	}

}
