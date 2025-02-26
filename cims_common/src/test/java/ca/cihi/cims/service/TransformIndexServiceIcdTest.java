package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
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
import ca.cihi.cims.content.icd.index.IcdIndexAlphabetical;
import ca.cihi.cims.content.icd.index.IcdIndexNeoplasm;
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
public class TransformIndexServiceIcdTest {

	private static final Log LOGGER = LogFactory.getLog(TransformIndexServiceIcdTest.class);

	private static final String DESCRIPTION = "description";

	@Autowired
	private TransformIndexService transformIndexService;

	private ContextAccess context;
	private IndexXmlGenerator indexXmlGenerator;
	private Long runId;

	@Before
	public void setUp() {
		context = transformIndexService.getContextProvider().findContext(
				ContextDefinition.forVersion(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION));
		indexXmlGenerator = new IndexXmlGenerator();
		runId = Long.valueOf(-1);
	}

	@Test
	public void testAlphabeticalLeadTerm() {

		// "cavité"
		Ref<IcdIndexAlphabetical> refIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(refIndex, refIndex.eq(DESCRIPTION, "cavité"));
		Assert.assertTrue(iterator.hasNext());

		IcdIndexAlphabetical aIndex = iterator.next();
		transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, "2015", aIndex, runId, Index.LANGUAGE_FRA,
				context, indexXmlGenerator, true);
		String longPresentation = aIndex.getPresentationHtml(Index.LANGUAGE_FRA);
		Assert.assertNotNull(longPresentation);
		Assert.assertFalse(longPresentation.indexOf(">cavité</a>") == -1);
		// Assert.assertFalse(longPresentation.indexOf(">K65.0</a>") == -1);
	}

	@Test
	public void testBookIndex() {
		Ref<BookIndex> iIndex = ref(BookIndex.class);
		Iterator<BookIndex> iterator = context.find(iIndex, iIndex.eq("code", "A"));
		Assert.assertTrue(iterator.hasNext());

		while (iterator.hasNext()) {
			BookIndex bookIndex = iterator.next();
			String language = bookIndex.getLanguage();
			transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
					bookIndex, runId, language, context, indexXmlGenerator, true);
			Assert.assertNotNull(bookIndex.getPresentationHtml(language));
		}
	}

	@Test
	public void testDrugLetterIndex() {
		BookIndex bookIndex = transformIndexService.getBookIndex(context, IndexXmlGenerator.BOOK_INDEX_TYPE_DRUGS,
				Index.LANGUAGE_ENG);
		Assert.assertNotNull(bookIndex);

		// Get all child
		Collection<Index> letterIndices = bookIndex.getChildren();
		Assert.assertTrue(letterIndices.size() == 26);

		if (!letterIndices.isEmpty()) {
			try {
				transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						letterIndices.iterator().next(), runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}
	}

	@Test
	public void testIndexWithCatRef() {
		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex,
				iIndex.eq(DESCRIPTION, "Acidity, gastric (high) (low)"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexAlphabetical caIndex = iterator.next();
			Collection<Index> indexList = caIndex.descendantIndices();

			try {
				transformIndexService.transformIndexData(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG, indexList, runId, context,
						indexXmlGenerator);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}
	}

	@Test
	public void testIndexWithMCatRef() {
		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq(DESCRIPTION, "with coma"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexAlphabetical caIndex = iterator.next();
			Collection<Index> indexList = caIndex.descendantIndices();

			try {
				transformIndexService.transformIndexData(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG, indexList, runId, context,
						indexXmlGenerator);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}
	}

	@Test
	public void testIndexWithNote() {
		Ref<IcdIndexNeoplasm> iIndex = ref(IcdIndexNeoplasm.class);
		Iterator<IcdIndexNeoplasm> iterator = context.find(iIndex, iIndex.eq(DESCRIPTION, "bone (periosteum)"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexNeoplasm caIndex = iterator.next();

			try {
				transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						caIndex, runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}
	}

	@Test
	public void testLeadTermWithNote() {

		// "Adenocarcinoma"
		Ref<IcdIndexAlphabetical> refIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(refIndex,
				refIndex.eq(DESCRIPTION, "Adenocarcinoma (see also Neoplasm, malignant)"));
		Assert.assertTrue(iterator.hasNext());

		IcdIndexAlphabetical aIndex = iterator.next();
		transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION, aIndex,
				runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
		String longPresentation = aIndex.getPresentationHtml(Index.LANGUAGE_ENG);
		Assert.assertNotNull(longPresentation);
		Assert.assertFalse(longPresentation.indexOf(">Adenocarcinoma (see also Neoplasm, malignant)</a>") == -1);
		Assert.assertFalse(longPresentation.indexOf("Note:") == -1);
	}

	@Test
	public void testLetterIndex() {
		Ref<LetterIndex> iIndex = ref(LetterIndex.class);
		Iterator<LetterIndex> iterator = context.find(iIndex, iIndex.eq(DESCRIPTION, "A"));
		Assert.assertTrue(iterator.hasNext());

		LetterIndex letterIndex = iterator.next();
		transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
				letterIndex, runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
		String longPresentation = letterIndex.getPresentationHtml(Index.LANGUAGE_ENG);
		Assert.assertNotNull(longPresentation);
		Assert.assertFalse(longPresentation.indexOf("A</a>") == -1);
	}

	@Test
	public void testNeoplasmBookIndex() {

		BookIndex bookIndex = transformIndexService.getBookIndex(context, IndexXmlGenerator.BOOK_INDEX_TYPE_NEOPLASM,
				Index.LANGUAGE_ENG);
		Assert.assertNotNull(bookIndex);
		try {
			transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
					bookIndex, runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage());
		}
	}

	@Test
	public void testNeoplasmIndex() {
		Ref<IcdIndexNeoplasm> iIndex = ref(IcdIndexNeoplasm.class);
		Iterator<IcdIndexNeoplasm> iterator = context.find(iIndex, iIndex.eq(DESCRIPTION, "symphysis pubis"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexNeoplasm caIndex = iterator.next();

			try {
				transformIndexService.transformIndexConcept(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						caIndex, runId, Index.LANGUAGE_ENG, context, indexXmlGenerator, true);

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}
	}

	@Test
	public void testTransformIndexConcept() {

		// Test the index with pairedCodeValue
		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex,
				iIndex.eq(DESCRIPTION, "in (due to) (with) diabetes"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexAlphabetical caIndex = iterator.next();
			try {
				transformIndexService.transformIndexConcept(caIndex, Index.LANGUAGE_ENG, context, true);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}

	}

	@Test
	public void testTransformIndexData() {

		Ref<IcdIndexAlphabetical> iIndex = ref(IcdIndexAlphabetical.class);
		Iterator<IcdIndexAlphabetical> iterator = context.find(iIndex, iIndex.eq(DESCRIPTION, "late, of newborn"));
		Assert.assertTrue(iterator.hasNext());

		// Get all descendent
		if (iterator.hasNext()) {
			IcdIndexAlphabetical caIndex = iterator.next();
			Collection<Index> indexList = caIndex.descendantIndices();

			try {
				transformIndexService.transformIndexData(CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION,
						IndexXmlGenerator.BOOK_INDEX_TYPE_ALPHABETICAL, Index.LANGUAGE_ENG, indexList, runId, context,
						indexXmlGenerator);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage());
			}
		}

	}
}