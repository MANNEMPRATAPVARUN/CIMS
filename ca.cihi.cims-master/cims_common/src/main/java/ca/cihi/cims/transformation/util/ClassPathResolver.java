package ca.cihi.cims.transformation.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * A custom EntityResolver for the JAXP compliant parser, that intercepts the request for the DTD and loads it from the
 * classpath instead.
 * 
 * @author wxing
 */

public class ClassPathResolver implements EntityResolver {

    private static final Log LOGGER = LogFactory.getLog(ClassPathResolver.class);

    public InputSource resolveEntity(final String publicId, final String systemId)
        throws IOException {

        LOGGER.debug("reolveEntity for publicId="
                     + publicId
                     + " and systemId="
                     + systemId);

        InputSource inputSource = null;
        final InputStream stream = getClass().getResourceAsStream(systemId.replace("file://", ""));
        if (stream == null) {
            LOGGER.debug("stream is null for systemId "
                         + systemId);
        } else {
            inputSource = new InputSource(stream);
        }
        
        return inputSource;
    }
}
