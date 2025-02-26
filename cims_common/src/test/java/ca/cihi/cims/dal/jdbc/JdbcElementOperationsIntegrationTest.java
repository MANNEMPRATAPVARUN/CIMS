package ca.cihi.cims.dal.jdbc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
import ca.cihi.cims.bll.hg.SetValuedMap;
import ca.cihi.cims.dal.BooleanPropertyVersion;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.NumericPropertyVersion;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class JdbcElementOperationsIntegrationTest {

	@Autowired
	private ElementOperations elementOperations;

	@Autowired
	private JdbcTemplate template;

	@Autowired
	private ContextFinder contextFinder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Autowired
	private ORConfig orConfig;

	private boolean isInverseProperty(long conceptId, PropertyVersion prop) {
		boolean inverseProperty = false;
		if (prop instanceof ConceptPropertyVersion) {
			if (((ConceptPropertyVersion) prop).getRangeElementId() == conceptId) {
				inverseProperty = true;
			}
		}
		return inverseProperty;
	}

	@Test
	public void outputColumnMappings() {
		ClassORMapping f = orConfig.getMapping(NumericPropertyVersion.class);

		for (ColumnMapping col : f.getColumnMappings()) {
			LOGGER.debug(col);
		}
	}

	@Test
	public void testElementLoading() throws Exception {

		// Find a suitable element in the database to load
		ContextIdentifier testContext = contextFinder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		String sql = "select elementid from (select e.elementid from elementversion ev, element e, class c "
				+ "where ev.elementid = e.elementid and e.classid=c.classid and c.tablename='XMLPropertyVersion' "
				+ "and ev.elementversionid in (select elementversionid from StructureElementVersion where structureId=?)) "
				+ "where rownum=1";

		LOGGER.debug(sql + " " + testContext.getContextId());

		long anyOldElementId = template.queryForObject(sql, new Object[] {testContext.getContextId()}, Long.class);

		ElementVersion element = elementOperations.loadElement(testContext, anyOldElementId);

		Assert.assertNotNull(element);

		// Make sure all the properties are set
		assertNotNull("Class name must be set.", element.getClassName());
		assertNotNull("Class id must be set.", element.getClassId());
		assertNotNull("Version code must be set.", element.getVersionCode());
		assertNotNull("Version timestamp must be set.", element.getVersionTimeStamp());
		// Todo, this probably works but most of my elements don't have unique
		// ids, at least until migration is updated.
		// assertNotNull("Business key must be set.", element.getBusinessKey());
	}
	
	@Test
	public void testLoadElements1(){
		ContextIdentifier testContext = contextFinder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
	    List<Long> eList = new ArrayList<Long>();
	    for (int i = 1; i <= 3006; i++) {
	    	eList.add(new Long(i));
	    }

	    Collection<Long> elementIdsCollection = new HashSet<Long>(eList);
	    List<ElementVersion> eVersionList = elementOperations.loadElements(testContext, elementIdsCollection);
	    
	    assertTrue(eVersionList.size()<=3006);
	}

	@Test
	public void testLoadElements2(){
		ContextIdentifier testContext = contextFinder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
	    List<Long> eList = new ArrayList<Long>();
	    for (int i = 1; i <= 1000; i++) {
	    	eList.add(new Long(i));
	    }

	    Collection<Long> elementIdsCollection = new HashSet<Long>(eList);
	    List<ElementVersion> eVersionList = elementOperations.loadElements(testContext, elementIdsCollection);
	    
	    assertTrue(eVersionList.size()<=1000);
	}

	
	@Test
	public void testPropertyLoading() throws Exception {
		ContextIdentifier testContext = contextFinder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		if (testContext == null) {
			return;
		}

		// Find a suitable element in the database to load
		long conceptId = template
				.queryForObject(
						"select elementid from (select e.elementid from elementversion ev, element e, class c "
								+ "where ev.elementid=e.elementid and e.classid=c.classid and c.classname='Category' "
								+ "and ev.elementversionid in (select elementversionid from StructureElementVersion where structureId=?)) "
								+ " where rownum=1", new Object[] {testContext.getContextId()}, Long.class);

		LOGGER.debug("Loading properties for concept w/ element ID " + conceptId);
		// What tables do we care about?
		SetValuedMap<Class, String> classNames = new SetValuedMap<Class, String>();

		// classNames.put(TextPropertyVersion.class, "Code");
		// classNames.put(TextPropertyVersion.class, "ShortTitle");
		// classNames.put(TextPropertyVersion.class, "LongTitle");
		// classNames.put(TextPropertyVersion.class, "UserTitle");
		// classNames.put(ConceptPropertyVersion.class, "Narrower");
		// classNames.put(BooleanPropertyVersion.class,
		// "CaEnhancementIndicator");
		// classNames.put(BooleanPropertyVersion.class, "ValidCodeIndicator");
		// classNames.put(BooleanPropertyVersion.class,
		// "RenderChildrenAsTableIndicator");

		classNames.put(ConceptPropertyVersion.class, null);
		classNames.put(BooleanPropertyVersion.class, null);
		classNames.put(NumericPropertyVersion.class, null);
		classNames.put(HtmlPropertyVersion.class, null);
		classNames.put(TextPropertyVersion.class, null);
		classNames.put(XmlPropertyVersion.class, null);

		List<String> empty = Collections.emptyList();

		List<PropertyVersion> properties = elementOperations.loadProperties(testContext, Arrays.asList(conceptId),
				classNames, empty, empty);

		// Assert.assertFalse(properties.isEmpty());
		for (PropertyVersion prop : properties) {
			if (prop != null) {

				boolean regularProperty = prop.getDomainElementId() == conceptId;

				boolean inverse = isInverseProperty(conceptId, prop);

				if (!(regularProperty || inverse)) {
					LOGGER.debug("Property loaded in error for concept " + conceptId + ": " + prop);
				}

				Assert.assertTrue("The property must either be regular or an inverse property of the concept.",
						regularProperty || inverse);

				LOGGER.debug(prop);
			}
		}
	}	
}
