package ca.cihi.cims.transformation;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ca.cihi.cims.model.TransformationError;

/**
 * To report errors that are catched during parsing xml.
 * 
 * @author WXing
 */
public class XmlParserErrorHandler implements ErrorHandler {
    private static final Log LOGGER = LogFactory.getLog(XmlParserErrorHandler.class);
    
    private final List<TransformationError> errors;
    
    public XmlParserErrorHandler(final List<TransformationError> errors){
        this.errors = errors;
    }

    public void warning(final SAXParseException exception)
        throws SAXException {
        LOGGER.error("Warning at line "
                         + exception.getLineNumber()
                         + ": ");
        LOGGER.error(exception.getMessage());
        errors.add(new TransformationError("", "", "", "", exception.getMessage(), ""));
    }

    public void error(final SAXParseException exception)
        throws SAXException {

        LOGGER.error("Error at line "
                         + exception.getLineNumber()
                         + ": ");

        LOGGER.error(exception.getMessage());
        errors.add(new TransformationError("", "", "", "", exception.getMessage(),""));

    }

    public void fatalError(final SAXParseException exception)
        throws SAXException {

        LOGGER.error("Fatal error at line "
                         + exception.getLineNumber()
                         + ": ");

        LOGGER.error(exception.getMessage());
        errors.add(new TransformationError("", "", "", "", exception.getMessage(), ""));

    }

    
    public List<TransformationError> getErrors() {
        return errors;
    }

}
