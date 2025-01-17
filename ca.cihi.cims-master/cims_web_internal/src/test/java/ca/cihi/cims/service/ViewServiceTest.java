package ca.cihi.cims.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.ContentDisplayMapper;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.model.CciCodeValidation;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.IcdCodeValidation;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.service.sgsc.SGSCService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ViewServiceTest {

	@Mock
	ContentDisplayMapper contentDisplayMapper;

	@Mock
	LookupMapper lookupMapper;
	/*
	 * @Autowired ContentDisplayMapper mapperReal;
	 */
	@Autowired
	LookupService lookupService;
	@Mock
	SGSCService sgscService;

	ViewServiceImpl viewService;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);

		viewService = new ViewServiceImpl();

		viewService.setContentDisplayMapper(contentDisplayMapper);
		viewService.setLookupService(lookupService);
		viewService.setSgscService(sgscService);
		when(contentDisplayMapper.getTreeNodes(nullable(Map.class))).thenReturn(mockContentViewerModelList());

		when(contentDisplayMapper.getContentList(nullable(Map.class))).thenReturn(mockContentViewerModelList());
		when(contentDisplayMapper.getShortPresentation(nullable(Map.class))).thenReturn(mockConceptShortPresentation());
		when(contentDisplayMapper.getSearchResultsByBookIndex(nullable(Map.class))).thenReturn(mockSearchResult());
		when(contentDisplayMapper.getSearchResultsByCode(nullable(Map.class))).thenReturn(mockSearchResult());
		when(contentDisplayMapper.getSearchResultsByBookIndexAll(nullable(Map.class))).thenReturn(mockSearchResult());

		when(contentDisplayMapper.getConceptIdFromCode(nullable(Map.class))).thenReturn("conceptId");
		when(contentDisplayMapper.getHierCCIValidationRulesFromConceptId(nullable(Map.class)))
				.thenReturn(mockCciCodeValidations());
		when(contentDisplayMapper.getAllBookIndexes(nullable(Map.class))).thenReturn(mockGetAllBookIndexes());
		when(contentDisplayMapper.getAllBookIndexesNoLang(nullable(Map.class))).thenReturn(mockGetAllBookIndexes());

		when(contentDisplayMapper.getAttributesFromReferenceCode(nullable(Map.class))).thenReturn(mockContentViewerModelList());
		when(contentDisplayMapper.getBaseContextId(nullable(Map.class))).thenReturn(1L);

	}

	private List<CciCodeValidation> mockCciCodeValidations() {
		List<CciCodeValidation> cciCodeValidations = new LinkedList<CciCodeValidation>();
		CciCodeValidation cciCodeValidation = new CciCodeValidation();
		cciCodeValidations.add(cciCodeValidation);
		return cciCodeValidations;
	}

	private String mockConceptShortPresentation() {
		return "this is short presentation";
	}

	private List<ContentToSynchronize> mockContentsToSynchronize() {
		List<ContentToSynchronize> contentsToSynchronize = new ArrayList<ContentToSynchronize>();
		contentsToSynchronize.add(mockContentToSynchronize());
		return contentsToSynchronize;
	}

	private ContentToSynchronize mockContentToSynchronize() {
		ContentToSynchronize contentToSynchronize = new ContentToSynchronize();
		contentToSynchronize.setElementId(1L);
		contentToSynchronize.setType("type");
		return contentToSynchronize;
	}

	private List<ContentViewerModel> mockContentViewerModelList() {
		List<ContentViewerModel> contentViewerModels = new ArrayList<ContentViewerModel>();
		ContentViewerModel contentViewerModel = new ContentViewerModel();
		contentViewerModels.add(contentViewerModel);
		return contentViewerModels;
	}

	private List<CodeDescription> mockGetAllBookIndexes() {
		List<CodeDescription> allBookIndexes = new LinkedList<CodeDescription>();
		CodeDescription codeDesc = new CodeDescription();
		codeDesc.setCode("862705");
		codeDesc.setDescription("Section IV -- Table of Neoplasm Index");
		allBookIndexes.add(codeDesc);
		return allBookIndexes;

	}

	private IcdCodeValidation mockIcdCodeValidation() {
		IcdCodeValidation icdCodeValidation = new IcdCodeValidation();
		return icdCodeValidation;
	}

	private List<IcdCodeValidation> mockIcdCodeValidations() {
		List<IcdCodeValidation> icdCodeValidations = new ArrayList<IcdCodeValidation>();
		icdCodeValidations.add(mockIcdCodeValidation());
		return icdCodeValidations;
	}

	private List<SearchResultModel> mockSearchResult() {
		List<SearchResultModel> searchResults = new LinkedList<SearchResultModel>();
		SearchResultModel searchResultModel = new SearchResultModel();
		searchResultModel.setConceptCode("A00");
		searchResults.add(searchResultModel);
		return searchResults;
	}

	@Test
	public void testGetAllBookIndexes() {
		String test_classification = "ICD-10-CA";
		Long test_contextId = 1L;
		String test_language = "ENG";
		List<CodeDescription> allBookIndexes = viewService.getAllBookIndexes(test_classification, test_contextId,
				test_language);
		assertTrue(allBookIndexes.size() == 1);
	}

	@Test
	public void testGetAllBookIndexesNoLang() {
		String test_classification = "ICD-10-CA";
		List<CodeDescription> allBookIndexes = viewService.getAllBookIndexesNoLang(test_classification);
		assertTrue(allBookIndexes.size() > 0);
	}

	@Test
	public void testGetAttributesFromReferenceCode() {
		String refAttrCode = "refAttrCode";
		String classification = "CCI";
		Long contextId = 1L;
		String language = "ENG";
		List<ContentViewerModel> cvms = viewService.getAttributesFromReferenceCode(refAttrCode, classification,
				contextId, language);
		assertTrue(cvms.size() == 1);
	}

	@Test
	public void testGetBaseContextId() {
		long contextId = 1l;
		when(contentDisplayMapper.getBaseContextId(nullable(Map.class))).thenReturn(1L);
		Long baseContextId = viewService.getBaseContextId(contextId);
		assertTrue(baseContextId == 1l);
	}

	@Test
	public void testGetCategoryClassId() {
		String classification = "CCI";
		when(contentDisplayMapper.getCategoryClassId(nullable(Map.class))).thenReturn(1L);
		Long categoryClassId = viewService.getCategoryClassId(classification);
		assertTrue(categoryClassId == 1l);
	}

	@Test
	public void testGetCCIClassID() {
		String tablename = "tablename";
		String classname = "classname";
		when(contentDisplayMapper.getCCIClassID(nullable(Map.class))).thenReturn(1L);
		Long cciClassId = viewService.getCCIClassID(tablename, classname);
		assertTrue(cciClassId == 1l);
	}

	@Test
	public void testGetCodeClassId() {
		String classification = "CCI";
		when(contentDisplayMapper.getCodeClassId(nullable(Map.class))).thenReturn(1L);
		long codeClassId = viewService.getCodeClassId(classification);
		assertTrue(codeClassId == 1l);
	}

	@Test
	public void testGetConceptIdFromCode() {
		String code = "A00";
		String contextId = "1";
		when(contentDisplayMapper.getConceptIdFromCode(nullable(Map.class))).thenReturn("1");
		String conceptId = viewService.getConceptIdFromCode(code, contextId);
		assertTrue(conceptId.equalsIgnoreCase("1"));
	}

	@Test
	public void testGetConceptIdPathByElementId() {
		String classification = "CCI";
		Long contextId = 1L;
		Long elementId = 1L;
		when(contentDisplayMapper.getConceptIdPathByElementId(nullable(String.class), nullable(Long.class), nullable(Long.class)))
				.thenReturn("A00/A0001");
		String conceptIdPath = viewService.getConceptIdPathByElementId(classification, contextId, elementId);
		assertTrue(conceptIdPath.equalsIgnoreCase("A00/A0001"));
	}

	@Test
	public void testGetConceptMajorType() {
		String elementId = "1";
		when(contentDisplayMapper.getConceptMajorType(nullable(Map.class))).thenReturn("block");
		String conceptMajorType = viewService.getConceptMajorType(elementId);
		assertTrue(conceptMajorType.equalsIgnoreCase("block"));
	}

	@Test
	public void testGetConceptShortPresentation() {
		String test_code = "O08.0";
		String test_classification = "ICD-10-CA";
		// String test_fiscalYear ="2012";
		Long test_fcontextId = 1L;

		String test_language = "ENG";
		String conceptShortPresentation = viewService.getConceptShortPresentation(test_code, test_classification,
				test_fcontextId, test_language);
		// in case the transform not run

		String expected = "this is short presentation";
		assertTrue("Should have contained the expected result", conceptShortPresentation.contains(expected));

	}

	@Test
	public void testGetContentListWithContextId() {
		String test_unitConceptId = "39";
		String test_classification = "ICD-10-CA";
		Long test_contextId = 1L;
		String test_language = "ENG";
		String test_requestId = null;
		when(contentDisplayMapper.getBaseContextId(nullable(Map.class))).thenReturn(1L);
		when(contentDisplayMapper.getConceptMajorType(nullable(Map.class))).thenReturn("TABULAR");
		// this is the chapter 01
		List<ContentViewerModel> contentViewerModels = viewService.getContentList(test_unitConceptId,
				test_classification, test_contextId, test_language, test_requestId, true, Boolean.FALSE);
		if (contentViewerModels != null) {
			assertTrue("should return a list", contentViewerModels.size() >= 0);
		}
	}

	@Test
	public void testGetContentToSynchronize() {
		long contextId = 1l;
		when(contentDisplayMapper.getContentToSynchronize(nullable(Map.class))).thenReturn(mockContentsToSynchronize());
		List<ContentToSynchronize> contents = viewService.getContentToSynchronize(contextId);
		assertTrue(contents.size() == 1);
	}

	@Test
	public void testGetGroupClassId() {
		String classification = "CCI";
		when(contentDisplayMapper.getGroupClassId(nullable(Map.class))).thenReturn(1L);
		long groupClassId = viewService.getGroupClassId(classification);
		assertTrue(groupClassId == 1l);
	}

	@Test
	public void testGetHierICDValidationRulesForCategory() {
		String categoryCode = "A0";
		String classification = "ICD";
		String contextId = "1";
		String language = "ENG";
		when(contentDisplayMapper.getHierICDValidationRulesFromConceptId((Map<String, Object>) nullable(Map.class)))
				.thenReturn(mockIcdCodeValidations());
		List<IcdCodeValidation> icdCodeValidations = viewService.getHierICDValidationRulesForCategory(categoryCode,
				classification, contextId, language);
		assertTrue(icdCodeValidations.size() == 1);
	}

	@Test
	public void testGetHierValidationRules() {
		String test_conceptCode = "1.AA.13.^^";
		String test_classification = "CCI";
		// String test_fiscalYear ="2012";

		// ContextAccess contextAccess= contextProvider.findContext(ContextDefinition.forVersion("CCI", "2012"));
		// String test_contextId=String.valueOf(contextAccess.getContextId().getContextId());
		List<CciCodeValidation> cciCodeValidations = viewService.getHierCCIValidationRulesForRubric(test_conceptCode,
				test_classification, "10000", "ENG");
		if (cciCodeValidations != null) {
			assertTrue(cciCodeValidations.size() >= 0);
		}

	}

	@Test
	public void testGetIndexTermSearchResults() {
		// viewService = new ViewServiceImpl();
		// viewService.setContentDisplayMapper(mapperReal);
		// viewService.setLookupService(lookupService);
		String test_classification = "CCI";
		List<CodeDescription> allBookIndexes = viewService.getAllBookIndexesNoLang(test_classification);
		for (CodeDescription indexBook : allBookIndexes) {
			Long indexElementId = Long.parseLong(indexBook.getCode());
			String searchString = "ad";
			int maxResult = 18;
			List<SearchResultModel> results = viewService.getIndexTermSearchResults(test_classification, indexElementId,
					searchString, maxResult);
			assertTrue(results.size() <= 18);
			for (SearchResultModel result : results) {
				assertTrue(result.getConceptCode().toUpperCase().startsWith("A00"));
			}
		}
	}

	@Test
	public void testGetSearchResultsWithContextId() {
		String test_classification = "ICD-10-CA";
		Long test_contextId = 1L;
		String test_language = "ENG";
		String test_searchString = "A00";
		int test_maxResults = 10;
		List<SearchResultModel> searchResultModels = viewService.getSearchResults(test_classification, test_contextId,
				test_language, "code", null, test_searchString, test_maxResults);
		String expectedConceptCode = "A00";
		for (SearchResultModel searchResultModel : searchResultModels) {
			assertTrue("Should have contained the expected result",
					searchResultModel.getConceptCode().contains(expectedConceptCode));
		}
	}

	@Test
	public void testGetTreeNodesWithContextId() {
		String test_conceptId = "39";
		String test_classification = "ICD-10-CA";
		Long test_contextId = 1L;
		String test_language = "ENG";
		String test_chapterId = "01";
		List<ContentViewerModel> result = viewService.getTreeNodes(test_conceptId, test_classification, test_contextId,
				test_language, test_chapterId);
		if (result != null) {
			assertTrue("Should have the expected result", result.size() >= 0);
		}
	}
}
