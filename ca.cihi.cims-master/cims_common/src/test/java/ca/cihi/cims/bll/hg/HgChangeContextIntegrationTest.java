package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.*;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Ignore;
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
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.ContextIdentifier;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class HgChangeContextIntegrationTest {

	@Autowired
	private ContextProvider provider;

	private final Logger LOGGER = LogManager.getLogger(HgChangeContextIntegrationTest.class);

	@Test
	public void changePropertyTwice() {
		ContextDefinition def = ContextDefinition.forVersion("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess base2015context = provider.findContext(def);

		ContextAccess changeContext = createChangeContext(base2015context);

		// Load a concept from the change context
		IcdTabular newChild = IcdTabular.create(changeContext, "A0000", IcdTabular.CATEGORY);

		newChild.setPresentationHtml("ENG", "fake");
		Assert.assertEquals("fake", newChild.getPresentationHtml("ENG"));
		newChild.setPresentationHtml("ENG", "fake");

		changeContext.persist();
	}

	private ContextAccess createChangeContext(ContextAccess base2015context) {
		ContextIdentifier baseId = base2015context.getContextId();
		return provider.createChangeContext(baseId, null);
	}

	private IcdTabular loadByCode(ContextAccess contextAccess, String code) {
		LOGGER.info("Trying to load " + code + " from " + contextAccess.getContextId());
		Ref<IcdTabular> icd = ref(IcdTabular.class);
		IcdTabular findOne = contextAccess.findOne(icd, icd.eq("code", code));
		return findOne;
	}

	/**
	 * This "test" is not a test, just a utility for making a bunch of changes to a base year.
	 */
	@Test
	@Ignore
	public void makeABigChangeContext() {
		ContextDefinition def = ContextDefinition.forVersion("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess base2015context = provider.findContext(def);

		// ContextIdentifier originalId = base2015context.getContextId();
		ContextAccess changeContext = createChangeContext(base2015context);

		Ref<IcdTabular> icd = ref(IcdTabular.class);

		Iterator<IcdTabular> iterator = changeContext.find(icd, icd.like("shortDescription", "%disease%"));

		int max = 1000;
		int current = 0;
		while (iterator.hasNext() && current++ < max) {
			IcdTabular next = iterator.next();
			String oldDesc = next.getShortDescription("ENG");
			if (oldDesc != null) {
				String newDesc = oldDesc.replaceAll("isease", "iddles");
				System.err.println("Changing " + next.getCode() + ": " + oldDesc + " to " + newDesc);
				next.setShortDescription("ENG", newDesc);
			}
		}
		changeContext.persist();
	}

	private ContextAccess reload(ContextAccess context) {
		return provider.findContext(context.getContextId());
	}

	@Test
	public void testDbUnchangedWithoutSaving() {
		ContextDefinition def = ContextDefinition.forVersion("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess base2015context = provider.findContext(def);

		ContextAccess changeContext = createChangeContext(base2015context);

		String code = "A00";

		// Load a concept from the change context
		IcdTabular a00 = loadByCode(changeContext, code);

		String originalDesc = a00.getShortDescription("ENG");

		// Modify it's description, BUT DO NOT SAVE
		a00.setShortDescription("ENG", "FAKE VALUE");

		// Now reload the change context
		changeContext = reload(changeContext);
		a00 = loadByCode(changeContext, code);

		assertEquals("The unsaved change must not have modified the database.", originalDesc,
				a00.getShortDescription("ENG"));
	}

	@Test
	public void testSavedChangesArePersisted() throws Exception {

		ContextDefinition def = ContextDefinition.forVersion("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess base2015context = provider.findContext(def);

		String code = "A00";

		IcdTabular originalA00 = loadByCode(base2015context, code);
		assertNotNull("The example concept should exist in the base context.", originalA00);
		originalA00.setPresentationHtml("ENG", "fake original value");
		base2015context.persist();

		// ContextIdentifier originalId = base2015context.getContextId();

		LOGGER.info("Creating a change context.");
		ContextAccess changeContext = createChangeContext(base2015context);

		LOGGER.info("Created : " + changeContext.getContextId());

		// Now, look up a concept in the change context
		IcdTabular changeA00 = loadByCode(changeContext, code);
		assertNotNull("The example code should also virtually exist in the new change context.", changeA00);

		String oldValue = changeA00.getPresentationHtml("ENG");
		String newValue = "" + System.currentTimeMillis();

		changeA00.setPresentationHtml("ENG", newValue);
		changeContext.persist();

		changeContext = reload(changeContext);
		IcdTabular reloadedChangedConcept = loadByCode(changeContext, code);

		assertEquals(newValue, reloadedChangedConcept.getPresentationHtml("ENG"));

		LOGGER.info("Reloading base context.");
		base2015context = reload(base2015context);
		LOGGER.info("Loading " + code + " from base context.");
		IcdTabular reloadedBaseConcept = loadByCode(base2015context, code);

		System.err.println("Old value: " + oldValue);
		System.err.println("New value: " + newValue);
		assertEquals(oldValue, reloadedBaseConcept.getPresentationHtml("ENG"));
	}
}
