package ca.cihi.cims.web.controller.changerequest.legacy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.model.changerequest.legacy.LegacyRequestDetailModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.legacy.LegacyRequestService;
import ca.cihi.cims.web.controller.changerequest.legacy.LegacyRequestController;

/**
 * @author ylu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class LegacyRequestControllerTest {

	protected static final String LEGACY_CHANGEREQUEST_VIEW = "legacyChangeRequests";
	protected static final String LEGACY_CHANGEREQUEST_DETAIL_VIEW = "/requestmanagement/legacy/legacyChangeRequestDetail";
	
	LegacyRequestController legacyRequestController;

	@Mock
	protected Model model;

	@Mock
	protected ModelMap modelMap;

	@Mock
	protected HttpServletRequest request;

	@Mock
	protected HttpSession session;

	@Mock
	HttpServletResponse response;



	@Mock
	protected BindingResult result;

	@Mock
	private LegacyRequestService legacyRequestService;
	
	@Mock
	private DisplayTagUtilService dtService;
	
	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		legacyRequestController = new LegacyRequestController();
        legacyRequestController.setLegacyRequestService(legacyRequestService);
        legacyRequestController.setDtService(dtService);

		when(legacyRequestService.findLegacyChangeRequestByRequestId(1L)).thenReturn(mockLegacyChangeRequestDetail());
		when(legacyRequestService.findLegacyChangeRequestAttachmentsByRequestId(1L)).thenReturn(mockLegacyChangeRequestAttachments());
		when(legacyRequestService.findLegacyChangeRequestQueryRefNumsByRequestId(1L)).thenReturn(mockLegacyChangeRequestQueryRefNums());

		LegacyRequestSearchModel legacyRequestSearchModel = new LegacyRequestSearchModel();
        List<String> versionCodes = new ArrayList<String>();
        versionCodes.add("2008");
		legacyRequestSearchModel.setVersionCodes(versionCodes);
		legacyRequestSearchModel.setLanguageCode("ENG");
		legacyRequestSearchModel.setClassificationTitleCode("CCI");
		when(legacyRequestService.findLegacyChangeRequestsBySearchModel(legacyRequestSearchModel)).thenReturn(mockLegacyRequestResultsModel());
		when(legacyRequestService.findNumOfLegacyChangeRequestsBySearchModel(legacyRequestSearchModel)).thenReturn(Integer.parseInt("1"));


	}

	@Test
	public void testloadLegacySearch(){
		String viewModal = legacyRequestController.loadLegacySearch(request, session, modelMap);
		String expectedMav = LEGACY_CHANGEREQUEST_VIEW;
	    assertEquals("Should get same view", viewModal, expectedMav);
	}

	
	@Test
	public void testLoadLegacyResultDetail(){
		String viewModal = legacyRequestController.loadLegacyResultDetail(model, 1l, request, session);
        String expectedMav = LEGACY_CHANGEREQUEST_DETAIL_VIEW;
	    assertEquals("Should get same view", viewModal, expectedMav);
	}

	
	@Test
	public void testRunLegacySearch(){
		LegacyRequestSearchModel legacyRequestSearchModel = new LegacyRequestSearchModel();
		String viewModal = legacyRequestController.runLegacySearch(request, session, legacyRequestSearchModel, result, modelMap);
        String expectedMav = LEGACY_CHANGEREQUEST_VIEW;
	    assertEquals("Should get same view", viewModal, expectedMav);
	}
		
	private List<LegacyRequestDetailModel> mockLegacyChangeRequestDetail() {
		LegacyRequestDetailModel legacyRequestDetailModel = new LegacyRequestDetailModel();
		legacyRequestDetailModel.setRequestId(1l);
		legacyRequestDetailModel.setClassificationTitleCode("CCI");
		legacyRequestDetailModel.setVersionCode("2008");
        List<LegacyRequestDetailModel> list = new ArrayList<LegacyRequestDetailModel>();
        list.add(legacyRequestDetailModel);
		return list;
	}

	private List<String> mockLegacyChangeRequestAttachments() {
        List<String> list = new ArrayList<String>();
        list.add("attachment_1");
        list.add("attachment_2");
		return list;
	}

	private List<String> mockLegacyChangeRequestQueryRefNums() {
        List<String> list = new ArrayList<String>();
        list.add("query_ref_num_1");
        list.add("query_ref_num_2");
		return list;
	}

	private List<LegacyRequestResultsModel> mockLegacyRequestResultsModel() {
		LegacyRequestResultsModel legacyRequestResultsModel = new LegacyRequestResultsModel();
		legacyRequestResultsModel.setRequestId(1L);
		legacyRequestResultsModel.setVersionCode("2008");
		legacyRequestResultsModel.setClassificationTitleCode("CCI");
		legacyRequestResultsModel.setLanguage("English");
		
		List<LegacyRequestResultsModel> list = new ArrayList<LegacyRequestResultsModel>();
        list.add(legacyRequestResultsModel);
		return list;
	}

}
