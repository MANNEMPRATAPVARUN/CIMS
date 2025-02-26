package ca.cihi.cims.dal.jdbc;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.DuplicateConceptException;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.NonContextOperations;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( { "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class JdbcNonContextOperationsTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	ContextFinder finder;

	@Autowired
	NonContextOperations nco;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	@Ignore
	public void testCreatePreviouslyDeletedConcept() {

		String parentCode = "A02";
		Ref<IcdTabular> icdTabCR1 = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess cr1 = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = cr1.find(icdTabCR1, icdTabCR1.eq("code", parentCode));
		IcdTabular conceptCR1 = iteratorCR1.next();

		try {
			IcdTabular newConceptCR1 = IcdTabular.create(cr1, "A02.0", IcdTabular.CATEGORY);

			newConceptCR1.setParent(conceptCR1);

			newConceptCR1.setUserDescription("ENG", "whew, im back!");

			cr1.persist();

			cr1.realizeChangeContext(false);
		} catch (DuplicateConceptException dce) {
			LOGGER.warn("Concept already exists...");
		}
	}

	@Test
	@Ignore
	public void testCreateThenDelete() {

		String parentCode = "A02";
		String conceptToCreate = "A02.99";
		Ref<IcdTabular> icdTabCR1 = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess cr1 = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = cr1.find(icdTabCR1, icdTabCR1.eq("code", parentCode));
		IcdTabular conceptCR1 = iteratorCR1.next();

		try {
			IcdTabular newConceptCR1 = IcdTabular.create(cr1, conceptToCreate, IcdTabular.CATEGORY);

			newConceptCR1.setParent(conceptCR1);

			newConceptCR1.setUserDescription("ENG", "whew, im back!");

			cr1.persist();

			cr1.realizeChangeContext(false);
		} catch (DuplicateConceptException dce) {
			fail("Shouldnt happen, last we checked this concept didnt exist!");
		}

		// Delete this new concept now
		Ref<IcdTabular> icdT = ref(IcdTabular.class);

		contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		iteratorCR1 = context.find(icdT, icdT.eq("code", conceptToCreate));

		Long elementId = iteratorCR1.next().getElementId();

		nco.remove(context.getContextId(), elementId);

		context.realizeChangeContext(false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteReferencedConceptFailure() {
		String code = "A02.0"; // this code cant be deleted, as it is referenced elsewhere

		Ref<IcdTabular> icdT = ref(IcdTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Iterator<IcdTabular> iteratorCR1 = context.find(icdT, icdT.eq("code", code));

		Long elementId = iteratorCR1.next().getElementId();

		nco.remove(context.getContextId(), elementId);

		context.realizeChangeContext(false);
	}

	@Test
	public void testDetermineClassNameByElementId() {
		// Test dependant on data
		long elementId = 2800585L;
		String className = nco.determineClassNameByElementId(elementId);

		LOGGER.info(className);
	}

	@Test
	public void testDetermineVersionCodeByElementId() {
		// Test dependant on data
		long elementId = 2800585L;
		String className = nco.determineVersionCodeByElementId(elementId);

		LOGGER.info(className);

	}
}
