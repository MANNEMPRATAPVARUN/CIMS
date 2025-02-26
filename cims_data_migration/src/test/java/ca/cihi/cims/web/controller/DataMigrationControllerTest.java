package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.Constants;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.service.MigrationService;
import ca.cihi.cims.web.bean.KeyValueBean;

/**
 * @author wxing
 */
public class DataMigrationControllerTest {
    private static final Log LOGGER = LogFactory.getLog(DataMigrationControllerTest.class);
    
    private DataMigrationController controller;
    protected ApplicationContext context;
    private static final String FISCAL_YEAR_2009 = "2009";
    private static final String FISCAL_YEAR_2010 = "2010";
    
    @Mock
    private Model model;    
 
    @Mock
    private MigrationService migrationService;  
        
    @Mock
    private ContextProvider contextProvider;
    

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this); 
        controller = new DataMigrationController();
        controller.setMigrationService(migrationService);
        controller.setContextProvider(contextProvider);
    }
    

    /**
     * Test case for view setup form
     */
    @Test
    public void testSetupForm() {
        ModelAndView mav = controller.setupForm(model);
        assertSame(mav.getViewName(), "/migration/dataMigration");
    }
    
    
    /**
     * Test case for migrateData.
     */
    @Test
    public void testMigrateData(){
                
        // RunStatus is false
        when(migrationService.checkRunStatus(Constants.CLASSIFICATION_ICD10CA)).thenReturn(false);
        ModelAndView mav = controller.migrateData(model, FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA);
        assertSame(mav.getViewName(), "/migration/dataMigration");   
        
        //RunStatus is true        
        when(migrationService.checkRunStatus(Constants.CLASSIFICATION_ICD10CA)).thenReturn(true);
        mav = controller.migrateData(model, FISCAL_YEAR_2010, Constants.CLASSIFICATION_ICD10CA);
        assertSame(mav.getViewName(), "redirect:/migration.htm");
        
    }
    
    
    /**
     * Test case for viewMigrationReport
     */
    @Test
    public void testViewMigrationReport(){
        ModelAndView mav = controller.viewMigrationReport(model, FISCAL_YEAR_2009, "ICD-10-CA");
        assertSame(mav.getViewName(), "redirect:/migration.htm");
    }
    
    
    @Test
    public void testPopulateClassification(){
        LOGGER.info("Enter DataMigrationControllerTest.testPopulateClassification()");
        
        List<String> classifications = new ArrayList<String>();
        classifications.add("ICD-10-CA");
        classifications.add("CCI");
        classifications.add("ICD11");
        
        when(contextProvider.findBaseClassifications()).thenReturn(classifications);
        
        Collection<KeyValueBean> results = controller.populateClassification();
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        
        Assert.assertTrue(results.size() == 3);
    }

    
    @Test
    public void testGetMigrationService(){
        Assert.assertTrue(migrationService.equals(controller.getMigrationService()));
    }
    
    @Test
    public void testGetContextProvider(){
        Assert.assertTrue(contextProvider.equals(controller.getContextProvider()));
    }
}
