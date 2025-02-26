package ca.cihi.cims.content.icd;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.DataPropertyVersion;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class RealizationTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	ClassService classService;

	@Autowired
	NonContextOperations nco;

	@Autowired
	private ContextOperations operations;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	private void discardConflicts(HashMap<ElementVersion, ElementVersion> conflicts, ContextAccess context) {
		Iterator<Entry<ElementVersion, ElementVersion>> entry = conflicts.entrySet().iterator();

		while (entry.hasNext()) {
			Entry<ElementVersion, ElementVersion> e = entry.next();
			ElementVersion ev = e.getKey();
			LOGGER.info("Discarding : " + ev.getBusinessKey());
			context.discardFromChangeContext(ev);

		}
	}

	private void printConflicts(HashMap<ElementVersion, ElementVersion> conflicts) {
		Iterator<Entry<ElementVersion, ElementVersion>> entry = conflicts.entrySet().iterator();

		while (entry.hasNext()) {
			Entry<ElementVersion, ElementVersion> e = entry.next();

			String whoIsThis = "Problem with " + e.getKey().getClass().getSimpleName() + ": ";
			if (e.getKey() instanceof DataPropertyVersion) {
				LOGGER.info(whoIsThis + ((DataPropertyVersion<?>) e.getKey()).getValue());
			} else if (e.getKey() instanceof ConceptPropertyVersion) {
				LOGGER.info(whoIsThis + ((ConceptPropertyVersion) e.getKey()).getBusinessKey());
			} else if (e.getKey() instanceof ConceptVersion) {
				LOGGER.info(whoIsThis + ((ConceptVersion) e.getKey()).getBusinessKey());
			}
		}
	}

	@Test
	public void testCategoryCreation() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess cr1 = provider.createChangeContext(contextId, null);

		IcdTabular newConceptCR1 = IcdTabular.create(cr1, "44", IcdTabular.CHAPTER);

		cr1.persist();
		cr1.realizeChangeContext(false);

	}

	@Ignore
	@Test
	public void testConflictWithConceptAndReBase() {

		String parentCode = "A06";
		Ref<IcdTabular> icdTabCR1 = ref(IcdTabular.class);
		Ref<IcdTabular> icdTabCR2 = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ContextAccess cr1 = provider.createChangeContext(contextId, null);
		ContextAccess cr2 = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = cr1.find(icdTabCR1, icdTabCR1.eq("code", parentCode));
		IcdTabular conceptCR1 = iteratorCR1.next();

		Iterator<IcdTabular> iteratorCR2 = cr2.find(icdTabCR2, icdTabCR2.eq("code", parentCode));
		IcdTabular conceptCR2 = iteratorCR2.next();

		conceptCR1.setUserDescription("ENG", "TEST CR" + new Date().getTime());
		conceptCR2.setUserDescription("ENG", "TEST CR " + new Date().getTime());

		cr1.persist();
		cr2.persist();

		cr1.realizeChangeContext(false);

		// Should fail, but from what..?
		HashMap<ElementVersion, ElementVersion> conflicts = cr2.realizeChangeContext(false);
		// printConflicts(conflicts);
		assertTrue(conflicts.size() > 0);

		// Get the class Id for this property.
		long classid = classService.getCachedClassId(cr2.getContextId().getBaseClassification(), "UserTitle");
		// Rebase the version so we can realize
		boolean isSuccessfullyRebased = operations.reBaseChangedFromVersionId(conceptCR2.getElementId(), cr2
				.getContextId().getContextId(), classid, "ENG");
		LOGGER.debug("Rebased? " + isSuccessfullyRebased);

		conflicts = cr2.realizeChangeContext(false);
		assertTrue(conflicts.size() == 0);

	}

	@Test
	public void testDiscard() {

		String testCode = "A01";
		String languageCode = "ENG";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		while (iterator.hasNext()) {
			IcdTabular concept = iterator.next();
			concept.setUserDescription(languageCode, "blah blah blah");
		}

		context.persist();

		List<ElementVersion> changes = context.retrieveChangesFromChangeContext();
		assertTrue(changes.size() == 1);

		for (ElementVersion ev : changes) {
			context.discardFromChangeContext(ev);
		}

		// context now requires a reload
		context = context.reload();

		assertTrue(context.realizeChangeContext(false).size() == 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDiscardAndRedoAgain() {

		String testCode = "A01";
		String languageCode = "ENG";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		IcdTabular concept = iterator.next();
		concept.setUserDescription(languageCode, "blah blah blah");
		context.persist();

		List<ElementVersion> changes = context.retrieveChangesFromChangeContext();
		assertTrue(changes.size() == 1);

		for (ElementVersion ev : changes) {
			context.discardFromChangeContext(ev);
		}

		// What happens if you do this?
		concept.setUserDescription(languageCode, "blah blah blah1");

		// Will throw a illegal exception, since reload was not called
		context.persist();

		context.realizeChangeContext(false);
	}

	@Test
	public void testDiscardAndRedoAgain1() {

		/*
		 * This test and discard works because you re-open the change context. Once re-opened, it wont pick up the old
		 * discarded version.
		 */
		String testCode = "A01";
		String languageCode = "ENG";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);
		String ccVersionCode = context.getContextId().getVersionCode();

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		IcdTabular concept = iterator.next();
		concept.setUserDescription(languageCode, "blah blah blah");
		context.persist();

		List<ElementVersion> changes = context.retrieveChangesFromChangeContext();
		assertTrue(changes.size() == 1);

		for (ElementVersion ev : changes) {
			context.discardFromChangeContext(ev);
		}

		// What happens if you do this?
		LOGGER.info("!!!!!!!!!!!!!!!!!!");

		// Re open CC
		contextId = finder.findIfAvail("ICD-10-CA", ccVersionCode);
		context = provider.findContext(contextId);

		iterator = context.find(icdTab, icdTab.eq("code", testCode));
		concept = iterator.next();

		concept.setUserDescription(languageCode, "blah blah blah");
		context.persist();

		context.realizeChangeContext(false);
	}

	@Test
	public void testDoesntKnowOtherCRGotRealized() {
		String parentCode = "A00";
		Ref<IcdTabular> icdTabCR1 = ref(IcdTabular.class);
		Ref<IcdTabular> icdTabCR2 = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ContextAccess cr1 = provider.createChangeContext(contextId, null);
		ContextAccess cr2 = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = cr1.find(icdTabCR1, icdTabCR1.eq("code", parentCode));
		IcdTabular conceptCR1 = iteratorCR1.next();
		conceptCR1.setUserDescription("ENG", "This should work NOW =)");

		Iterator<IcdTabular> iteratorCR2 = cr2.find(icdTabCR2, icdTabCR2.eq("code", parentCode));
		IcdTabular conceptCR2 = iteratorCR2.next();
		conceptCR2.setUserDescription("ENG", "THIS WILL NOT WORK =(");

		cr1.persist();
		cr2.persist();

		cr1.realizeChangeContext(false);

		// What happens here?
		// So inside realizeChangeContext, it'll load from the base context. Since this executes after CR1
		// got realized, it'll pick up the changes. The question is now, what if it gets realized at the EXACT same
		// time?
		HashMap<ElementVersion, ElementVersion> conflicts = cr2.realizeChangeContext(false);

		printConflicts(conflicts);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testErrorRealizingClosedChangeRequest() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		// Decide I dont want this
		context.closeChangeContext();

		// This is illegal, and will throw an exception
		context.realizeChangeContext(false);
	}

	// We allow concepts/properties to be realized if the value is the same as in the base context
	// In this test, we initially create duplicate concepts with different user description property.
	// Initially we will get a failure to realize. After discarding the conflicting property, we should
	// now be able to realize
	@Test
	public void testRealizationWithSamePropertyValue() {

		String parentCode = "A00";
		Ref<IcdTabular> icdTabCR1 = ref(IcdTabular.class);
		Ref<IcdTabular> icdTabCR2 = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ContextAccess cr1 = provider.createChangeContext(contextId, null);
		ContextAccess cr2 = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = cr1.find(icdTabCR1, icdTabCR1.eq("code", parentCode));
		IcdTabular conceptCR1 = iteratorCR1.next();

		Iterator<IcdTabular> iteratorCR2 = cr2.find(icdTabCR2, icdTabCR2.eq("code", parentCode));
		IcdTabular conceptCR2 = iteratorCR2.next();

		IcdTabular newConceptCR1 = IcdTabular.create(cr1, "A00.23", IcdTabular.CATEGORY);
		IcdTabular newConceptCR2 = IcdTabular.create(cr2, "A00.23", IcdTabular.CATEGORY);

		newConceptCR1.setParent(conceptCR1);
		newConceptCR2.setParent(conceptCR2);

		newConceptCR1.setUserDescription("ENG", "TEST CR1");
		newConceptCR2.setUserDescription("ENG", "TEST CR2");

		cr1.persist();
		cr2.persist();

		cr1.realizeChangeContext(false);

		// Conflict will be detected due to the user description being different
		HashMap<ElementVersion, ElementVersion> conflicts = cr2.realizeChangeContext(false);
		printConflicts(conflicts);

		if (conflicts.size() > 0) {
			// Discard the user description change
			discardConflicts(conflicts, cr2);

			// context now requires a reload
			cr2 = cr2.reload();

			// This will pass now
			conflicts = cr2.realizeChangeContext(false);
			printConflicts(conflicts);
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRealizeANonChangeRequest() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		// Will not allow
		context.realizeChangeContext(false);
	}

	@Test
	public void testRealizeChangeRequest() {
		// There are 3 transactions going on here
		String parentCode = "A00";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", parentCode));
		IcdTabular concept = iterator.next();

		concept.setUserDescription("ENG", "A00 new description!!!!!!!!");

		context.persist();

		context.realizeChangeContext(false);
	}

	@Test
	public void testTextUpdate() {

		String testCode = "A01";
		String languageCode = "ENG";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		while (iterator.hasNext()) {
			IcdTabular concept = iterator.next();
			concept.setUserDescription(languageCode, "blah blah blah");
		}

		context.persist();
		context.realizeChangeContext(false);
	}

	@Test
	public void testUnableToRealizeDueToConflict() {
		String parentCode = "A00";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ContextAccess context = provider.createChangeContext(contextId, null);
		ContextAccess contextOther = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);

		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", parentCode));
		IcdTabular concept = iterator.next();
		concept.setUserDescription("ENG", "This should work NOW");

		iterator = contextOther.find(icdTab, icdTab.eq("code", parentCode));
		concept = iterator.next();
		concept.setUserDescription("ENG", "THIS WILL NOT WORK");

		context.persist();
		contextOther.persist();

		context.realizeChangeContext(false);

		// After some time, this guy reopens the CR, and decides to realize.
		// Should get the updated value

		// Fails here, with the real conflict stuff returned in a map
		// Is it a problem no exception is thrown?
		contextOther.realizeChangeContext(false);

	}

}
