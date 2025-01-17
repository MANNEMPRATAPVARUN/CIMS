package ca.cihi.cims.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class of TransformationError
 * 
 * @author wxing
 *
 */
public class TransformationErrorTest {
    
    private TransformationError error;
    
    @Before
    public void setUp()
    {
       error = new TransformationError();
    }
    
    @Test
    public void testGetSetClassification(){
        String classification = "CCI";
        Assert.assertTrue(error.getClassification().equals(""));
        
        error.setClassification(classification);
        Assert.assertTrue(error.getClassification().equalsIgnoreCase(classification));
    }
    
    @Test
    public void testGetSetVersion(){
        String version = "2009";
        Assert.assertTrue(error.getVersion().equals(""));
        
        error.setVersion(version);
        Assert.assertTrue(error.getVersion().equalsIgnoreCase(version));
    }
    
    @Test
    public void testGetSetConceptCode(){
        String conceptCode = "A15.0";
        Assert.assertTrue(error.getConceptCode().equals(""));
        
        error.setConceptCode(conceptCode);
        Assert.assertTrue(error.getConceptCode().equalsIgnoreCase(conceptCode));
    }
    
    @Test
    public void testGetSetConceptTypeCode(){
        String conceptTypeCode = "code";
        Assert.assertTrue(error.getConceptTypeCode().equals(""));
        
        error.setConceptTypeCode(conceptTypeCode);
        Assert.assertTrue(error.getConceptTypeCode().equalsIgnoreCase(conceptTypeCode));
    }
    
    @Test
    public void testGetSetXmlString(){
        String xmlString = "<a>test</a>";
        Assert.assertTrue(error.getXmlString().equals(""));
        
        error.setXmlString(xmlString);
        Assert.assertTrue(error.getXmlString().equalsIgnoreCase(xmlString));
    }    
    
    @Test
    public void testGetSetCreateDate(){
        String createDate = "01/01/2000";
        Assert.assertNull(error.getCreateDate());
        
        error.setCreateDate(createDate);
        Assert.assertTrue(error.getCreateDate().equalsIgnoreCase(createDate));
    }
    
    
    @Test
    public void testGetSetErrorMessage(){
        String errorMessage = "This is an error!";
        Assert.assertTrue(error.getErrorMessage().equals(""));
        
        error.setErrorMessage(errorMessage);
        Assert.assertTrue(error.getErrorMessage().equalsIgnoreCase(errorMessage));
    }
    
    @Test
    public void testGetErrorId(){
        Assert.assertNull(error.getErrorId());
    }

}
