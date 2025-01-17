package ca.cihi.cims.transformation;

import java.util.ArrayList;
import java.util.List;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ca.cihi.cims.model.TransformationError;


/**
 * Test class of XmlParserErrorHandler
 * 
 * @author wxing
 *
 */
public class XmlParserErrorHandlerTest {
    
    private XmlParserErrorHandler errorHandler;
    
    @Before
    public void setUp()
    {
       List<TransformationError> errors = new ArrayList<TransformationError>();
       errorHandler = new XmlParserErrorHandler(errors);
    }
    
    @Test
    public void testWarning() throws SAXException{    
        String message = "This is a warning!";
        SAXParseException exception = new SAXParseException(message, null);        
        errorHandler.warning(exception);
        List<TransformationError> errors = errorHandler.getErrors();
        Assert.assertTrue(errors.size() == 1);
        Assert.assertTrue(errors.get(0).getErrorMessage().equalsIgnoreCase(message));
        
    }
    
    @Test
    public void testError() throws SAXException{
        String message = "This is an error!";
        SAXParseException exception = new SAXParseException(message, null);        
        errorHandler.error(exception);
        List<TransformationError> errors = errorHandler.getErrors();
        Assert.assertTrue(errors.size() == 1);
        Assert.assertTrue(errors.get(0).getErrorMessage().equalsIgnoreCase(message));
        
    }
    
    @Test
    public void testFatalError() throws SAXException{
        String message = "This is a fatal error!";
        SAXParseException exception = new SAXParseException(message, null);        
        errorHandler.fatalError(exception);
        List<TransformationError> errors = errorHandler.getErrors();
        Assert.assertTrue(errors.size() == 1);
        Assert.assertTrue(errors.get(0).getErrorMessage().equalsIgnoreCase(message));        
    }
}
