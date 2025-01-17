package ca.cihi.cims.transformation;

import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

/**
 * This class handles construction of XslTransformers.
 * 
 * @author MPrescott
 */
public class XslTransformerFactory {

	private static final Log LOGGER = LogFactory.getLog(XslTransformerFactory.class);

	public XslTransformerFactory() {
	}

	public XslTransformer create(final Resource xslt) {

		LOGGER.info("Creating XslTransformer for XSL resource " + xslt.getDescription());

		XslTransformer xslTransformer = null;

		try {

			final TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",
					null);

			URL xsltURL = xslt.getURL();

			Source xsltSource = new StreamSource(xsltURL.openStream(), xsltURL.toExternalForm());
			Transformer transformer = tFactory.newTransformer(xsltSource);
			xslTransformer = new XslTransformer(transformer);
		} catch (Exception e) {
			LOGGER.error("Error on creating Transformer:", e);
		}

		return xslTransformer;
	}
}
