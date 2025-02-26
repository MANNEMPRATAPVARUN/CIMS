package ca.cihi.cims.web.bean;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.TransformationError;


public class IndexTransformationViewBeanTest {
    
    private IndexTransformationViewBean viewBean;
    
    @Before
    public void setUp()
    {
       viewBean = new IndexTransformationViewBean();
    }
    
    @Test
    public void testSetGetClassification(){
        String classification = "CCI";
        Assert.assertNull(viewBean.getClassification());
        
        viewBean.setClassification(classification);
        Assert.assertTrue(viewBean.getClassification().equalsIgnoreCase(classification));
    }
    
    @Test
    public void testSetGetFiscalYear(){
        String version = "2009";
        Assert.assertNull(viewBean.getFiscalYear());
        
        viewBean.setFiscalYear(version);
        Assert.assertTrue(viewBean.getFiscalYear().equalsIgnoreCase(version));
    }
    
    @Test
    public void testSetGetBookIndexType(){
        String bookIndexType = "A";
        Assert.assertNull(viewBean.getBookIndexType());
        
        viewBean.setBookIndexType(bookIndexType);
        Assert.assertTrue(bookIndexType.equalsIgnoreCase(viewBean.getBookIndexType()));        
    }
    
    @Test
    public void testSetGetLanguage(){
        String language = "ENG";
        Assert.assertNull(viewBean.getLanguage());
        
        viewBean.setLanguage(language);
        Assert.assertTrue(language.equalsIgnoreCase(viewBean.getLanguage()));        
    }  
    
    @Test
    public void testSetGetErrorList(){
        List<TransformationError> errors = new ArrayList<TransformationError>();
        Assert.assertNull(viewBean.getErrorList());
        
        viewBean.setErrorList(errors);
        Assert.assertTrue(viewBean.getErrorList().equals(errors));
    }
}
