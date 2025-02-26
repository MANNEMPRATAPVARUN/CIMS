package ca.cihi.cims.content.cci;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class CciTabularConceptCreationTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	ContextFinder finder;

	@Autowired
	NonContextOperations nco;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test(expected = DuplicateConceptException.class)
	public void testCreateExistingConcept() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess cr1 = provider.createChangeContext(contextId, null);

		Ref<CciGroupComponent> groupRef = ref(CciGroupComponent.class);
		Iterator<CciGroupComponent> groupIterator = cr1.find(groupRef, groupRef.eq("code", "GB"));
		CciGroupComponent group = groupIterator.next();

		CciTabular newConceptCR1 = CciTabular.createGroup(cr1, "1", group);
		newConceptCR1.setUserDescription("ENG", "This wont work");

		cr1.persist();

		cr1.realizeChangeContext(false);
	}

	@Ignore
	@Test
	public void testCreateGroupAndCreateToNewConcept() {

		String parentCode = "1.GB.^^.^^";
		Ref<CciTabular> tabular = ref(CciTabular.class);
		Ref<CciTabular> sectionRef = ref(CciTabular.class);

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess cr1 = provider.createChangeContext(contextId, null);

		Iterator<CciTabular> iteratorCR1 = cr1.find(tabular, tabular.eq("code", parentCode));
		CciTabular conceptCR1 = iteratorCR1.next();

		Iterator<CciTabular> sectionIterator = cr1.find(sectionRef, sectionRef.eq("typeCode", CciTabular.SECTION),
				sectionRef.eq("code", "1"));

		CciTabular section = sectionIterator.next();
		LOGGER.info(section.getCode());

		CciGroupComponent group = CciGroupComponent.create(cr1, "Z2", section);

		try {
			CciTabular newConceptCR1 = CciTabular.createGroup(cr1, "1", group);

			newConceptCR1.setParent(conceptCR1);

			newConceptCR1.setUserDescription("ENG", "heh, this works");

			cr1.persist();

			cr1.realizeChangeContext(false);
		} catch (DuplicateConceptException dce) {
			LOGGER.warn("Concept already exists...");
		}
	}
}
