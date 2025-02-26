package org.cihi.claml.utils;

import java.io.IOException;
import java.io.InputStream;

import org.cihi.claml.converter.CciClamlToPdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class ClamlEntityResolver implements EntityResolver {

	  /** The Constant log. */
	  private static final Logger log = LoggerFactory.getLogger(CciClamlToPdfConverter.class);

    public InputSource resolveEntity(final String publicId, final String systemId)
        throws IOException {

        log.debug("reolveEntity for publicId="
                     + publicId
                     + " and systemId="
                     + systemId);

        InputSource inputSource = null;
        int pos = systemId.lastIndexOf('/');
        String internalResource = "/schema" + systemId.substring(pos);
        final InputStream stream = getClass().getResourceAsStream(internalResource);
        if (stream == null) {
            log.debug("stream is null for systemId "
                         + systemId);
        } else {
            inputSource = new InputSource(stream);
        }
        
        return inputSource;
    }
}