package ca.cihi.cims.web.controller;

import static org.junit.Assert.assertSame;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.service.MigrationService;

/**
 * @author wxing
 */
public class ContextCreationControllerTest {
    private ContextCreationController controller;
    protected ApplicationContext context;
        
    @Mock
    private Model model;    
 
    @Mock
    private MigrationService migrationService;  
    
    

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);     
        controller = new ContextCreationController();
        controller.setMigrationService(migrationService);
    }
    

    /**
     * Test case for setupForm
     */
    @Test
    public void testSetupForm() {
        
        ModelAndView mav = controller.setupForm(model);
        assertSame(mav.getViewName(), "/migration/createContext");        
    } 
  
    
    
    @Test
    public void testGetMigrationService(){
        Assert.assertTrue(migrationService.equals(controller.getMigrationService()));
    }
    


}

