package ca.cihi.cims.web.controller.refset;


import static org.mockito.ArgumentMatchers.nullable;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.ui.Model;

import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.model.refset.BaseOutputContent;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.service.refset.EvolutionService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetExportUtils;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

@PrepareForTest({RefsetExportUtils.class})
@RunWith(PowerMockRunner.class)
public class EvolutionControllerTest {
	
	private EvolutionController controller;
	
	@Mock
	private RefsetService refsetService;
	@Mock
	private EvolutionService evolutionService;
	@Mock
	private RefsetImpl refset;
	
	private RefsetConfigDetailBean viewBean;
	
	@Mock
	private Model model;
	@Mock
	private HttpServletResponse response;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new EvolutionController();
		controller.setRefsetService(refsetService);
		controller.setEvolutionService(evolutionService);
	}
	
	@Test
	@Ignore
	public void testGenerateEvolution() throws Exception {
		
		BaseOutputContent baseOutputContent = Mockito.mock(BaseOutputContent.class);
		PowerMockito.when(refsetService.getRefset(nullable(Long.class), nullable(Long.class), nullable(Long.class))).thenReturn(refset);
		PowerMockito.when(refsetService.getVersionCode(nullable(Long.class))).thenReturn("1.0");
		PowerMockito.when(evolutionService.getPicklistColumnEvolutionContent(nullable(PicklistColumnEvolutionRequestDTO.class))).thenReturn(baseOutputContent);
		
		PowerMockito.when(refset.getVersionCode()).thenReturn("v1.0");
		Context context =  Mockito.mock(Context.class);
		PowerMockito.when(refset.getContext()).thenReturn(context);
		PowerMockito.when(context.getBaseContextId()).thenReturn(1l);
		PowerMockito.when(refset.getCCIContextId()).thenReturn(20l);
		PowerMockito.when(refset.getICD10CAContextId()).thenReturn(30l);
		PowerMockito.mockStatic(RefsetExportUtils.class);
		PowerMockito.doNothing().when(RefsetExportUtils.class,"outputExcel",baseOutputContent,response);
		controller.generateEvolution(1l, 1l, 1l, 1l, 1l, model, response);
		PowerMockito.verifyStatic(RefsetExportUtils.class);
		RefsetExportUtils.outputExcel(baseOutputContent,response);
	}
	
}
