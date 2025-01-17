package ca.cihi.cims.dal.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ConceptMatchTest {

	@Autowired
	private ElementOperations elementOperations;

	@Autowired
	private ContextFinder contextFinder;

	// @Autowired
	// private ContextProvider provider;

	@Test
	public void buildConceptMatches() {

		// Find me active categories with the code of A00 that have parents of
		// class Block

		List<Restriction> r = new ArrayList<Restriction>();

		ElementRef cat = new ElementRef(ConceptVersion.class);
		r.add(new ClassIn(cat, "Category"));
		r.add(new FieldEq(cat, "status", "ACTIVE"));

		ElementRef code = new ElementRef(TextPropertyVersion.class);
		r.add(new ClassIn(code, "Code"));
		r.add(new FieldEq(code, "status", "ACTIVE"));
		r.add(new FieldEq(code, "value", "A00"));
		r.add(new PointsToElement(code, "domainElementId", cat));

		ElementRef parent = new ElementRef(ConceptVersion.class);
		r.add(new ClassIn(parent, "Block"));
		r.add(new FieldEq(parent, "status", "ACTIVE"));

		ElementRef narrower = new ElementRef(ConceptPropertyVersion.class);
		r.add(new PointsToElement(narrower, "domainElementId", cat));
		r.add(new PointsToElement(narrower, "rangeElementId", parent));
		r.add(new FieldEq(narrower, "status", "ACTIVE"));

		Iterator<Long> iterator = find("ICD-10-CA", CIMSTestConstants.TEST_VERSION, cat, r);

		dumpValues(iterator, 1000);
	}

	@Test
	public void deadSimpleTransitive() {
		List<Restriction> r = new ArrayList<Restriction>();

		ElementRef aConcept = new ElementRef(ConceptVersion.class);
		aConcept.setName("StartConcept");
		r.add(new FieldEq(aConcept, "elementId", 1531791L));

		ElementRef parent = new ElementRef(ConceptVersion.class);
		parent.setName("Parentf");

		r.add(new TransitiveLink(aConcept, "Narrower", false, parent));

		Iterator<Long> it = find("ICD-10-CA", CIMSTestConstants.TEST_VERSION, parent, r);

		dumpValues(it, 1000);
	}

	private void dumpValues(Iterator<Long> iterator, int max) {
		int count = 0;

		while (count++ < max && iterator.hasNext()) {
			System.err.print(iterator.next() + ",");
		}
	}

	private Iterator<Long> find(String baseClassification, String versionCode, ElementRef element,
			List<Restriction> restrictions) {
		ContextIdentifier id = contextFinder.findIfAvail(baseClassification, versionCode);

		Iterator<Long> iterator = elementOperations.find(id, element, restrictions);

		return iterator;
	}

	// 1531779

	@Test
	public void findAConcept() {

		// Find all the properties of an element.
		List<Restriction> r = new ArrayList<Restriction>();

		ElementRef cat = new ElementRef(ConceptVersion.class);
		r.add(new ClassIn(cat, "Category"));
		r.add(new FieldEq(cat, "status", "ACTIVE"));

		ElementRef code = new ElementRef(TextPropertyVersion.class);
		r.add(new ClassIn(code, "Code"));
		r.add(new FieldEq(code, "status", "ACTIVE"));
		r.add(new FieldEq(code, "value", "A00"));
		r.add(new PointsToElement(code, "domainElementId", cat));

		Iterator<Long> it = find("ICD-10-CA", CIMSTestConstants.TEST_VERSION, cat, r);

		Assert.assertTrue("A00 must exist in ICD-10-CA " + CIMSTestConstants.TEST_VERSION, it.hasNext());
		System.err.println(it.next());
	}

	@Test
	public void propertyQuery() {

		// Find all the properties of an element.
		List<Restriction> r = new ArrayList<Restriction>();

		Long elementId = 1531779L;

		ElementRef concept = new ElementRef(ElementVersion.class);
		r.add(new FieldEq(concept, "elementId", elementId));

		ElementRef property = new ElementRef(PropertyVersion.class);
		ElementRef conceptProperty = new ElementRef(ConceptPropertyVersion.class);
		r.add(new MightBeA(property, conceptProperty));

		r.add(new OrRestriction(new PointsToElement(property, "domainElementId", concept), new PointsToElement(
				conceptProperty, "rangeElementId", concept)));

		// Hey, how cool is this? I can get rid of the
		// ElementOperations.loadProperties method!

		Iterator<Long> it = find("ICD-10-CA", CIMSTestConstants.TEST_VERSION, property, r);
		dumpValues(it, 1000);

	}

	@Test
	public void transitiveRelationshipTest() {
		List<Restriction> r = new ArrayList<Restriction>();

		// Find the chapter of category A00

		ElementRef cat = new ElementRef(ConceptVersion.class);
		cat.setName("Cat");
		r.add(new ClassIn(cat, "Category"));
		r.add(new FieldEq(cat, "status", "ACTIVE"));

		ElementRef categoryCode = new ElementRef(TextPropertyVersion.class);
		categoryCode.setName("CatCode");
		r.add(new ClassIn(categoryCode, "Code"));
		r.add(new Fieldike(categoryCode, "value", "A00"));
		r.add(new PointsToElement(categoryCode, "domainElementId", cat));

		ElementRef chapter = new ElementRef(ConceptVersion.class);
		chapter.setName("Chapter");
		r.add(new ClassIn(chapter, "Chapter"));
		r.add(new FieldEq(chapter, "status", "ACTIVE"));

		// r.add(new TransitiveLink(cat, "Narrower", false, chapter));
		r.add(new TransitiveLink(chapter, "Narrower", true, cat));

		Iterator<Long> iterator = find("ICD-10-CA", CIMSTestConstants.TEST_VERSION, chapter, r);

		dumpValues(iterator, 1000);
	}
}
