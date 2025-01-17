package ca.cihi.cims.content.icd.index;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class IcdIndexAlphabeticalTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void testIndex() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);

		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq("description", "Ablatio, ablation"));

		while (iterator.hasNext()) {

			IcdIndexAlphabetical index = iterator.next();

			LOGGER.debug("*****************************************************");
			LOGGER.debug("Index Description: " + index.getDescription());
			LOGGER.debug("Index element ID: " + index.getElementId());
			LOGGER.debug("Index Note Description: " + index.getNoteDescription(Index.LANGUAGE_ENG));

			SortedSet<IcdIndexAlphabetical> indexChildrenSorted = index.getSortedChildren();
			for (IcdIndexAlphabetical childIndex : indexChildrenSorted) {
				LOGGER.debug("Child index term description: " + childIndex.getElementId() + " "
						+ childIndex.getDescription());
			}

			Collection<Index> desendentIndexList = index.descendantIndices();
			for (Index dIndex : desendentIndexList) {
				LOGGER.debug("desendent index term description: " + dIndex.getElementId() + " "
						+ dIndex.getDescription());
			}

			LOGGER.debug("*****************************************************");

			BookIndex bi = index.getContainingBook();
			LOGGER.debug(bi.getCode(Index.LANGUAGE_ENG));
			LOGGER.debug(bi.getDescription());

			LOGGER.debug("*****************************************************");

			Index letterIndex = index.getContainingPage();
			LOGGER.debug(letterIndex.getDescription());

			LOGGER.debug("*********************************************************");

			Collection<Index> letters = bi.getSortedChildren();
			for (Index letter : letters) {
				LOGGER.debug("Letters [" + letter.getDescription() + "]");
			}

			assertTrue(true);
		}

	}

	@Test
	@Ignore
	public void testIndexLoadByElementId() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		LetterIndex obj = context.load(862715);
		LOGGER.debug(obj.getClass());

	}

	@Test
	public void testIndexXML() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);

		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq("description", "Ablatio, ablation"));

		while (iterator.hasNext()) {

			IcdIndexAlphabetical index = iterator.next();

			LOGGER.debug("*****************************************************");
			LOGGER.debug("Index Description: " + index.getDescription());
			LOGGER.debug("Index element ID: " + index.getElementId());
			LOGGER.debug("Index Note Description: " + index.getNoteDescription(Index.LANGUAGE_ENG));
			LOGGER.debug("Index XML: " + index.getIndexRefDefinition(Index.LANGUAGE_ENG));
			LOGGER.debug("*****************************************************");
		}

	}

	@Test
	public void testNestingLevel() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);

		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex,
				iIndex.eq("description", "anesthesia, anesthetic NEC"));

		// Anesthesia, anesthetic (see also Effect, adverse, anesthesia) R20.0

		IcdIndexAlphabetical index = iterator.next();
		LOGGER.info("Nesting Level: " + index.getNestingLevel());

		index = (IcdIndexAlphabetical) index.getParent();
		LOGGER.info("Parent: " + index.getDescription());
		LOGGER.info("Nesting Level: " + index.getNestingLevel());

	}

}
