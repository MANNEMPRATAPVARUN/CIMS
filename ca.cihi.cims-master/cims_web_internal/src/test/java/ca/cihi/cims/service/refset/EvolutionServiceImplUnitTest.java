package ca.cihi.cims.service.refset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.model.refset.BaseOutputContent;
import ca.cihi.cims.refset.concept.ColumnImpl;
import ca.cihi.cims.refset.concept.PickListImpl;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.util.RefsetExportUtils;

@PowerMockIgnore({"javax.management.", "com.sun.org.apache.xerces.", "javax.xml.", "org.xml.", "org.w3c.*"})
@PrepareForTest({RefsetFactory.class,Context.class, RefsetExportUtils.class})
@RunWith(PowerMockRunner.class)
@Ignore
public class EvolutionServiceImplUnitTest {
	
    private	EvolutionServiceImpl evolutionService;
	
    @Mock
    private RefsetImpl refset;
    
    @Before
	public void setup() {
		evolutionService = new EvolutionServiceImpl();
	}
	
	@Test
	public void testVerifyPicklistOutputConfig1() {
	    //Mocking static
	    PowerMockito.mockStatic(RefsetFactory.class);
		List<PicklistColumnOutputDTO> pColumnOutputList = new ArrayList<PicklistColumnOutputDTO>();
		PicklistColumnOutputDTO dto = new PicklistColumnOutputDTO();
		dto.setColumnId(1l);
		dto.setDisplayModeCode("aa");
		dto.setOrderNumber(1);
		dto.setParentPickListColumnOutputId(200);
		dto.setPickListColumnOutputId(300);
		dto.setPicklistOutputId(22);
		dto.setRefsetContextId(2222l);
		pColumnOutputList.add(dto);
	    PowerMockito.when(RefsetFactory.getPicklistColumnOutputConfig(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(pColumnOutputList);
	    assert(pColumnOutputList != null);
	    
	    ColumnImpl column = Mockito.mock(ColumnImpl.class);
	    PowerMockito.when(RefsetFactory.getColumn(Mockito.any(Long.class), Mockito.any(ElementIdentifier.class), Mockito.any(ConceptLoadDegree.class))).thenReturn(column);
	    
	    PowerMockito.when(RefsetFactory.getRefset((Long)anyObject(), (ElementIdentifier)anyObject(), (ConceptLoadDegree)anyObject())).thenReturn(refset);
		PowerMockito.when(column.getColumnType()).thenReturn("CIMS ICD-10-CA Code");
       
        PowerMockito.mockStatic(Context.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(Context.findById((Long)anyObject())).thenReturn(context);
        PowerMockito.when(context.getBaseContextId()).thenReturn(1l);
        
		assertFalse(evolutionService.verifyPicklistOutputConfig(1l, 2l, 3l, 4l));		
	}
	
	@Test
	public void testVerifyPicklistOutputConfig2() {
	    //Mocking static
	    PowerMockito.mockStatic(RefsetFactory.class);
		List<PicklistColumnOutputDTO> pColumnOutputList = new ArrayList<PicklistColumnOutputDTO>();
		PicklistColumnOutputDTO dto = new PicklistColumnOutputDTO();
		dto.setColumnId(1l);
		dto.setDisplayModeCode("aa");
		dto.setOrderNumber(1);
		dto.setParentPickListColumnOutputId(200);
		dto.setPickListColumnOutputId(300);
		dto.setPicklistOutputId(22);
		dto.setRefsetContextId(2222l);
		pColumnOutputList.add(dto);
	    PowerMockito.when(RefsetFactory.getPicklistColumnOutputConfig(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(pColumnOutputList);
	    assert(pColumnOutputList != null);
	    
	    ColumnImpl column = Mockito.mock(ColumnImpl.class);
	    PowerMockito.when(RefsetFactory.getColumn(Mockito.any(Long.class), Mockito.any(ElementIdentifier.class), Mockito.any(ConceptLoadDegree.class))).thenReturn(column);
	    
	    PowerMockito.when(RefsetFactory.getRefset((Long)anyObject(), (ElementIdentifier)anyObject(), (ConceptLoadDegree)anyObject())).thenReturn(refset);
	    List<PickList> picklists = new ArrayList<PickList>();
	    PickListImpl pickList = Mockito.mock(PickListImpl.class);	
	    ElementIdentifier ei = new ElementIdentifier();
	    ei.setElementId(4l);
	    ei.setElementVersionId(2l);
	    PowerMockito.when(pickList.getElementIdentifier()).thenReturn(ei);;
	    picklists.add(pickList);
		PowerMockito.when(refset.listPickLists()).thenReturn(picklists);
		PowerMockito.when(column.getColumnType()).thenReturn("CIMS ICD-10-CA Code");
       
        PowerMockito.mockStatic(Context.class);
        Context context = Mockito.mock(Context.class);
        Mockito.when(Context.findById((Long)anyObject())).thenReturn(context);
        PowerMockito.when(context.getBaseContextId()).thenReturn(1l);
        
		assertTrue(evolutionService.verifyPicklistOutputConfig(1l, 2l, 3l, 4l));
		
	}
	
	@Test
	public void testGetPicklistColumnEvolutionContent(){
		 PowerMockito.mockStatic(RefsetFactory.class);
		 PicklistOutputDTO pickListOutput = Mockito.mock(PicklistOutputDTO.class);
		 Mockito.when(pickListOutput.getLanguageCode()).thenReturn(Language.ENG.getCode());
		 PowerMockito.when(RefsetFactory.getPicklistOutputConfigByOutputId((Integer)anyObject())).thenReturn(pickListOutput);
		 List<PicklistColumnConfigEvolutionDTO> columnConfigList = new ArrayList<PicklistColumnConfigEvolutionDTO>(); 
		 PicklistColumnConfigEvolutionDTO dto = new PicklistColumnConfigEvolutionDTO();
		 dto.setColumnId(1l);
		 dto.setColumnName("TestColumn");
		 dto.setStatus("Active");
		 columnConfigList.add(dto);
		 PowerMockito.when(RefsetFactory.getPicklistColumnConfigEvolutionList((PicklistColumnEvolutionRequestDTO)anyObject())).thenReturn(columnConfigList);		 
		 PowerMockito.mockStatic(RefsetExportUtils.class);
		 PowerMockito.when(RefsetExportUtils.getTitleNameByLanguageCode((String)anyObject())).thenReturn("TestingTitleTabEng");		 
		 PowerMockito.when(RefsetExportUtils.getTitleDescriptionByLanguageCode((String)anyObject())).thenReturn("NUMOFSHEETS testing only");
		 PowerMockito.when(RefsetExportUtils.getTableContentByLanguageCode((String)anyObject())).thenReturn("TableContentTest");
		 PicklistColumnEvolutionRequestDTO request = new PicklistColumnEvolutionRequestDTO();
		 request.setBaseRefsetContextId(1l);
		 request.setBaseVersionCode("v1.0");
		 request.setCciContextId(2l);
		 request.setIcd10caContextId(3l);
		 request.setPicklistElementId(4l);
		 request.setPicklistOutputId(5l);
		 request.setRefsetContextId(6l);
		 request.setVersionCode("v1.1");
		 BaseOutputContent outputContent =  evolutionService.getPicklistColumnEvolutionContent(request);
		 assertTrue(outputContent.getOutputType().equals("PicklistColumnEvolution"));
	}
	
}
