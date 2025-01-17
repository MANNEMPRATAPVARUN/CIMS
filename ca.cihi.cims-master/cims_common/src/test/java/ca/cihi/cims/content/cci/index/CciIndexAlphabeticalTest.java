package ca.cihi.cims.content.cci.index;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

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
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class CciIndexAlphabeticalTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void testIndex() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciIndexAlphabetical> iIndex = ref(CciIndexAlphabetical.class);

		Iterator<CciIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq("description", "Abdominocentesis"));

		while (iterator.hasNext()) {

			CciIndexAlphabetical index = iterator.next();

			LOGGER.debug("*****************************************************");
			LOGGER.debug("Index Description: " + index.getDescription());
			LOGGER.debug("Index element ID: " + index.getElementId());
			LOGGER.debug("Index Note Description: " + index.getNoteDescription("ENG"));

			// Removed getting children, descendant indexes and books/letter as similar tests are covered
			// in other index tests
		}
	}

}
