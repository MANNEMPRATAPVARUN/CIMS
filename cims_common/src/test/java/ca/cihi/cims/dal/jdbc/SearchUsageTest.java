package ca.cihi.cims.dal.jdbc;

import static ca.cihi.cims.bll.query.FindCriteria.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.commons.collections.IteratorUtils;
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
import ca.cihi.cims.bll.hg.ElementQueryAssembler;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.FieldEq;
import ca.cihi.cims.dal.query.Restriction;
import ca.cihi.cims.hg.mapper.config.MappingConfig;

/*
 * Not a true unit test class, but more just provides usage examples
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class SearchUsageTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	ClassService classService;

	@Autowired
	MappingConfig mappingConfig;

	@Autowired
	private ORConfig orConfig;

	@Autowired
	ContextRestrictionQueryBuilder queryBuilder;

	@Autowired
	ElementOperations elementOperations;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void testFindMeTheParent() {

		String testCode = "A00";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<IcdTabular> parent = ref(IcdTabular.class);
		Ref<IcdTabular> child = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(parent, child.eq("code", testCode), child.link("parent", parent));

		while (iterator.hasNext()) {
			IcdTabular concept = iterator.next();
			LOGGER.debug("Code: " + concept.getCode());
		}
	}

	@Test
	public void testGetRestrictions() {

		String testCode = "A00";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		Ref<IcdTabular> parent = ref(IcdTabular.class);
		Ref<IcdTabular> child = ref(IcdTabular.class);

		ArrayList<FindCriterion> criteria = new ArrayList<FindCriterion>();
		criteria.add(child.eq("code", testCode));
		criteria.add(child.link("parent", parent));

		// The find method is commented below
		// private <T> Iterator<T> find(Ref<T> wrapper, Collection<FindCriterion> criteria) {
		//
		// ElementQueryAssembler query = new ElementQueryAssembler(wrapper, criteria, mappingConfig);
		//
		// Iterator<Long> find = elementOperations.find(contextId, query.getTargetElement(), query.getRestrictions());
		//
		// return prefetchingIterator(wrapper.getWrapperClass(), find);
		// }

		ElementQueryAssembler assembler = new ElementQueryAssembler(parent, criteria, mappingConfig);

		List<Restriction> restrictions = assembler.getRestrictions();

		// System.err.println("Restrictions");
		// System.err.println("---------------------------------------------------------------------------------------");
		// for (Restriction r : restrictions) {
		// System.err.println(r);
		// }

		ParamNamer namer = new ParamNamer();
		// ElementQueryBuilder elementQueryBuilder = new ElementQueryBuilder(contextId, orConfig, queryBuilder, namer);
		NestedElementQueryBuilder elementQueryBuilder = new NestedElementQueryBuilder(contextId, orConfig,
				queryBuilder, namer);

		SelectBits query = elementQueryBuilder.buildElementIdQuery(assembler.getTargetElement(), restrictions);
		// Explore more of the line above

		// for (Restriction restriction : restrictions) {
		//
		// if (restriction instanceof TransitiveLink) {
		// //addTransitiveLink((TransitiveLink) restriction);
		// } else {
		// //query.addWhere(buildWhere(restriction));
		// if (restriction instanceof OrRestriction) {
		//
		// OrRestriction or = (OrRestriction) restriction;
		//
		// Collection<String> wheres = new ArrayList<String>();
		// for (Restriction r : or.getSubRestrictions()) {
		// wheres.add(buildWhere(r));
		// }
		//
		// System.err.println( "( ( " + StringUtils.join(wheres, " ) OR ( ") + " ) )");
		// } else if (restriction instanceof MightBeA) {
		// MightBeA might = (MightBeA) restriction;
		//
		// orConfig.getMapping((might.getElement().getElementClass())).getIdColumn();
		//
		// getMapping(might.getTargetElement()).getIdColumn();
		//
		// // Use the A.id = B.id(+) syntax to indicate: A left outer join B on
		// // A.id=B.id
		//
		// return idReference(might.getElement()) + " = " + idReference(might.getTargetElement()) + "(+)";
		//
		// }
		//
		//
		//
		//
		// }
		// }
		//

		// Now run the query!
		LOGGER.debug(new SqlFormatter().format(query.toString(), namer.getParamMap()));

		// List<Long> elementIds = jdbcNamed.queryForList(query.toString(), namer.getParamMap(), Long.class);

		// @Override
		// public Iterator<Long> find(ContextIdentifier context, ElementMatch element, Collection<Restriction>
		// restrictions) {
		//
		// ParamNamer namer = new ParamNamer();
		//
		// ElementQueryBuilder elementQueryBuilder = new ElementQueryBuilder(context, mappingConfig, queryBuilder,
		// namer);
		//
		// SelectBits query = elementQueryBuilder.buildElementIdQuery(element, restrictions);
		//
		// // Now run the query!
		// LOGGER.debug(new SqlFormatter().format(query.toString(), namer.getParamMap()));
		// List<Long> elementIds = jdbcNamed.queryForList(query.toString(), namer.getParamMap(), Long.class);
		//
		// return Collections.unmodifiableList(elementIds).iterator();
		// }

	}

	@Test
	@Ignore
	public void testLoadConceptById() {

		Long elementId = 1746L; // A05.8

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		List<Restriction> r = new ArrayList<Restriction>();

		ElementRef concept = new ElementRef(ConceptVersion.class);

		r.add(new FieldEq(concept, "elementId", elementId));

		Iterator<Long> iterator = elementOperations.find(contextId, concept, r);

		LOGGER.debug(iterator.next());
	}

	@Test
	public void testMyDomainCPVs() {

		Long elementId = 1746L;
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ElementRef domain = new ElementRef(ConceptPropertyVersion.class);

		List<Restriction> r = new ArrayList<Restriction>();
		r.add(new FieldEq(domain, "domainElementId", elementId));

		Iterator<Long> iterator = elementOperations.find(contextId, domain, r);

		Long[] elementIds = (Long[]) IteratorUtils.toArray(iterator, Long.class);

		List<ElementVersion> list = elementOperations.loadElements(contextId, Arrays.asList(elementIds));

		for (ElementVersion ev : list) {
			LOGGER.debug(ev.getBusinessKey());
		}

	}

	@Test
	public void testMyRangeCPVs() {

		Long elementId = 1746L;
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		ElementRef range = new ElementRef(ConceptPropertyVersion.class);

		List<Restriction> r = new ArrayList<Restriction>();
		r.add(new FieldEq(range, "rangeElementId", elementId));

		Iterator<Long> iterator = elementOperations.find(contextId, range, r);

		Long[] elementIds = (Long[]) IteratorUtils.toArray(iterator, Long.class);

		List<ElementVersion> list = elementOperations.loadElements(contextId, Arrays.asList(elementIds));

		for (ElementVersion ev : list) {
			LOGGER.debug(ev.getBusinessKey());
		}
	}

	// //

	@Test
	public void testSimpleConceptLoad() {

		Long elementId = 1746L;
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		List<Restriction> r = new ArrayList<Restriction>();
		ElementRef concept = new ElementRef(ConceptVersion.class);
		r.add(new FieldEq(concept, "elementId", elementId));

		List<ElementVersion> list = elementOperations.loadElements(contextId, Arrays.asList(elementId));

		for (ElementVersion ev : list) {
			LOGGER.debug(ev.getBusinessKey());
		}
	}

	@Test
	public void testSimpleConceptLoad1() {

		Long elementId = 1746L;
		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);

		List<Restriction> r = new ArrayList<Restriction>();
		ElementRef concept = new ElementRef(ConceptVersion.class);
		r.add(new FieldEq(concept, "elementId", elementId));

		Iterator<Long> iterator = elementOperations.find(contextId, concept, r);

		while (iterator.hasNext()) {
			ElementVersion ev = elementOperations.loadElement(contextId, iterator.next());
			LOGGER.debug(ev.getBusinessKey());
		}
	}

}
