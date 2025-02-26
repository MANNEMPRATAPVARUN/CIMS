package ca.cihi.cims.web.controller.cci;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciAttribute;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;

@SuppressWarnings("unchecked")
public class CciAttributesReferenceInContextControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CciAuxService auxService;
	@Mock
	private LookupService lookupService;
	@Mock
	private ContextOperations operations;
	@Mock
	protected MessageSource messageSource;
	@Mock
	private DisplayTagUtilService dtService;
	@Mock
	private ContextProvider contextProvider;
	@Mock
	private ElementOperations elementOperations;
	@Mock
	private CommonElementOperations commonOperations;
	@Mock
	private NonContextOperations nonContextOperations;

	@Mock
	private ContextAccess access;
	@Mock
	private ClassService classService;
	@Mock
	private CciAttribute attribute;
	@Mock
	private CciAttributeType attributeType;
	@Mock
	private CciGenericAttribute genAttribute;
	@Mock
	private CciReferenceAttribute refAttribute;

	private final CciAttributesReferenceInContextController controller = new CciAttributesReferenceInContextController();

	// -----------------------------------------------------

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setAuxService(auxService);
		controller.setCommonOperations(commonOperations);
		controller.setContextProvider(contextProvider);
		controller.setDtService(dtService);
		controller.setElementOperations(elementOperations);
		controller.setLookupService(lookupService);
		controller.setNonContextOperations(nonContextOperations);
		controller.setOperations(operations);
		controller.setMessageSource(messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(contextProvider.findContext(nullable(ContextDefinition.class))).thenReturn(access);
		when(contextProvider.findContext(nullable(ContextIdentifier.class))).thenReturn(access);
		when(access.createChangeContext(nullable(Long.class))).thenReturn(access);
		when(refAttribute.getType()).thenReturn(attributeType);

		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(access.getContextId()).thenReturn(contextId);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);

		BusinessKeyGenerator bkg = new BusinessKeyGenerator();
		bkg.setClassService(classService);
		bkg.registerInstance();
	}

	@Test
	public void testCreate() throws Exception {
		when(access.load(999L)).thenReturn(refAttribute);
		when(
				access.findOne(any(Ref.class), any(FindCriterion.class), any(FindCriterion.class),
						any(FindCriterion.class))).thenReturn(genAttribute);
		when(access.createWrapper(eq(CciAttribute.class), eq("Attribute"), nullable(String.class))).thenReturn(attribute);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes/999/inContext") //
						.param("genericAttributeCode", attrs.getAttributeType()) //
						.param("descriptionEng", "eng") //
						.param("descriptionFra", "fra") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Ignore
	@Test
	public void testDelete() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setFreezingStatus(FreezingStatus.BLK);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		// invalid
		mockMvc
				.perform(delete("/referenceAttributes/999/inContext/888") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"Attribute is not eligible to be removed. \",\"objectName\":\"\",\"field\":\"Attribute\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
		// valid
		when(commonOperations.isConceptEligibleForRemoval(any(ContextIdentifier.class), nullable(ConceptVersion.class)))
				.thenReturn(true);
		mockMvc
				.perform(delete("/referenceAttributes/999/inContext/888") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Test
	public void testRead() throws Exception {
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		attrs.setStatus("SOME");

		CciAttributeReferenceModel referenceModel = new CciAttributeReferenceModel();
		List<CciAttributeReferenceInContextModel> inContextModels = new ArrayList<CciAttributeReferenceInContextModel>();
		CciAttributeReferenceInContextModel m1 = new CciAttributeReferenceInContextModel();
		m1.setGenericAttributeCode("REMOVE");
		inContextModels.add(m1);

		when(auxService.getReferenceAttributeType(eq(100L), eq(999L))).thenReturn("attributeType");
		when(auxService.getReferenceAttribute(eq(100L), eq(999l))).thenReturn(referenceModel);
		when(auxService.getReferenceAttributeInContextSQL(eq("CCI"), nullable(String.class), eq(999L))).thenReturn(
				inContextModels);

		List<CciAttributeGenericModel> cciGenericAttributes = new ArrayList<CciAttributeGenericModel>();
		final CciAttributeGenericModel ga1 = new CciAttributeGenericModel();
		ga1.setStatus("ACTIVE");
		ga1.setCode("ADD");
		cciGenericAttributes.add(ga1);
		CciAttributeGenericModel ga2 = new CciAttributeGenericModel();
		ga2.setStatus("DISABLED");
		ga2.setCode("ADD1");
		cciGenericAttributes.add(ga2);
		CciAttributeGenericModel ga3 = new CciAttributeGenericModel();
		ga3.setStatus("ACTIVE");
		ga3.setCode("REMOVE");
		cciGenericAttributes.add(ga3);

		when(auxService.getGenericAttributesSQL(eq("CCI"), nullable(String.class), nullable(String.class))).thenReturn(
				cciGenericAttributes);

		mockMvc.perform(get("/referenceAttributes/999/inContext") //
				.param("disableEditing", "false") //
				.param("versionCode", "2015") //
				.sessionAttr("cciAttributesForViewer", attrs)) //
				.andExpect(model().attribute("genericAttributeCodes", hasSize(1)))
				.andExpect(model().attribute("genericAttributeCodes", equalTo(Arrays.asList(ga1.getCode()))))
				.andExpect(model().attribute("hasConceptBeenPublished", false))//
				.andExpect(model().attribute("refAttrModel", referenceModel)) //
				.andExpect(model().attribute("inContextAttributes", inContextModels));
	}

	@Test
	public void testReadNote() throws Exception {
		CciAttributeGenericModel model = new CciAttributeGenericModel();
		model.setCode("code");
		model.setDescriptionEng("eng");
		CciAttributeReferenceModel referenceModel = new CciAttributeReferenceModel();
		when(auxService.getGenericAttribute(eq(100L), eq(888L))).thenReturn(model);
		when(auxService.getReferenceAttribute(eq(100L), eq(999l))).thenReturn(referenceModel);
		when(auxService.getAttributeNote(eq(100L), eq(888L), eq(Language.ENGLISH))).thenReturn("noteEnglish");
		when(auxService.getAttributeNote(eq(100L), eq(888L), eq(Language.FRENCH))).thenReturn("noteFrench");

		mockMvc.perform(get("/referenceAttributes/999/inContext/888/notes") //
				.param("versionCode", "2015") //
				.param("disableEditing", "false")) //
				.andExpect(model().attribute("attrModel", referenceModel))//
				.andExpect(model().attribute("genCode", model.getCode()))//
				.andExpect(model().attribute("descEng", model.getDescriptionEng()))//
				.andExpect(model().attribute("notesEng", "noteEnglish"))//
				.andExpect(model().attribute("notesFra", "noteFrench"));
	}

	@Ignore
	@Test
	public void testUpdate() throws Exception {
		when(access.load(888L)).thenReturn(attribute);
		when(
				access.findOne(any(Ref.class), any(FindCriterion.class), any(FindCriterion.class),
						any(FindCriterion.class))).thenReturn(genAttribute);
		when(access.createWrapper(eq(CciAttribute.class), eq("Attribute"), nullable(String.class))).thenReturn(attribute);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes/999/inContext/888") //
						.param("genericAttributeCode", attrs.getAttributeType()) //
						.param("descriptionEng", "eng") //
						.param("descriptionFra", "fra") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Test
	public void testUpdateNotes() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		when(access.getContextId()).thenReturn(contextId);
		when(access.load(anyLong())).thenReturn(attribute);

		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes/999/inContext/888/notes") //
						.param("ne", "ne") //
						.param("nf", "nf") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}
}
