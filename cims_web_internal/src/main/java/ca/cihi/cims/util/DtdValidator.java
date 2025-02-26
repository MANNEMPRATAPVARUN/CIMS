package ca.cihi.cims.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.jdom.input.BuilderErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DtdValidator {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

	private final DocumentBuilderFactory domFactory;
	private final BuilderErrorHandler errorHandler = new BuilderErrorHandler();
	private final EntityResolver entityResolver = new EntityResolver() {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			String file = StringUtils.substringAfterLast(systemId, "/");
			InputStream stream = getClass().getResourceAsStream("/dtd/" + file);
			if (stream == null) {
				return null;
			} else {
				return new InputSource(stream);
			}
		}
	};

	// --------------------------------------------------------------------------------------

	public DtdValidator() {
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setValidating(true);
	}

	public String validateDocument(String rootElement, String dtd, String xml) {
		xml = StringUtils.remove(xml, XML_HEADER);
		return validateSegment(rootElement, dtd, xml);
	}

	public String validateSegment(String rootElement, String dtd, String xml) {
		try {
			String xmlDtd = "<!DOCTYPE " + rootElement + " SYSTEM \"" + dtd + "\">\n" + xml;
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			builder.setEntityResolver(entityResolver);
			builder.setErrorHandler(errorHandler);
			builder.parse(new InputSource(new ByteArrayInputStream(xmlDtd.getBytes(UTF8))));
			return null;
		} catch (Exception ex) {
			return ex.getMessage();
		}
	}

}
