package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.index.CciIndexAlphabetical;
import ca.cihi.cims.content.shared.index.BookIndex;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.content.shared.index.LetterIndex;
import ca.cihi.cims.transformation.IndexXmlGenerator;

/**
 * Test class of TransformIndexServiceImpl.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformIndexServiceCciTest {

	@Autowired
	private TransformIndexService transformIndexService;

	private ContextAccess context;
	private IndexXmlGenerator indexXmlGenerator;
	private Long runId;

	@Before
	public void setUp() {
		context = transformIndexService.getContextProvider().findContext(
				ContextDefinition.forVersion(CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION));
		indexXmlGenerator = new IndexXmlGenerator();
		runId = Long.valueOf(-1);
	}

	@Test
	public void testAlphabeticalLeadTerm() {

		// "Abduction, arytenoid"
		Ref<CciIndexAlphabetical> refIndex = ref(CciIndexAlphabetical.class);
		Iterator<CciIndexAlphabetical> iterator = context.find(refIndex,
				refIndex.eq("description", "Abduction, arytenoid"));
		Assert.assertTrue(iterator.hasNext());

		CciIndexAlphabetical aIndex = iterator.next();
		transformIndexService.transformIndexConcept(CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION, aIndex, runId,
				Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
		String longPresentation = aIndex.getPresentationHtml(Index.LANGUAGE_ENG);
		Assert.assertNotNull(longPresentation);
		Assert.assertFalse(longPresentation.indexOf(">Abduction, arytenoid</a>") == -1);
		Assert.assertFalse(longPresentation.indexOf("1.GD.83.^^</a>") == -1);
	}

	@Test
	public void testBookIndex() {
		Ref<BookIndex> iIndex = ref(BookIndex.class);
		Iterator<BookIndex> iterator = context.find(iIndex, iIndex.eq("code", "A"));
		Assert.assertTrue(iterator.hasNext());

		while (iterator.hasNext()) {
			BookIndex bookIndex = iterator.next();
			String language = bookIndex.getLanguage();
			transformIndexService.transformIndexConcept(CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION, bookIndex,
					runId, language, context, indexXmlGenerator, true);
			Assert.assertNotNull(bookIndex.getPresentationHtml(language));
		}
	}


	@Test
	public void testLetterIndex() {
		Ref<LetterIndex> iIndex = ref(LetterIndex.class);
		Iterator<LetterIndex> iterator = context.find(iIndex, iIndex.eq("description", "A"));
		Assert.assertTrue(iterator.hasNext());

		LetterIndex letterIndex = iterator.next();
		transformIndexService.transformIndexConcept(CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION, letterIndex,
				runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
		String longPresentation = letterIndex.getPresentationHtml(Index.LANGUAGE_ENG);
		Assert.assertNotNull(longPresentation);
		Assert.assertFalse(longPresentation.indexOf(">A</a>") == -1);
	}

	@Test
	public void testTransformIndexData() {

		Ref<CciIndexAlphabetical> iIndex = ref(CciIndexAlphabetical.class);
		Iterator<CciIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq("description", "Administration"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			CciIndexAlphabetical caIndex = iterator.next();
			Collection<Index> indexList = caIndex.descendantIndices();
			for (Index index : indexList) {
				transformIndexService.transformIndexConcept(CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION, index,
						runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
			}
		}
	}
}