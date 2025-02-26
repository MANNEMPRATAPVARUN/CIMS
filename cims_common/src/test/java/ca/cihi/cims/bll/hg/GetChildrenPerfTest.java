package ca.cihi.cims.bll.hg;

import static ca.cihi.cims.bll.query.FindCriteria.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
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
import ca.cihi.cims.content.shared.BaseConcept;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;
import ca.cihi.cims.util.timer.Perf;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class GetChildrenPerfTest {

	@Autowired
	private ContextProvider provider;

	// @Autowired
	// private JdbcTemplate template;

	@Autowired
	private ContextFinder finder;

	private int conceptsSoFar;

	private void foo(IcdTabular tab, int level) {
		//
		// if (level > 2)
		// return;

		if (conceptsSoFar++ > 500) {
			return;
		}

		String res = StringUtils.repeat("\t", level) + tab.getCode() + ":" + tab.getShortDescription("ENG");
		System.err.println(res);

		for (IcdTabular child : tab.getSortedChildren()) {
			foo(child, level + 1);
		}
	}

	private void iterateChildren(Collection<IcdTabular> children, int level) {
		Perf.start("iterating children");
		for (IcdTabular child : children) {
			Perf.start("print child short description");
			System.err.println(StringUtils.repeat("\t", level) + child.getCode() + " "
					+ child.getShortDescription("ENG"));
			Perf.stop("print child short description");

			if (level < 3) {

				Perf.start("getChildren");
				Collection<IcdTabular> children2 = child.getChildren();
				Perf.stop("getChildren");

				iterateChildren(children2, level + 1);
			}

		}
		Perf.stop("iterating children");
	}

	@Test
	public void testGetChildrenSpeed() {
		Perf.start("testGetChildrenSpeed");

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.findContext(contextId);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> chapters = context.find(icdTab, icdTab.eq("typeCode", "Chapter"));

		// while (chapters.hasNext()) {

		Perf.start("loadChapter");
		IcdTabular chapter = chapters.next();
		Perf.stop("loadChapter");

		Perf.start("getChildren");
		Collection<IcdTabular> children = chapter.getChildren();
		Perf.stop("getChildren");

		iterateChildren(children, 0);

		// }
		Perf.stop("testGetChildrenSpeed");

		Perf.displayAll();
	}

	@Test
	public void testSpitOutHierarchy() {
		Perf.start("GetChildrenPerfTest.testSpitOutHierarchy");
		ContextAccess ctxtx = provider.findContext(ContextDefinition.forVersion("ICD-10-CA",
				CIMSTestConstants.TEST_VERSION));
		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> chapters = ctxtx.find(icdTab, icdTab.eq("typeCode", "Chapter"));

		BaseConcept concept = chapters.next().getParent();
		if (!(concept instanceof IcdTabular)) {
			fail("Expecting the parent should be IcdTabular!");
		}

		IcdTabular root = (IcdTabular) concept;

		foo(root, 0);

		Perf.stop("GetChildrenPerfTest.testSpitOutHierarchy");
		Perf.displayAll();

	}

}
