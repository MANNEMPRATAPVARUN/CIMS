package ca.cihi.cims.web.controller.cci;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
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
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
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
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.LookupService;

@SuppressWarnings( { "static-access", "unchecked" })
public class CciAttributesGenericControllerTest {

	private MockMvc mockMvc;

	@Mock
	private CciAuxService auxService;
	@Mock
	private DisplayTagUtilService dtService;
	@Mock
	private ElementOperations elementOperations;
	@Mock
	private ContextOperations contextOperations;
	@Mock
	private CommonElementOperations commonOperations;
	@Mock
	private NonContextOperations nonContextOperations;

	@Mock
	private LookupService lookupService;
	@Mock
	private ContextProvider contextProvider;
	@Mock
	protected MessageSource messageSource;

	@Mock
	private ContextAccess access;
	@Mock
	private ClassService classService;
	@Mock
	private CciAttributeType attributeType;
	@Mock
	private CciGenericAttribute genAttribute;
	@Mock
	private CciReferenceAttribute refAttribute;

	@Mock
	private CciGenericAttribute genericAttribute;

	private final CciAttributesGenericController controller = new CciAttributesGenericController();

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
		controller.setContextOperations(contextOperations);
		controller.setMessageSource(messageSource);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysExpect(status().isOk()).build();

		when(contextProvider.findContext(any(ContextDefinition.class))).thenReturn(access);
		when(contextProvider.findContext(any(ContextIdentifier.class))).thenReturn(access);
		when(access.createChangeContext(nullable(Long.class))).thenReturn(access);
		when(refAttribute.getType()).thenReturn(attributeType);
		when(access.findOne(any(Ref.class), any(FindCriterion.class))).thenReturn(attributeType);
		when(access.createWrapper(eq(CciGenericAttribute.class), nullable(String.class), nullable(String.class))).thenReturn(
				genericAttribute);

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
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");

		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(access.getContextId()).thenReturn(contextId);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		
		mockMvc
				.perform(post("/genericAttributes") //
						.param("code", attrs.getAttributeType()) //
						.param("status", "ACTIVE") //
						.param("descriptionEng", "descriptionEng") //
						.param("descriptionFra", "descriptionFra") //
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
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), nullable(String.class))).thenReturn(
				contextId);
		when(commonOperations.isConceptEligibleForRemoval(any(ContextIdentifier.class), nullable(ConceptVersion.class)))
				.thenReturn(true);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(delete("/genericAttributes/1") //
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
				.perform(delete("/genericAttributes/1") //
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
		CciAttributeGenericModel model1 = new CciAttributeGenericModel();
		model1.setStatus(attrs.getStatus());
		final List<CciAttributeGenericModel> values = new ArrayList<CciAttributeGenericModel>();
		values.add(model1);
		values.add(new CciAttributeGenericModel());
		when(auxService.getGenericAttributesSQL(eq("CCI"), nullable(String.class), eq(attrs.getAttributeType())))
				.thenReturn(values);
		mockMvc.perform(get("/genericAttributes") //
				.sessionAttr("cciAttributesForViewer", attrs))
				.andExpect(model().attribute("genericAttributes",     contains(
						hasProperty("status", equalTo(model1.getStatus()))
			    	)	
				))
				.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, 1));
	}

	@Test
	public void testReadReferences() throws Exception {
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		attrs.setStatus("SOME");
		final List<CciAttributeGenericRefLink> values = new ArrayList<CciAttributeGenericRefLink>();
		values.add(new CciAttributeGenericRefLink());
		values.add(new CciAttributeGenericRefLink());
		when(auxService.getGenericAttributeReferencesSQL(eq("CCI"), nullable(String.class), eq(1L), eq("c"))).thenReturn(
				values);
		mockMvc.perform(get("/genericAttributes/1/references") //
				.param("c", "c") //
				.sessionAttr("cciAttributesForViewer", attrs)) //
				.andExpect(model().attribute("attributes", equalTo(values)))
				.andExpect(model().attribute(controller.MODEL_KEY_RESULT_SIZE, values.size()));
	}

	@Ignore
	@Test
	public void testUpdate() throws Exception {
		ContextIdentifier contextId = new ContextIdentifier();
		contextId.setContextId(100);
		contextId.setFreezingStatus(FreezingStatus.BLK);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(eq("CCI"), any(String.class))).thenReturn(
				contextId);
		when(access.getContextId()).thenReturn(contextId);
		when(access.load(anyLong())).thenReturn(genAttribute);
		CciAttributes attrs = new CciAttributes();
		attrs.setAttributeType("xxx");
		mockMvc
				.perform(post("/genericAttributes/100") //
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

		//ra.andDo(MockMvcResultHandlers.print());
		mockMvc.perform(post("/genericAttributes/1") //
						.param("code", "code") //
						.param("status", "status") //
						.sessionAttr("cciAttributesForViewer", attrs))
				.andExpect(
						content()
								.string(
										"{\"value\":null,\"status\":\"FAIL\",\"contextFrozen\":false,\"errorMessageList\":[{\"codes\":null,\"arguments\":null,\"defaultMessage\":\"The CCI classification table package is being generated. Changes to generic attributes, reference values and in-context generic description are restricted.\",\"objectName\":\"\",\"field\":\"\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":null}]}"));
	}

}
