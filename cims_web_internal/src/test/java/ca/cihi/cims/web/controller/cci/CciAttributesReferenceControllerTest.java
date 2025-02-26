package ca.cihi.cims.web.controller.cci;

import static org.hamcrest.Matchers.equalTo;
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
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;

@SuppressWarnings( { "static-access", "unchecked" })
public class CciAttributesReferenceControllerTest {

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
	private CciAttributeType attributeType;
	@Mock
	private CciReferenceAttribute attribute;

	private final CciAttributesReferenceController controller = new CciAttributesReferenceController();

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

		when(contextProvider.findContext(any(ContextDefinition.class))).thenReturn(access);
		when(contextProvider.findContext(nullable(ContextIdentifier.class))).thenReturn(access);
		when(access.createChangeContext(nullable(Long.class))).thenReturn(access);
		when(access.findOne(any(Ref.class), any(FindCriterion.class))).thenReturn(attributeType);
		when(access.createWrapper(eq(CciReferenceAttribute.class), nullable(String.class), nullable(String.class))).thenReturn(
				attribute);

		BusinessKeyGenerator bkg = new BusinessKeyGenerator();
		bkg.setClassService(classService);
		bkg.registerInstance();
	}

	@Test
	public void testCreate() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		when(access.getContextId()).thenReturn(contextId);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes") //
						.param("code", attrs.getAttributeType()) //
						.param("mandatory", "mandatory") //
						.param("status", "status") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Test
	public void testCreateInvalid() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setFreezingStatus(FreezingStatus.ALL);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes") //
						.param("code", "code") //
						.param("status", "status") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"Code must start with the correct letter\",\"objectName\":\"\",\"field\":\"Attribute \",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null},{\"codes\":[\"NotEmpty.cciAttributeReferenceModel.mandatory\",\"NotEmpty.mandatory\",\"NotEmpty.java.lang.String\",\"NotEmpty\"],\"arguments\":[{\"codes\":[\"cciAttributeReferenceModel.mandatory\",\"mandatory\"],\"arguments\":null,\"defaultMessage\":\"mandatory\",\"code\":\"mandatory\"}],\"defaultMessage\":\"may not be empty\",\"objectName\":\"cciAttributeReferenceModel\",\"field\":\"mandatory\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":\"NotEmpty\"},{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"The CCI classification table package is being generated. Changes to generic attributes, reference values and in-context generic description are restricted.\",\"objectName\":\"\",\"field\":\"\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
	}

	@Ignore
	@Test
	public void testDelete() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		when(commonOperations.isConceptEligibleForRemoval(nullable(ContextIdentifier.class), nullable(ConceptVersion.class)))
				.thenReturn(true);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(delete("/referenceAttributes/1") //
						.param("code", "code") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}

	@Ignore
	@Test
	public void testDeleteInvalid() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setFreezingStatus(FreezingStatus.ALL);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(delete("/referenceAttributes/1") //
						.param("code", "code") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"Attribute is not eligible to be removed. \",\"objectName\":\"\",\"field\":\"Attribute\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null},{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"The CCI classification table package is being generated. Changes to generic attributes, reference values and in-context generic description are restricted.\",\"objectName\":\"\",\"field\":\"\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
	}

	@Test
	public void testRead() throws Exception {
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		attrs.setStatus("SOME");
		CciAttributeReferenceModel model1 = new CciAttributeReferenceModel();
		model1.setStatus(attrs.getStatus());
		final List<CciAttributeReferenceModel> values = new ArrayList<CciAttributeReferenceModel>();
		values.add(model1);
		values.add(new CciAttributeReferenceModel());
		when(auxService.getReferenceAttributesSQL(eq("CCI"), nullable(String.class), eq(attrs.getAttributeType())))
				.thenReturn(values);
		mockMvc.perform(get("/referenceAttributes") //
				.sessionAttr("cciAttributesForViewer", attrs)) //
				.andExpect(model().attribute("referenceAttributes", equalTo(Arrays.asList(values.get(0)))))//
				.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, 1));

//		.andExpect(model().attribute("referenceAttributes", new ArgumentMatcher<Object>() {
//
//			@Override
//			public boolean matches(Object arg0) {
//				List<CciAttributeReferenceModel> bean = (List<CciAttributeReferenceModel>) arg0;
//				assertEquals(values.get(0), bean.get(0));
//				return true;
//			}
//		}))//
//		.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, 1));
	
	}

	@Test
	public void testReadNote() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(access.getContextId()).thenReturn(contextId);
		when(lookupService.findContextIdentificationById(eq(100L))).thenReturn(contextId);

		CciAttributeReferenceModel model = new CciAttributeReferenceModel();
		when(auxService.getReferenceAttributeNoteDescription(any(Long.class), any(Long.class), any(Language.class)))
				.thenReturn("note");
		when(auxService.getReferenceAttribute(any(Long.class), any(Long.class))).thenReturn(model);

		mockMvc.perform(get("/referenceAttributes/1/note") //
				.param("contextId", "100") //
				.param("language", "ENG"))//
				.andExpect(model().attribute("language", "ENG"))//
				.andExpect(model().attribute("attrModel", model))//
				.andExpect(model().attribute("note", "note"));
	}

	@Ignore
	@Test
	public void testUpdate() throws Exception {
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
				.perform(post("/referenceAttributes/100") //
						.param("code", attrs.getAttributeType()) //
						.param("mandatory", "mandatory") //
						.param("status", "status") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"SUCCESS\",\"contextFrozen\":false,\"errorMessageList\":null}"));
	}
	
	@Ignore
	@Test
	public void testUpdateInvalid() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setFreezingStatus(FreezingStatus.ALL);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/referenceAttributes/1") //
						.param("code", "code") //
						.param("status", "status") //
						.sessionAttr("cciAttributesForViewer", attrs))
				//
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":[\"NotEmpty.attrModel.mandatory\",\"NotEmpty.mandatory\",\"NotEmpty.java.lang.String\",\"NotEmpty\"],\"arguments\":[{\"codes\":[\"attrModel.mandatory\",\"mandatory\"],\"arguments\":null,\"defaultMessage\":\"mandatory\",\"code\":\"mandatory\"}],\"defaultMessage\":\"may not be empty\",\"objectName\":\"attrModel\",\"field\":\"mandatory\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":\"NotEmpty\"},{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"The CCI classification table package is being generated. Changes to generic attributes, reference values and in-context generic description are restricted.\",\"objectName\":\"\",\"field\":\"\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
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
				.perform(post("/referenceAttributes/100/notes") //
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
