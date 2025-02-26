package ca.cihi.cims.content.icd;

import static ca.cihi.cims.bll.query.FindCriteria.eq;
import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

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
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.SexValidation;
import ca.cihi.cims.content.shared.SupplementType;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class DomainValuesTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void testDaggerAsterisks() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<DaggerAsterisk> iIndex = ref(DaggerAsterisk.class);

		Iterator<DaggerAsterisk> iterator = context.find(iIndex, eq(iIndex, "code", "*"));

		while (iterator.hasNext()) {
			DaggerAsterisk da = iterator.next();
			assertTrue(da.getCode().equals("*"));
		}
	}

	@Test
	public void testDaggerAsterisksFindAll() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<DaggerAsterisk> iterator = context.findAll(DaggerAsterisk.class);
		while (iterator.hasNext()) {
			DaggerAsterisk da = iterator.next();
			LOGGER.info(da.getCode());
		}
	}

	@Test
	public void testDaggerAsterisksFindList() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<DaggerAsterisk> da = ref(DaggerAsterisk.class);

		List<DaggerAsterisk> daList = context.findList(da);

		for (DaggerAsterisk dag : daList) {
			LOGGER.info(dag.getCode());
		}
	}

	@Test
	public void testFacilityType() {
		int icdFacilityType = 2; /* 5 values for ICD */
		int cciFacilityType = 2; /* 5 values for ICD */
		int counter = 0;

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<FacilityType> iterator = context.findAll(FacilityType.class);

		while (iterator.hasNext()) {
			iterator.next();
			counter++;
		}

		assertTrue(counter >= icdFacilityType);
		counter = 0;

		contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		context = provider.findContext(contextId);

		iterator = context.findAll(FacilityType.class);

		while (iterator.hasNext()) {
			iterator.next();
			counter++;
		}

		assertTrue(counter >= cciFacilityType);
	}

	@Test
	public void testSexValidation() {
		int icdSV = 3; /* 3 values for ICD */
		int cciSV = 3; /* 3 values for ICD */
		int counter = 0;

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<SexValidation> iterator = context.findAll(SexValidation.class);

		while (iterator.hasNext()) {
			iterator.next();
			counter++;
		}

		assertTrue(counter >= icdSV);
		counter = 0;

		contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		context = provider.findContext(contextId);

		iterator = context.findAll(SexValidation.class);

		while (iterator.hasNext()) {
			iterator.next();
			counter++;
		}

		assertTrue(counter >= cciSV);
	}

	@Test
	public void testSupplementTypeFindAll() {
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<SupplementType> iterator = context.findAll(SupplementType.class);
		while (iterator.hasNext()) {
			SupplementType supp = iterator.next();
			LOGGER.info(supp.getCode() + " " + supp.getDescription("ENG"));
		}
	}
}
