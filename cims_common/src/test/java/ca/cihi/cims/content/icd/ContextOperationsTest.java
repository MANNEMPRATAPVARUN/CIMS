package ca.cihi.cims.content.icd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

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
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class ContextOperationsTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	ClassService classService;

	@Test
	public void testChangeRequestContextCreation() {
		String baseClassification = "ICD-10-CA";
		String version = CIMSTestConstants.TEST_VERSION;

		ContextIdentifier contextId = finder.findIfAvail(baseClassification, version);
		if (contextId == null) {
			fail("Base context not found " + version);
		}

		ContextAccess ca = provider.createChangeContext(contextId, null);

		assertNotNull(ca);
		assertTrue(contextId.isVersionYear() == ca.getContextId().isVersionYear());

		Collection<String> versions = provider.findVersionCodes(baseClassification);
		assertTrue(versions.contains(ca.getContextId().getVersionCode()));

		contextId = finder.findIfAvail(baseClassification, ca.getContextId().getVersionCode());
		if (contextId == null) {
			fail("Change request context not found " + version);
		}

	}

	@Test
	@Ignore
	public void testContextCreation() {
		// Test depends on data
		String baseClassification = "ICD-10-CA";
		String version = CIMSTestConstants.TEST_VERSION;

		ContextIdentifier contextId = finder.findIfAvail(baseClassification, version);

		provider.createContext(contextId, false);
	}

	@Test
	public void testLoadContextById() {

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		long cID = contextId.getContextId();

		contextId = finder.findIfAvail("ICD-10-CA", cID);

		ContextAccess context = provider.findContext(contextId);

		assertTrue(cID == contextId.getContextId());
	}

}
