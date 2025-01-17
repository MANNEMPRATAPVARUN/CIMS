package ca.cihi.cims.web.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.bll.ContextProvider;

/**
 * @author wxing
 */
public class CIMSAjaxControllerTest {
    private static final Log LOGGER = LogFactory.getLog(CIMSAjaxControllerTest.class);
    
    private CIMSAjaxController controller;
     
    @Mock
    private ContextProvider contextProvider; 
    

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this); 
        controller = new CIMSAjaxController();
        controller.setContextProvider(contextProvider);
    }

    
    @Test
    public void testGetVersionForClassification(){
        LOGGER.info("Enter CIMSAjaxControllerTest.testGetVersionForClassification()");
        
        List<String> versions = new ArrayList<String>();
        versions.add("2009");
        versions.add("2010");
        versions.add("2012");
        
        when(contextProvider.findVersionCodes("ICD-10-CA")).thenReturn(versions);
        
        Map<String, String> results = controller.getVersionForClassification("ICD-10-CA");
        Assert.assertNotNull(results);
        Assert.assertFalse(results.isEmpty());
        
        Assert.assertTrue(results.keySet().size() == 3);
    }
    
    @Test
    public void testGetContextProvider(){
        Assert.assertTrue(contextProvider.equals(controller.getContextProvider()));
    }

}


