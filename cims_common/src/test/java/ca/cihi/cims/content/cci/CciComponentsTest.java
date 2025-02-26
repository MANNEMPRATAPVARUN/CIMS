package ca.cihi.cims.content.cci;

import static ca.cihi.cims.bll.query.FindCriteria.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

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
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class CciComponentsTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	SortedSet<CciTabular> blah(Iterator<CciTabular> cciTabular) {

		CciTabular[] cci = (CciTabular[]) IteratorUtils.toArray(cciTabular, CciTabular.class);

		return new TreeSet<CciTabular>(Arrays.asList(cci));
	}

	@Test
	@Ignore
	public void testConceptListOutComponents() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> iterator = context.find(cciTab, cciTab.eq("code", "1.AA.52.SE-AZ"));

		CciTabular cci = iterator.next();

		LOGGER.debug(cci.getTissueComponent().getCode());
		LOGGER.debug(cci.getInterventionComponent().getCode());
		LOGGER.debug(cci.getDeviceAgentComponent().getCode());
		LOGGER.debug(cci.getApproachTechniqueComponent().getCode());
		LOGGER.debug(cci.getGroupComponent().getCode());

		Ref<CciTissueComponent> cciTissue = ref(CciTissueComponent.class);
		Iterator<CciTissueComponent> i = context.find(cciTissue, cciTissue.eq("code", "C"),
				cciTissue.link("sectionAssociatedWith", cciTab), cciTab.eq("code", "1"));
		CciTissueComponent b = i.next();

		cci.setTissueComponent(b);

		context.persist();
	}

	@Test
	@Ignore
	public void testCreateAGroupComponent() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess cc = provider.createChangeContext(contextId, null);

		// So, if you had the elementID, you'd do something like
		// CciTabular obj = context.load(862715);
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> iterator = cc.find(cciTab, cciTab.eq("code", "1"),
				cciTab.eq("typeCode", CciTabular.SECTION));

		CciTabular cci = iterator.next();
		LOGGER.debug("Found Section " + cci.getCode());

		CciGroupComponent b = CciGroupComponent.create(cc, "ZS", cci);
		b.setShortTitle("ENG", "Vancouver Canucks");

		cc.persist();
		cc.realizeChangeContext(false);
	}

	@Test
	public void testDeviceAgentComponent() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess cc = provider.findContext(contextId);

		Ref<CciDeviceAgentComponent> cciGC = ref(CciDeviceAgentComponent.class);

		Iterator<CciDeviceAgentComponent> iterator = cc.find(cciGC, cciGC.eq("code", "1C"));

		CciDeviceAgentComponent b = iterator.next();

		LOGGER.info(b.getCode());
		LOGGER.info(b.getAgentATCCode());
		LOGGER.info(b.getAgentExample("ENG"));
		LOGGER.info(b.getAgentGroup().getCode());
		LOGGER.info(b.getAgentTypeDescription("ENG"));
		// LOGGER.info(b.g);

	}

	@Test
	@Ignore
	public void testFindAGroupComponent() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciGroupComponent> cciTab = ref(CciGroupComponent.class);
		Iterator<CciGroupComponent> iterator = context.find(cciTab, cciTab.eq("code", "ZS"));

		while (iterator.hasNext()) {

			CciGroupComponent b = iterator.next();
			LOGGER.debug("[" + b.getCode() + "] [" + b.getSectionAssociatedWith().getCode() + "] ["
					+ b.getShortTitle("ENG") + "]");

		}
	}

	@Test
	@Ignore
	public void testFindAllAgentGroups() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<CciAgentGroup> iterator = context.findAll(CciAgentGroup.class);

		while (iterator.hasNext()) {

			CciAgentGroup b = iterator.next();
			LOGGER.debug(b.getCode() + " " + b.getDescription("ENG"));
			Collection<CciDeviceAgentComponent> daComponents = b.getSortedDeviceAgentComponents();

			for (CciDeviceAgentComponent da : daComponents) {
				LOGGER.debug("- " + da.getCode() + "  Section: " + da.getSectionAssociatedWith().getCode());
			}
		}
	}

	@Test
	@Ignore
	public void testFindAllDeviceAgentsComponents() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<CciDeviceAgentComponent> iterator = context.findAll(CciDeviceAgentComponent.class);

		while (iterator.hasNext()) {

			CciDeviceAgentComponent b = iterator.next();
			LOGGER.debug("[" + b.getCode() + "] [" + b.getSectionAssociatedWith().getCode() + "] ["
					+ b.getShortTitle("ENG") + "]");
		}
	}

	@Test
	@Ignore
	// Dont run, displays all groups and is slow
	public void testFindAllGroupComponents() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Iterator<CciGroupComponent> iterator = context.findAll(CciGroupComponent.class);

		while (iterator.hasNext()) {

			CciGroupComponent b = iterator.next();
			LOGGER.debug("[" + b.getCode() + "] [" + b.getSectionAssociatedWith().getCode() + "] ["
					+ b.getShortTitle("ENG") + "]");
		}
	}

	@Test
	public void testGroupComponentForSection1Smarter() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Ref<CciGroupComponent> component = ref(CciGroupComponent.class);

		Iterator<CciGroupComponent> components = context.find(component, cciTab.eq("typeCode", CciTabular.SECTION),
				cciTab.eq("code", "3"), cciTab.link("sectionGC", component));

		while (components.hasNext()) {

			CciGroupComponent c = components.next();
			// LOGGER.debug("- " + c.getCode() + " / " + c.getStatus() + " / " + c.getShortTitle("ENG"));
		}

	}

	@Test
	public void testSearchforAGroupInASection() {
		// A bit complicated search. Useful for those who need it
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciGroupComponent> cciGC = ref(CciGroupComponent.class);
		Ref<CciTabular> cciTab = ref(CciTabular.class);

		Iterator<CciGroupComponent> iterator = context.find(cciGC, cciGC.eq("code", "SJ"),
				cciGC.link("sectionAssociatedWith", cciTab), cciTab.eq("code", "7"));

		/*
		 * [SJ] [7] [support acts] [SJ] [3] [back] [SJ] [1] [back] [SJ] [2] [back]
		 */

		while (iterator.hasNext()) {

			CciGroupComponent b = iterator.next();
			LOGGER.debug("[" + b.getCode() + "] [" + b.getSectionAssociatedWith().getCode() + "] ["
					+ b.getShortTitle("ENG") + "]");
		}
	}

	@Test
	@Ignore
	public void testTissueComponentButNotASection() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> iterator = context.find(cciTab, cciTab.eq("code", "1GA-1GZ"));

		CciTabular cci = iterator.next();

		Collection<CciTissueComponent> tissues = cci.getSortedSectionTissueComponents();
		assertTrue(tissues.size() > 0);

		assertTrue(cci.getSectionTC().size() == 0);
	}

	@Test
	@Ignore
	// Slow
	public void testTissueComponentForEachSection() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> sections = context.find(cciTab, cciTab.eq("typeCode", CciTabular.SECTION));

		// Lets sort these sections...
		SortedSet<CciTabular> sortedSections = blah(sections);
		for (CciTabular c : sortedSections) {
			LOGGER.debug(c.getCode());

			Collection<CciTissueComponent> tissues = c.getSortedSectionTissueComponents();

			for (CciTissueComponent tissue : tissues) {
				LOGGER.debug("- " + tissue.getCode() + " " + tissue.getShortTitle("ENG"));
			}
		}
	}

	@Test
	@Ignore
	public void testTissueComponentForEachSection1() {

		// A bit complicated search. Useful for those who need it
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTissueComponent> cciTissue = ref(CciTissueComponent.class);
		Ref<CciTabular> cciTab = ref(CciTabular.class);

		Iterator<CciTissueComponent> iterator = context.find(cciTissue, cciTissue.eq("code", "A"),
				cciTissue.link("sectionAssociatedWith", cciTab), cciTab.eq("code", "1"));

		while (iterator.hasNext()) {

			CciTissueComponent b = iterator.next();
			LOGGER.debug("[" + b.getCode() + "] [" + b.getSectionAssociatedWith().getCode() + "] ["
					+ b.getShortTitle("ENG") + "]");
		}

	}

	@Test
	public void testTissueComponentForSection8() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> sections = context.find(cciTab, cciTab.eq("typeCode", CciTabular.SECTION),
				cciTab.eq("code", "8"));

		// Lets sort these sections...
		SortedSet<CciTabular> sortedSections = blah(sections);
		for (CciTabular c : sortedSections) {
			LOGGER.debug(c.getCode());

			Collection<CciTissueComponent> tissues = c.getSortedSectionTissueComponents();

			for (CciTissueComponent tissue : tissues) {
				LOGGER.debug("- " + tissue.getCode() + " / " + tissue.getStatus() + " / " + tissue.getShortTitle("ENG"));
			}
		}
	}

	@Test
	public void testTissueComponentForSection8Smarter() {

		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Ref<CciTissueComponent> cciTissue = ref(CciTissueComponent.class);

		Iterator<CciTissueComponent> components = context.find(cciTissue, cciTab.eq("typeCode", CciTabular.SECTION),
				cciTab.eq("code", "1"), cciTab.link("sectionTC", cciTissue), cciTissue.eq("status", "DISABLED"));

		while (components.hasNext()) {

			CciTissueComponent tissue = components.next();
			LOGGER.debug("- " + tissue.getCode() + " / " + tissue.getStatus() + " / " + tissue.getShortTitle("ENG"));
		}

	}

	@Test
	@Ignore
	public void testUpdateAGroupComponent() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		ContextAccess cc = provider.createChangeContext(contextId, null);

		Ref<CciGroupComponent> cciGC = ref(CciGroupComponent.class);
		Ref<CciTabular> cciTab = ref(CciTabular.class);

		Iterator<CciGroupComponent> iterator = cc.find(cciGC, cciGC.eq("code", "SJ"),
				cciGC.link("sectionAssociatedWith", cciTab), cciTab.eq("code", "1"));

		CciGroupComponent b = iterator.next();
		b.setShortTitle("ENG", "Vancouver Canucks");

		cc.persist();
		cc.realizeChangeContext(false);
	}

}
