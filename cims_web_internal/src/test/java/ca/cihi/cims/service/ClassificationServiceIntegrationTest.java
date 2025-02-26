package ca.cihi.cims.service;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;
import static ca.cihi.cims.util.CollectionUtils.asSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.MapBindingResult;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.shared.RootConcept;
import ca.cihi.cims.content.shared.TabularConcept;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.model.ContentToSynchronize;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.DxType;
import ca.cihi.cims.model.IndexBookReferencedLink;
import ca.cihi.cims.model.SearchResultModel;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.TabularReferencedLink;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.DrugDetailType;
import ca.cihi.cims.model.index.IndexCategoryReferenceModel;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexTermReferenceModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.model.index.NeoplasmDetailType;
import ca.cihi.cims.model.index.TabularReferenceModel;
import ca.cihi.cims.model.supplement.SupplementMatter;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.model.tabular.TabularConceptXmlType;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationGenderModel;
import ca.cihi.cims.service.synchronization.SynchronizationService;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.filter.ThreadLocalCurrentContext;

@Ignore
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ClassificationServiceIntegrationTest {

	// CRITICAL HARDOCODED ELEMENT DESCRIPTION --------------------------------

	private static final String CCI_BASE_CLASSIFICATION = "CCI";
	private static final String ICD_BASE_CLASSIFICATION = "ICD-10-CA";

	// -------------------------------------------------------------------------

	private static final String ICD_CATEGORY_CODE = "A18";
	private static final String ICD_CATEGORY_PARENT_CODE = "A15-A19";

	private static final String SUPLEMENT_CCI_DESCRIPTION = "Acknowledgments";
	private static final String SUPLEMENT_ICD_DESCRIPTION = "Introduction";

	// -------------------------------------------------------------------------

	@Autowired
	private AdminService adminService;
	@Autowired
	private ChangeRequestService changeRequestService;
	@Autowired
	private ThreadLocalCurrentContext context;
	@Autowired
	private ContextProvider contextProvider;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private NonContextOperations nonContextOperations;
	@Autowired
	private ClassificationService service;
	@Autowired
	private SynchronizationService synchronizationService;

	private final TransformationService transformationService = Mockito.mock(TransformationService.class);

	@Autowired
	private ViewService viewService;

	// -------------------------------------------------------------------------

	private TabularConceptModel assertIcdDataReadOnCategory() {
		// test root exception is thrown
		RootConcept root = service.getRootConcept();
		assertNotNull(root);
		try {
			service.getTabularConceptById(root.getElementId());
			fail();
		} catch (RootElementExeption ex) {
		}

		// test ICD_CATEGORY level 1 data
		TabularConceptModel model = service.getTabularConceptByCode(ICD_CATEGORY_CODE);
		assertNotNull(model);
		assertEquals(ICD_CATEGORY_CODE, model.getCode());
		assertEquals(TabularConceptType.ICD_CATEGORY, model.getType());
		assertEquals("Tuberculosis of other organs", model.getUserTitleEng());
		assertEquals(1, model.getNestingLevel());

		// test ICD_BLOCK level 1 data
		TabularConceptModel parent = model.getParent();
		assertNotNull(parent);
		assertEquals(ICD_CATEGORY_PARENT_CODE, parent.getCode());
		// assertEquals(TabularConceptType.ICD_BLOCK, parent.getType());
		assertEquals(1, model.getNestingLevel());
		return model;
	}

	private SupplementModel assertSupplemement(String description, Language lang) {
		SupplementModel top = getSuplementByDescription(description, null, lang);
		assertNotNull(top);
		assertEquals(1, top.getLevel());
		assertEquals(SupplementMatter.FRONT, top.getMatter());
		assertEquals(top.getEntity().getSortingHint(), top.getSortOrder());
		assertEquals(ConceptStatus.ACTIVE, top.getStatus());
		return top;
	}

	@Before
	public void before() {
		service.setTransformationService(transformationService);
	}

	private ChangeRequestDTO createCciTabularContext(boolean versionYear) {
		return createContext(CCI_BASE_CLASSIFICATION, versionYear, ChangeRequestCategory.T, Language.ALL);
	}

	private ChangeRequestDTO createChangeRequest(User user, String baseClassification, long changeContextId,
			ChangeRequestCategory category, Set<Language> languages) {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setBaseClassification(baseClassification);
		changeRequest.setBaseContextId(changeContextId);
		changeRequest.setCategory(category);
		changeRequest.setChangeNatureId(4L);
		changeRequest.setChangeTypeId(1L);
		changeRequest.setChangeRationalTxt("unit test");
		changeRequest.setLanguageCode(languages.containsAll(Language.ALL) ? "ALL"
				: languages.contains(Language.ENGLISH) ? Language.ENGLISH.getCode() : Language.FRENCH.getCode());
		changeRequest.setName(baseClassification + " Test - " + new Date());
		changeRequest.setRequestorId(23l);
		List<Distribution> reviewGroups = new ArrayList<Distribution>();
		reviewGroups.add(newDistribution(Distribution.DL_ID_Classification));
		reviewGroups.add(newDistribution(Distribution.DL_ID_ADMINISTRATOR));
		changeRequest.setReviewGroups(reviewGroups);
		changeRequestService.createChangeRequest(changeRequest, user);

		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequest.setRationaleForValid("unit test");
		changeRequestService.validateChangeRequest(changeRequest, user);
		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		return changeRequest;
	}

	private ChangeRequestDTO createContext(String baseClassification, boolean versionYear,
			ChangeRequestCategory category, Set<Language> languages) {
		ContextIdentifier context = getOpenContextIdentifier(baseClassification, versionYear);
		assertNotNull("Can't find any " + (versionYear ? "version" : "non-version") + " year context", context);
		User user = getTestUser();
		ChangeRequestDTO changeRequest = createChangeRequest(user, baseClassification, context.getContextId(), category,
				languages);
		context = getChangeRequestContextIdentifier(changeRequest);
		assertNotNull(context);
		context.setRequestId(changeRequest.getChangeRequestId());
		createCurrentContext(context);
		changeRequest.setOwner(user);
		return changeRequest;
	}

	private void createCurrentContext(ContextIdentifier id) {
		ContextAccess access = contextProvider.findContext(id);
		assertNotNull(access);
		context.makeCurrentContext(access);
	}

	private ChangeRequestDTO createIcdTabularContext(boolean versionYear) {
		return createContext(ICD_BASE_CLASSIFICATION, versionYear, ChangeRequestCategory.T, Language.ALL);
	}

	private ContextIdentifier getChangeRequestContextIdentifier(ChangeRequestDTO changeRequest) {
		return lookupService.findOpenContextByChangeRquestId(changeRequest.getChangeRequestId());
	}

	private long getElementByDescription(String description, String parentId, Language lang) {
		List<ContentViewerModel> children = viewService.getTreeNodes(parentId, service.getCurrentBaseClassification(),
				service.getCurrentContextId(), lang.getCode(), null);
		for (ContentViewerModel model : children) {
			if (StringUtils.equals(StringUtils.trim(model.getConceptLongDesc()), description)) {
				return Long.parseLong(model.getConceptId());
			}
		}
		for (ContentViewerModel model : children) {
			long m = getElementByDescription(description, model.getConceptId(), lang);
			if (m != 0) {
				return m;
			}
		}
		return 0;
	}

	private IndexModel getIndexByDescription(String description, Long parentId, Language lang) {
		long id = getElementByDescription(description, parentId == null ? null : parentId.toString(), lang);
		return id == 0 ? null : service.getIndexById(id, lang);
	}

	private ContextIdentifier getOpenContextIdentifier(String baseClassification, boolean versionYear) {
		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findOpenBaseContextIdentifiers(baseClassification);
		for (ContextIdentifier id : contextIdentifiers) {
			if (!id.isChangeContext() && (id.isVersionYear() == versionYear)) {
				return id;
			}
		}
		return null;
	}

	private SupplementModel getSuplementByDescription(String description, Long parentId, Language lang) {
		long id = getElementByDescription(description, parentId == null ? null : parentId.toString(), lang);
		return id == 0 ? null : service.getSupplementById(id, lang);
	}

	private User getTestUser() {
		User user = adminService.getUserByUserName("flaw");
		user.setRoles(asSet(SecurityRole.ROLE_ADMINISTRATOR));
		return user;
	}

	private Distribution newDistribution(long id) {
		Distribution distribution = new Distribution();
		distribution.setDistributionlistid(id);
		return distribution;
	}

	private ErrorBuilder newErrorBuilder(Object model) {
		return new ErrorBuilder(new BeanPropertyBindingResult(model, "model"));
	}

	private MockMultipartFile newMultipartFile() {
		return new MockMultipartFile(RandomStringUtils.randomAscii(10) + ".gif",
				RandomStringUtils.randomAscii(10) + ".gif", "image/gif",
				RandomStringUtils.randomAscii(1000).getBytes());
	}

	private void performSuplementOperationsNonVersionYear(String description, String classification) {
		ChangeRequestDTO changeRequest = createContext(classification, false, ChangeRequestCategory.S,
				asSet(Language.ENGLISH));
		User user = changeRequest.getOwner();
		OptimisticLock lock = new OptimisticLock(Long.MIN_VALUE);
		Language lang = changeRequest.getLanguages().iterator().next();

		SupplementModel top = assertSupplemement(description, lang);
		{
			// test version year
			assertFalse(service.isVersionYear());
			// test permissions
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.S);
			assertFalse(p.isCanAdd());
			assertFalse(p.isCanDelete());
			assertTrue(p.isCanWrite());
		}
		{
			// test save
			ErrorBuilder result = newErrorBuilder(top);
			top.setDescription("top");
			service.saveSupplement(lock, result, user, top, lang);
			assertFalse(result.hasErrors());
		}
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	private void perfromSuplementOperationsVersionYear(String description, String classification) {
		ChangeRequestDTO changeRequest = createContext(classification, true, ChangeRequestCategory.S,
				asSet(Language.ENGLISH));
		User user = changeRequest.getOwner();
		OptimisticLock lock = new OptimisticLock(Long.MIN_VALUE);
		Language lang = changeRequest.getLanguages().iterator().next();

		SupplementModel top = assertSupplemement(description, lang);
		{
			// test version year
			assertTrue(service.isVersionYear());
			// test permissions
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.S);
			assertTrue(p.isCanAdd());
			assertTrue(p.isCanDelete());
			assertTrue(p.isCanWrite());
		}
		{
			// test save
			ErrorBuilder result = newErrorBuilder(top);
			top.setDescription("top");
			service.saveSupplement(lock, result, user, top, lang);
			assertFalse(result.hasErrors());
		}
		{
			// create a invalid child
			SupplementModel child1 = new SupplementModel();
			child1.setDescription("1");
			ErrorBuilder result = newErrorBuilder(top);
			service.createSupplement(lock, result, user, top.getElementId(), child1, lang);
			assertTrue(result.hasErrors());
			assertNotNull(result.getFieldError("status"));

			// create a normal child
			child1.setStatus(ConceptStatus.ACTIVE);
			result = newErrorBuilder(top);
			service.createSupplement(lock, result, user, top.getElementId(), child1, lang);
			assertFalse(result.hasErrors());

			{
				// assert child
				SupplementModel _child1 = service.getSupplementById(child1.getElementId(), lang);
				assertEquals(2, _child1.getLevel());
				assertEquals(child1.getDescription(), _child1.getDescription());
				assertEquals(ConceptStatus.ACTIVE, _child1.getStatus());
			}
			{
				// can't disable NEW child
				child1.setStatus(ConceptStatus.DISABLED);
				result = newErrorBuilder(top);
				service.saveSupplement(lock, result, user, child1, lang);
				assertTrue(result.hasErrors());
				assertNotNull(result.getFieldError("status"));
			}
			{
				// create second level child
				SupplementModel child2 = new SupplementModel();
				child2.setDescription("2");
				child2.setStatus(ConceptStatus.ACTIVE);
				result = newErrorBuilder(top);
				service.createSupplement(lock, result, user, child1.getElementId(), child2, lang);
				assertFalse(result.hasErrors());
				{
					// fail to delete node with a child
					try {
						service.deleteSupplementById(lock, user, child1.getElementId(), lang);
						fail("can't delete element with a child");
					} catch (Exception ex) {
					}
				}
				// delete 2 child
				service.deleteSupplementById(lock, user, child2.getElementId(), lang);
				// delete 1 child
				// if (false) {
				// service.deleteSupplementById(lock, user, child1.getElementId(), lang);
				// }
			}
			try {
				// delete top
				service.deleteSupplementById(lock, user, top.getElementId(), lang);
				fail("Can't delete published concept");
			} catch (Exception ex) {
			}
		}
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Index_CCI_VersionYear() throws Exception {
		String classification = CCI_BASE_CLASSIFICATION;
		String description = "Alphabetical Index";

		ChangeRequestDTO changeRequest = createContext(classification, true, ChangeRequestCategory.I,
				asSet(Language.ENGLISH));
		User user = changeRequest.getOwner();
		OptimisticLock lock = new OptimisticLock(Long.MIN_VALUE);
		Language lang = changeRequest.getLanguages().iterator().next();
		{
			// test version year
			assertTrue(service.isVersionYear());
			// test permissions
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
			assertTrue(p.isCanAdd());
			assertTrue(p.isCanDelete());
			assertTrue(p.isCanWrite());
		}
		IndexModel top = getIndexByDescription(description, null, lang);
		{
			assertEquals(0, top.getSection());
			assertEquals(IndexType.CCI_BOOK_INDEX, top.getType());
			assertEquals(ConceptStatus.ACTIVE, top.getStatus());
		}
		{
			{
				// test save
				top.setDescription("top");
				ErrorBuilder result = newErrorBuilder(top);
				service.saveIndex(lock, result, user, top, lang);
				assertFalse(result.hasErrors());
			}
			{
				// create a invalid BOOK INDEX child
				IndexModel child1 = new IndexModel();
				child1.setDescription("1");
				child1.setStatus(ConceptStatus.ACTIVE);
				try {
					service.createIndex(lock, newErrorBuilder(child1), user, top.getElementId(), child1, lang);
					fail("Can't create CCI book index child");
				} catch (Exception ex) {
				}
			}
			IndexModel a = getIndexByDescription("A", top.getElementId(), lang);
			{
				assertEquals(0, a.getSection());
				assertEquals(ConceptStatus.ACTIVE, a.getStatus());
				assertEquals(IndexType.CCI_LETTER_INDEX, a.getType());
				{
					// create a invalid BOOK INDEX child
					IndexModel child1 = new IndexModel();
					child1.setDescription("z");
					child1.setStatus(ConceptStatus.ACTIVE);
					try {
						service.createIndex(lock, newErrorBuilder(child1), user, a.getElementId(), child1, lang);
						fail("Can't create child non started with A ");
					} catch (Exception ex) {
					}
					{
						// success to create index
						child1.setDescription("Azzz");
						ErrorBuilder result1 = newErrorBuilder(child1);
						service.createIndex(lock, result1, user, a.getElementId(), child1, lang);
						assertFalse(result1.hasErrors());
						assertEquals(IndexType.CCI_ALPHABETIC_INDEX, child1.getType());
						assertEquals(1, child1.getLevel());
					}
					{
						// fail to disable Index created in this year
						child1.setStatus(ConceptStatus.DISABLED);
						ErrorBuilder result1 = newErrorBuilder(child1);
						service.saveIndex(lock, result1, user, child1, lang);
						assertTrue(result1.hasErrors());
						assertNotNull(result1.getFieldError("status"));
					}
					// success to delete child index
					service.deleteIndexById(lock, user, child1.getElementId(), lang);
				}
			}
		}
		changeRequestService.deleteChangeRequest(changeRequest, user);
	}

	@Test
	@Ignore
	public void test_Index_ICD_VersionYear() throws Exception {
		String classification = ICD_BASE_CLASSIFICATION;

		ChangeRequestDTO changeRequest = createContext(classification, true, ChangeRequestCategory.I,
				asSet(Language.ENGLISH));
		User user = changeRequest.getOwner();
		OptimisticLock lock = new OptimisticLock(Long.MIN_VALUE);
		Language lang = changeRequest.getLanguages().iterator().next();
		{
			// test version year
			assertTrue(service.isVersionYear());
			// test permissions
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
			assertTrue(p.isCanAdd());
			assertTrue(p.isCanDelete());
			assertTrue(p.isCanWrite());
		}
		// section 1
		{
			IndexModel top = getIndexByDescription("Section I -- Alphabetic Index to Diseases and Nature of Injury",
					null, lang);
			{
				assertEquals(1, top.getSection());
				assertEquals(ConceptStatus.ACTIVE, top.getStatus());
				assertEquals(IndexType.ICD_BOOK_INDEX, top.getType());
			}
			IndexModel a = getIndexByDescription("A", top.getElementId(), lang);
			{
				assertEquals(1, a.getSection());
				assertEquals(ConceptStatus.ACTIVE, a.getStatus());
				assertEquals(IndexType.ICD_LETTER_INDEX, a.getType());
			}
			{
				// create ICD_ALPHABETIC_INDEX child level 1
				IndexModel child1 = new IndexModel();
				child1.setDescription("Aa");
				child1.setStatus(ConceptStatus.ACTIVE);

				ErrorBuilder result1 = newErrorBuilder(child1);
				service.createIndex(lock, result1, user, a.getElementId(), child1, lang);
				assertFalse(result1.hasErrors());
				assertEquals(IndexType.ICD_ALPHABETIC_INDEX, child1.getType());
				assertEquals(1, child1.getLevel());

				IndexModel _child1 = service.getIndexById(child1.getElementId(), lang);
				assertEquals(1, _child1.getLevel());
				assertEquals(child1.getType(), _child1.getType());

				// set Code Value Reference
				{
					child1 = service.getIndexById(child1.getElementId(), lang);
					List<IndexCategoryReferenceModel> irs = child1.getCategoryReferences();
					assertEquals(0, irs.size());
					{
						// add one
						List<SearchResultModel> codes = viewService.getSearchResults(classification,
								service.getCurrentContextId(), lang.getCode(), "code", null, "W94", 1);
						assertTrue(!codes.isEmpty());
						SearchResultModel code1 = codes.get(0);

						codes = viewService.getSearchResults(classification, service.getCurrentContextId(),
								lang.getCode(), "code", null, "Q87.1", 1);
						assertTrue(!codes.isEmpty());
						SearchResultModel code2 = codes.get(0);

						IndexCategoryReferenceModel mp = new IndexCategoryReferenceModel();
						mp.setMainElementId(Long.parseLong(code1.getConceptId()));
						mp.setMainCustomDescription(code1.getConceptId());
						mp.setMainCode(code1.getConceptCode());

						mp.setPairedElementId(Long.parseLong(code2.getConceptId()));
						mp.setPairedCustomDescription(code2.getConceptId());
						mp.setPairedCode(code2.getConceptCode());

						irs.add(mp);
					}

					ErrorBuilder result = newErrorBuilder(child1);
					service.saveIndex(lock, result, user, child1, lang);
					assertFalse(result.hasErrors());

					_child1 = service.getIndexById(child1.getElementId(), lang);
					irs = _child1.getCategoryReferences();
					assertEquals(1, irs.size());
					assertEquals(irs.get(0).getMainElementId() + "", irs.get(0).getMainCustomDescription());
				}
				{
					// create ICD_ALPHABETIC_INDEX child level 2
					IndexModel child2 = new IndexModel();
					child2.setDescription("Aa");
					child2.setStatus(ConceptStatus.ACTIVE);

					ErrorBuilder result2 = newErrorBuilder(child1);
					service.createIndex(lock, result2, user, child1.getElementId(), child2, lang);
					assertFalse(result2.hasErrors());
					assertEquals(IndexType.ICD_ALPHABETIC_INDEX, child2.getType());
				}
			}
		}
		// section 2
		{
			IndexModel top = getIndexByDescription("Section II -- External Causes of Injury Index", null, lang);
			{
				assertEquals(2, top.getSection());
				assertEquals(ConceptStatus.ACTIVE, top.getStatus());
				assertEquals(IndexType.ICD_BOOK_INDEX, top.getType());
			}
			IndexModel a = getIndexByDescription("A", top.getElementId(), lang);
			{
				assertEquals(2, a.getSection());
				assertEquals(ConceptStatus.ACTIVE, a.getStatus());
				assertEquals(IndexType.ICD_LETTER_INDEX, a.getType());
			}
			{
				// create EXTERNAL_INJURY child level 1
				IndexModel child1 = new IndexModel();
				child1.setDescription("Aa");
				child1.setStatus(ConceptStatus.ACTIVE);

				ErrorBuilder result1 = newErrorBuilder(child1);
				service.createIndex(lock, result1, user, a.getElementId(), child1, lang);
				assertFalse(result1.hasErrors());
				assertEquals(IndexType.ICD_EXTERNAL_INJURY_INDEX, child1.getType());
				assertEquals(1, child1.getLevel());

				IndexModel _child1 = service.getIndexById(child1.getElementId(), lang);
				assertEquals(1, _child1.getLevel());
				assertEquals(child1.getType(), _child1.getType());

				// set Code Value Reference
				{
					child1 = service.getIndexById(child1.getElementId(), lang);
					List<IndexCategoryReferenceModel> irs = child1.getCategoryReferences();
					assertEquals(0, irs.size());
					{
						// add one
						List<SearchResultModel> codes = viewService.getSearchResults(classification,
								service.getCurrentContextId(), lang.getCode(), "code", null, "W94", 1);
						assertTrue(!codes.isEmpty());
						SearchResultModel code1 = codes.get(0);

						IndexCategoryReferenceModel mp = new IndexCategoryReferenceModel();
						mp.setMainElementId(Long.parseLong(code1.getConceptId()));
						mp.setMainCustomDescription(code1.getConceptId());
						mp.setMainCode(code1.getConceptCode());

						irs.add(mp);
					}

					ErrorBuilder result = newErrorBuilder(child1);
					service.saveIndex(lock, result, user, child1, lang);
					assertFalse(result.hasErrors());

					_child1 = service.getIndexById(child1.getElementId(), lang);
					irs = _child1.getCategoryReferences();
					assertEquals(1, irs.size());
					assertEquals(irs.get(0).getMainElementId() + "", irs.get(0).getMainCustomDescription());
				}
				{
					// create EXTERNAL_INJURY child level 2
					IndexModel child2 = new IndexModel();
					child2.setDescription("Aa");
					child2.setStatus(ConceptStatus.ACTIVE);

					ErrorBuilder result2 = newErrorBuilder(child1);
					service.createIndex(lock, result2, user, child1.getElementId(), child2, lang);
					assertFalse(result2.hasErrors());
					assertEquals(IndexType.ICD_EXTERNAL_INJURY_INDEX, child2.getType());
				}
			}
		}
		// section 3
		{
			IndexModel top = getIndexByDescription("Section III -- Table of Drugs and Chemicals Index", null, lang);
			{
				assertEquals(3, top.getSection());
				assertEquals(ConceptStatus.ACTIVE, top.getStatus());
				assertEquals(IndexType.ICD_BOOK_INDEX, top.getType());
			}
			IndexModel a = getIndexByDescription("A", top.getElementId(), lang);
			{
				assertEquals(3, a.getSection());
				assertEquals(ConceptStatus.ACTIVE, a.getStatus());
				assertEquals(IndexType.ICD_LETTER_INDEX, a.getType());
			}
			{
				// create ICD_DRUGS_AND_CHEMICALS_INDEX child level 1
				IndexModel child1 = new IndexModel();
				child1.setDescription("Aa");
				child1.setStatus(ConceptStatus.ACTIVE);

				ErrorBuilder result1 = newErrorBuilder(child1);
				service.createIndex(lock, result1, user, a.getElementId(), child1, lang);
				assertFalse(result1.hasErrors());
				assertEquals(IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX, child1.getType());
				assertEquals(1, child1.getLevel());

				IndexModel _child1 = service.getIndexById(child1.getElementId(), lang);
				assertEquals(1, _child1.getLevel());
				assertEquals(child1.getType(), _child1.getType());

				// set Code Value Reference
				{
					child1 = service.getIndexById(child1.getElementId(), lang);
					Map<DrugDetailType, TabularReferenceModel> dd = child1.getDrugsDetails();
					assertEquals(DrugDetailType.values().length, dd.size());
					for (TabularReferenceModel m : dd.values()) {
						assertEquals(-1, m.getElementId());
					}
					{
						// add one
						List<SearchResultModel> codes = viewService.getSearchResults(classification,
								service.getCurrentContextId(), lang.getCode(), "code", null, "X68", 1);
						assertTrue(!codes.isEmpty());
						SearchResultModel code1 = codes.get(0);

						TabularReferenceModel mp = dd.get(DrugDetailType.ACCIDENTAL);
						mp.setElementId(Long.parseLong(code1.getConceptId()));
						mp.setCustomDescription(code1.getConceptId());
					}

					ErrorBuilder result = newErrorBuilder(child1);
					service.saveIndex(lock, result, user, child1, lang);
					assertFalse(result.hasErrors());

					_child1 = service.getIndexById(child1.getElementId(), lang);
					dd = _child1.getDrugsDetails();
					assertEquals(dd.get(DrugDetailType.ACCIDENTAL).getElementId() + "",
							dd.get(DrugDetailType.ACCIDENTAL).getCustomDescription());
				}
				{
					// create ICD_DRUGS_AND_CHEMICALS_INDEX child level 2
					IndexModel child2 = new IndexModel();
					child2.setDescription("Aa");
					child2.setStatus(ConceptStatus.ACTIVE);

					ErrorBuilder result2 = newErrorBuilder(child1);
					service.createIndex(lock, result2, user, child1.getElementId(), child2, lang);
					assertFalse(result2.hasErrors());
					assertEquals(IndexType.ICD_DRUGS_AND_CHEMICALS_INDEX, child2.getType());
				}
			}
		}
		// section 4
		{
			IndexModel top = getIndexByDescription("Section IV -- Table of Neoplasm Index", null, lang);
			{
				assertEquals(4, top.getSection());
				assertEquals(ConceptStatus.ACTIVE, top.getStatus());
				assertEquals(IndexType.ICD_BOOK_INDEX, top.getType());
			}
			IndexModel a = getIndexByDescription("Neoplasm, neoplastic (table)", top.getElementId(), lang);
			{
				assertEquals(4, a.getSection());
				assertEquals(null, a.getSiteIndicatorCode());
				assertEquals(ConceptStatus.ACTIVE, a.getStatus());
				assertEquals(IndexType.ICD_NEOPLASM_INDEX, a.getType());
				assertTrue(a.getIndexReferences().isEmpty());

				// add Index Term Reference
				{
					List<SearchResultModel> codes = viewService.getSearchResults(classification,
							service.getCurrentContextId(), lang.getCode(), "bookIndex", top.getElementId(),
							"xiphoid process", 1);
					assertTrue(!codes.isEmpty());
					SearchResultModel code1 = codes.get(0);

					IndexTermReferenceModel ref = new IndexTermReferenceModel();
					ref.setElementId(Long.parseLong(code1.getConceptId()));
					ref.setCustomDescription(code1.getLongDescription());

					List<IndexTermReferenceModel> refs = new ArrayList<IndexTermReferenceModel>();
					refs.add(ref);
					a.setIndexReferences(refs);

					ErrorBuilder result = newErrorBuilder(a);
					service.saveIndex(lock, result, user, a, lang);
					assertFalse(result.hasErrors());

					IndexModel _a = service.getIndexById(a.getElementId(), lang);
					assertEquals(1, _a.getIndexReferences().size());
				}
				// set Code Value Reference
				{
					a = service.getIndexById(a.getElementId(), lang);
					Map<NeoplasmDetailType, TabularReferenceModel> nd = a.getNeoplasmDetails();
					assertEquals(NeoplasmDetailType.values().length, nd.size());
					{
						// delete one
						TabularReferenceModel mp = nd.get(NeoplasmDetailType.MALIGNANT_PRIMARY);
						assertEquals(mp.getCustomDescription(), "C80.9");
						nd.remove(NeoplasmDetailType.MALIGNANT_PRIMARY);
					}
					{
						// rename one
						TabularReferenceModel mp = nd.get(NeoplasmDetailType.UU_BEHAVIOUR);
						assertEquals(mp.getCustomDescription(), "D48.9");
						mp.setCustomDescription(mp.getElementId() + "");
					}

					ErrorBuilder result = newErrorBuilder(a);
					service.saveIndex(lock, result, user, a, lang);
					assertFalse(result.hasErrors());

					IndexModel _a = service.getIndexById(a.getElementId(), lang);
					nd = _a.getNeoplasmDetails();
					assertEquals(-1, nd.get(NeoplasmDetailType.MALIGNANT_PRIMARY).getElementId());
					assertEquals(nd.get(NeoplasmDetailType.UU_BEHAVIOUR).getElementId() + "",
							nd.get(NeoplasmDetailType.UU_BEHAVIOUR).getCustomDescription());
				}
			}
			{
				// create ICD_NEOPLASM_INDEX child level 1
				IndexModel child1 = new IndexModel();
				child1.setDescription("Aa");
				child1.setStatus(ConceptStatus.ACTIVE);

				ErrorBuilder result1 = newErrorBuilder(child1);
				service.createIndex(lock, result1, user, a.getElementId(), child1, lang);
				assertFalse(result1.hasErrors());
				assertEquals(IndexType.ICD_NEOPLASM_INDEX, child1.getType());
				assertEquals(2, child1.getLevel());

				IndexModel _child1 = service.getIndexById(child1.getElementId(), lang);
				assertEquals(child1.getLevel(), _child1.getLevel());
				assertEquals(child1.getType(), _child1.getType());

				{
					// create ICD_NEOPLASM_INDEX child level 2
					IndexModel child2 = new IndexModel();
					child2.setDescription("Aa");
					child2.setStatus(ConceptStatus.ACTIVE);

					ErrorBuilder result2 = newErrorBuilder(child1);
					service.createIndex(lock, result2, user, child1.getElementId(), child2, lang);
					assertFalse(result2.hasErrors());
					assertEquals(IndexType.ICD_NEOPLASM_INDEX, child2.getType());
				}
			}
		}
		changeRequestService.deleteChangeRequest(changeRequest, user);
	}

	@Test
	public void test_Supplement_CCI_NonVersionYear() throws Exception {
		performSuplementOperationsNonVersionYear(SUPLEMENT_CCI_DESCRIPTION, CCI_BASE_CLASSIFICATION);
	}

	@Test
	public void test_Supplement_CCI_VersionYear() throws Exception {
		perfromSuplementOperationsVersionYear(SUPLEMENT_CCI_DESCRIPTION, CCI_BASE_CLASSIFICATION);
	}

	// @Ignore
	@Test
	public void test_Supplement_ICD_NonVersionYear() throws Exception {
		performSuplementOperationsNonVersionYear(SUPLEMENT_ICD_DESCRIPTION, ICD_BASE_CLASSIFICATION);
	}

	// @Ignore
	@Test
	public void test_Supplement_ICD_VersionYear() throws Exception {
		perfromSuplementOperationsVersionYear(SUPLEMENT_ICD_DESCRIPTION, ICD_BASE_CLASSIFICATION);
	}

	@Test
	public void test_Tabular_Info_NonVersionYear() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(false);
		OptimisticLock lock = new OptimisticLock(changeRequest.getLastUpdatedTime());
		User user = changeRequest.getOwner();
		TabularConceptModel model = assertIcdDataReadOnCategory();
		{
			// test version year
			assertFalse(service.isVersionYear());
			// test permissions: all except add/delete
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.T);
			assertFalse(p.isCanAdd());
			assertFalse(p.isCanDelete());
			assertTrue(p.isCanWrite());
			assertEquals(Language.ALL, p.getWriteLanguages());
		}
		{
			// check can edit user title: false
			assertFalse(service.isTabularStatusEditable(model));
			// try edit user title
			String userTitleEng = model.getUserTitleEng();
			String userTitleEngNew = userTitleEng + " test";
			model.setUserTitleEng(userTitleEngNew);
			ErrorBuilder result = newErrorBuilder(model);
			service.saveTabular(lock, result, user, model);
			// check we have errors
			assertTrue(result.hasErrors());
			assertEquals(1, result.getErrors().getErrorCount());
			assertEquals("Modification is not allowed",
					result.getErrors().getFieldError("userTitleEng").getDefaultMessage());
			// check user title was not changed
			model = service.getTabularConceptById(model.getElementId());
			assertEquals(userTitleEng, model.getUserTitleEng());
		}
		{
			// check no diagrams
			assertNull(model.getDiagramFra().getName());
			assertNull(model.getDiagramEng().getName());
			// check diagram editing: false
			MockMultipartFile file = newMultipartFile();
			model.getDiagramEng().setFile(file);
			ErrorBuilder result = newErrorBuilder(model);
			service.saveTabular(lock, result, user, model);
			// test errors
			assertTrue(result.hasErrors());
			assertEquals(1, result.getErrors().getErrorCount());
			assertEquals("Modification is not allowed",
					result.getErrors().getFieldError("diagramEng").getDefaultMessage());
			// test diagram name is NOT saved
			model = service.getTabularConceptById(model.getElementId());
			assertNull(model.getDiagramFra().getName());
			assertNull(model.getDiagramEng().getName());
		}
		{
			// check can edit long description: true
			String longTitleEng = model.getLongTitleEng();
			String longTitleEngNew = longTitleEng + " test";
			model.setLongTitleEng(longTitleEngNew);
			ErrorBuilder result = newErrorBuilder(model);
			service.saveTabular(lock, result, user, model);
			// test no errors
			assertFalse(result.hasErrors());
			// check long description is changed
			model = service.getTabularConceptById(model.getElementId());
			assertEquals(longTitleEngNew, model.getLongTitleEng());
		}
		{
			// fail to add concept
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			String newCategoryCode = model.getCode() + ".99";
			try {
				service.createTabular(lock, result, user, ICD_CATEGORY, model.getElementId(), newCategoryCode);
				fail("Exception expected");
			} catch (CIMSException ex) {
				assertEquals("Tabular can be added only in the version year", ex.getMessage());
			}
		}
		{
			// fail to delete concept
			try {
				service.deleteTabularById(lock, user, model.getElementId(), Language.ENGLISH);
				fail("Exception expected");
			} catch (CIMSException ex) {
				assertEquals("Tabular can not be deleted", ex.getMessage());
			}
		}
		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Info_NonVersionYear2() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(false);
		TabularConcept t = service.getTabularByCode("A00");
		t.setLongDescription("ENG", "A00 new long title");
		context.context().persist();
		t = service.getTabularByCode("A00");
		String longdesc = t.getLongDescription("ENG");
		// TabularConceptModel model = service.getTabularConceptById(t.getElementId());
		assertEquals("A00 new long title", longdesc);
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Info_ReferencedLinks() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(false);
		TabularConceptModel model = service.getTabularConceptByCode(ICD_CATEGORY_CODE);
		long currentContextId = service.getCurrentContextId();
		String baseClassification = changeRequest.getBaseClassification();
		// check indexes
		List<IndexBookReferencedLink> indexes = viewService.getIndexBookReferencedLinks(currentContextId,
				model.getCode(), baseClassification);
		assertTrue(indexes.isEmpty());
		// check tabulars
		List<TabularReferencedLink> tabulars = viewService.getTabularReferencedLinks(currentContextId, model.getCode(),
				baseClassification);
		assertTrue(tabulars.isEmpty());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Info_VersionYear() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(true);
		User user = changeRequest.getOwner();
		TabularConceptModel model = assertIcdDataReadOnCategory();
		OptimisticLock lock = new OptimisticLock(changeRequest.getLastUpdatedTime());
		{
			// test version year
			assertTrue(service.isVersionYear());
			// test permissions: all
			ChangeRequestPermission p = service.getConceptInfoPermission(user, ChangeRequestCategory.T);
			assertTrue(p.isCanAdd());
			assertTrue(p.isCanDelete());
			assertTrue(p.isCanWrite());
			assertEquals(Language.ALL, p.getWriteLanguages());
		}
		{
			// check can edit user title: true
			assertTrue(service.isTabularStatusEditable(model));
			// try edit user title
			String userTitleEngNew = model.getUserTitleEng() + " test";
			model.setUserTitleEng(userTitleEngNew);
			ErrorBuilder result = newErrorBuilder(model);
			service.saveTabular(lock, result, user, model);
			// test no errors
			assertFalse(result.hasErrors());
			// test user title was changed
			model = service.getTabularConceptById(model.getElementId());
			assertEquals(userTitleEngNew, model.getUserTitleEng());
		}
		/*
		 * if (false) { // check no diagrams assertNull(model.getDiagramEng().getName());
		 * assertNull(model.getDiagramFra().getName()); // check diagram editing MockMultipartFile file =
		 * newMultipartFile(); model.getDiagramEng().setFile(file); ErrorBuilder result = newErrorBuilder(model);
		 * service.saveTabular(lock, result, user, model); assertFalse(result.hasErrors()); // check diagram saved
		 * assertNull(service.getTabularDiagramContent(model.getElementId(), Language.FRENCH)); byte[] content =
		 * service.getTabularDiagramContent(model.getElementId(), Language.ENGLISH); assertTrue(Arrays.equals(content,
		 * file.getBytes())); // test diagram name is saved model = service.getTabularConceptById(model.getElementId());
		 * assertNull(model.getDiagramFra().getName()); assertEquals(file.getOriginalFilename(),
		 * model.getDiagramEng().getName()); }
		 */
		{
			// try add nested ICD_CATEGORY with invalid CODE
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.createTabular(lock, result, user, ICD_CATEGORY, model.getElementId(), "xxx");
			// check we have errors
			assertTrue(result.hasErrors());
			assertEquals(1, result.getErrors().getErrorCount());
			assertEquals("C##.# or C##.## format expected",
					result.getErrors().getFieldError("code").getDefaultMessage());
		}
		{
			// try add nested ICD_CATEGORY with valid CODE
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			String newCategoryCode = model.getCode() + ".99";
			long newCategoryElementId = service.createTabular(lock, result, user, ICD_CATEGORY, model.getElementId(),
					newCategoryCode);
			// check no errors
			assertFalse(result.hasErrors());
			// test new category data
			TabularConceptModel newCategory = service.getTabularConceptById(newCategoryElementId);
			assertNotNull(newCategory);
			assertEquals(newCategoryCode, newCategory.getCode());
			assertEquals(model.getElementId(), newCategory.getParent().getElementId());
			// try to delete newly created category
			service.deleteTabularById(lock, user, newCategoryElementId, Language.ENGLISH);
			// test deleted
			// assertNull(service.getTabularConceptById(newCategoryElementId));
		}
		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	@Ignore
	public void test_Tabular_Synchronization() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(true);
		List<ContentToSynchronize> contents = synchronizationService.getContentToSynchronize();
		assertEquals(1, contents.size());
		synchronizationService.synchronizeAsync(new OptimisticLock(changeRequest.getLastUpdatedTime().getTime()),
				getTestUser(), changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Validation_VersionYear() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(true);
		User user = changeRequest.getOwner();
		TabularConceptModel concept = assertIcdDataReadOnCategory();
		OptimisticLock lock = new OptimisticLock(changeRequest.getLastUpdatedTime());

		List<TabularConceptValidationDadHoldingModel> dataHoldings = service.getDataHoldings(Language.ENGLISH);
		long dataHoldingId = dataHoldings.get(0).getElementId();

		// --- lookup non-existing ----------------------------------------
		TabularConceptIcdValidationSetModel model = service.getTabularValidationSet(concept.getElementId(),
				dataHoldingId);
		assertNotNull(model);
		assertEquals(dataHoldingId, model.getElementId());
		assertEquals(0, model.getAgeMinimum());
		assertEquals(130, model.getAgeMaximum());
		assertEquals(true, model.isDisabled());
		assertEquals(null, model.getGenderCode());
		assertEquals(false, model.isNewBorn());
		assertEquals(null, model.getDxTypeId());

		List<TabularConceptValidationGenderModel> genders = service.getGenders(ENGLISH);
		TabularConceptValidationGenderModel gender = genders.get(0);
		List<DxType> dxTypes = service.getIcdDxTypes(ENGLISH);
		DxType dxType = dxTypes.get(0);

		// --- save new validation errors ---------------------------------------
		{
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.saveTabularValidationSet(lock, result, user, concept.getElementId(), model,
					Collections.<Long> emptyList());
			assertTrue(result.hasErrors());
			assertEquals(2, result.getErrors().getAllErrors().size());
			assertNotNull(result.getErrors().getFieldError("genderCode"));
			assertNotNull(result.getErrors().getFieldError("dxTypeId"));
		}

		// --- save: new ok ----------------------------------------
		{
			model.setGenderCode(gender.getCode());
			model.setDxTypeId(dxType.getId());

			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.saveTabularValidationSet(lock, result, user, concept.getElementId(), model,
					Collections.<Long> emptyList());
			assertFalse(result.hasErrors());

			TabularConceptIcdValidationSetModel model2 = service.getTabularValidationSet(concept.getElementId(),
					dataHoldingId);
			assertNotNull(model);
			assertEquals(model.getElementId(), model2.getElementId());
			assertEquals(model.getAgeMinimum(), model2.getAgeMinimum());
			assertEquals(model.getAgeMaximum(), model2.getAgeMaximum());
			assertEquals(model.isDisabled(), model2.isDisabled());
			assertEquals(model.getGenderCode(), model2.getGenderCode());
			assertEquals(model.isNewBorn(), model2.isNewBorn());
			assertEquals(model.getDxTypeId(), model2.getDxTypeId());
		}
		List<Long> otherDataHoldings = new ArrayList<Long>();
		for (TabularConceptValidationDadHoldingModel dh : dataHoldings) {
			if (dh.getElementId() != dataHoldingId) {
				otherDataHoldings.add(dh.getElementId());
			}
		}
		// save: check other validations for holdings do not exist
		{
			for (Long otherDataHoldingId : otherDataHoldings) {
				TabularConceptIcdValidationSetModel m = service.getTabularValidationSet(concept.getElementId(),
						otherDataHoldingId);
				assertNotNull(m);
				assertTrue(m.isDisabled());
			}
		}
		// save: extend to other data holdings
		{
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.saveTabularValidationSet(lock, result, user, concept.getElementId(), model, otherDataHoldings);
			assertFalse(result.hasErrors());
			for (Long otherDataHoldingId : otherDataHoldings) {
				TabularConceptIcdValidationSetModel m = service.getTabularValidationSet(concept.getElementId(),
						otherDataHoldingId);
				assertFalse(m.isDisabled());
				assertEquals(model.getGenderCode(), m.getGenderCode());
				assertEquals(model.isNewBorn(), m.isNewBorn());
				assertEquals(model.getDxTypeId(), m.getDxTypeId());
			}
		}
		// delete: validation for holding
		{
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.deleteTabularValidationSet(lock, user, concept.getElementId(), dataHoldingId);
			assertFalse(result.hasErrors());
			TabularConceptIcdValidationSetModel m = service.getTabularValidationSet(concept.getElementId(),
					dataHoldingId);
			assertNotNull(m);
			assertTrue(m.isDisabled());
		}

		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Xml_NonVersionYear() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(false);
		User user = changeRequest.getOwner();
		TabularConceptModel concept = assertIcdDataReadOnCategory();
		OptimisticLock lock = new OptimisticLock(changeRequest.getLastUpdatedTime());
		{
			// test version year
			assertFalse(service.isVersionYear());
			// test permissions: all
			ChangeRequestPermission p = service.getConceptNonInfoPermission(user);
			assertFalse(p.isCanAdd());
			assertFalse(p.isCanDelete());
			assertFalse(p.isCanWrite());
			assertEquals(Language.ALL, p.getWriteLanguages());
		}
		TabularConceptXmlModel model = service.getTabularXml(concept.getElementId(), TabularConceptXmlType.NOTE);
		model.setEnglishXml(model.getEnglishXml() + "  ");
		ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
		try {
			service.saveTabularXml(lock, result, user, model);
			fail("CIMSException expected");
		} catch (Exception ex) {
			assertTrue(ex instanceof CIMSException);
		}
		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	public void test_Tabular_Xml_VersionYear() throws Exception {
		ChangeRequestDTO changeRequest = createIcdTabularContext(true);
		User user = changeRequest.getOwner();
		TabularConceptModel concept = assertIcdDataReadOnCategory();
		OptimisticLock lock = new OptimisticLock(changeRequest.getLastUpdatedTime());
		{
			// test version year
			assertTrue(service.isVersionYear());
			// test permissions: all
			ChangeRequestPermission p = service.getConceptNonInfoPermission(user);
			assertTrue(p.isCanAdd());
			assertTrue(p.isCanDelete());
			assertTrue(p.isCanWrite());
			assertEquals(Language.ALL, p.getWriteLanguages());
		}
		TabularConceptXmlModel model1 = service.getTabularXml(concept.getElementId(), TabularConceptXmlType.DEFINITION);
		// save: invalid xml
		{
			model1.setEnglishXml("<qualifierlist type='definition'>ERROR</qualifierlist>");
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.saveTabularXml(lock, result, user, model1);
			assertTrue(result.hasErrors());
			assertEquals(1, result.getErrors().getErrorCount());
			assertNotNull(result.getErrors().getFieldError("englishXml"));
		}
		TabularConceptXmlModel model2 = service.getTabularXml(concept.getElementId(), TabularConceptXmlType.NOTE);
		// save: valid xml
		{
			String xmlEnglishNew = "<qualifierlist type='note'><note><label>Brain</label></note><note><label>Dementia</label></note></qualifierlist>";
			model2.setEnglishXml(xmlEnglishNew);
			ErrorBuilder result = new ErrorBuilder(new MapBindingResult(new HashMap<Object, Object>(), "model"));
			service.saveTabularXml(lock, result, user, model2);
			assertFalse(result.hasErrors());
			model2 = service.getTabularXml(concept.getElementId(), TabularConceptXmlType.NOTE);
			assertEquals(xmlEnglishNew, model2.getEnglishXml());
		}
		changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequest.getChangeRequestId());
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testChildren() {
		ChangeRequestDTO changeRequest = createCciTabularContext(true);
		User user = changeRequest.getOwner();
		OptimisticLock lock = new OptimisticLock(Long.MIN_VALUE);

		TabularConceptModel parentModel = service.getTabularConceptByCode("1.AA.13.^^");
		assertEquals(TabularConceptType.CCI_RUBRIC, parentModel.getType());
		long sectionId = service.getContainedPageId(parentModel.getElementId());
		long cciApproachTechniqueId = service.getSortedSectionApproachTechniqueComponents(sectionId, Language.ENGLISH)
				.get(0).getId();

		ErrorBuilder result = new ErrorBuilder(new BeanPropertyBindingResult(null, "model"));
		long cciCodeId = service.createTabularCciCode(lock, result, user, parentModel.getElementId(),
				cciApproachTechniqueId, null, null);

		CciTabular tabular = service.getTabularById(cciCodeId);
		CciTabular parent = (CciTabular) tabular.getParent();

		ContextAccess access = context.context();
		nonContextOperations.remove(access.getContextId(), tabular.getElementId());
		access.persist();
		access = access.reload();

		parent = access.load(parentModel.getElementId());
		for (CciTabular tab : parent.getSortedChildren()) {
			assertNotSame(tabular.getElementId(), tab.getElementId());
		}
		changeRequestService.deleteChangeRequest(changeRequest, getTestUser());
	}

}
