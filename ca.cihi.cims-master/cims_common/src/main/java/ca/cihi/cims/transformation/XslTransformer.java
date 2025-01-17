package ca.cihi.cims.transformation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.transformation.util.ClassPathResolver;
import ca.cihi.cims.transformation.util.HtmlStringUtils;
import ca.cihi.cims.transformation.util.SpecialXmlCharactersUtils;

/**
 * Applies an XSL transformation for one particular XSL style sheet.
 * 
 * @author wxing
 */
public class XslTransformer {

	private static final Log LOGGER = LogFactory.getLog(XslTransformer.class);

	private Transformer transformer;
	private DocumentBuilder documentBuilder;

	/* package */XslTransformer() {
		// Package-visibility constructor for CGLIB proxies.
	}

	public XslTransformer(final Transformer transformer) {
		this.transformer = transformer;
		try {
			final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			// Enable the document validation as the document is being parsed
			documentFactory.setValidating(true);
			documentFactory.setNamespaceAware(true);
			documentBuilder = documentFactory.newDocumentBuilder();
			documentBuilder.setEntityResolver(new ClassPathResolver());

		} catch (ParserConfigurationException pce) {
			// Error generated by the parser
			LOGGER.error("\n** ParserConfigurationException in XMLTransformer()");
			LOGGER.error(pce.getMessage());
		}
	}

	public Transformer getTransformer() {
		return transformer;
	}

	/**
	 * Transform the given xml string to an html string
	 * 
	 * @param xmlStringBlock
	 *            String the given xml string
	 * @param errors
	 *            List<TransformationError> error list
	 * @return String
	 */
	public String transform(final String xmlStringBlock, final List<TransformationError> errors) {

		LOGGER.info(">>XslTransformer.transform");
		String htmlString = null;

		if (xmlStringBlock != null && !xmlStringBlock.isEmpty()) {
			try {
				LOGGER.debug("xmlStringBlock:" + xmlStringBlock);
				final List<TransformationError> parserErrors = new ArrayList<TransformationError>();

				final InputSource inSource = new InputSource();
				inSource.setCharacterStream(new StringReader(xmlStringBlock));

				documentBuilder.setErrorHandler(new XmlParserErrorHandler(parserErrors));

				// parse the xml string block with the specified DTD
				final Document doc = documentBuilder.parse(inSource);
				if (parserErrors.isEmpty()) {
					final DOMSource source = new DOMSource(doc);

					// Use a Transformer for output
					final StringWriter writer = new StringWriter();
					final StreamResult result = new StreamResult(writer);
					transformer.transform(source, result);

					htmlString = writer.getBuffer().toString();

					// Remove xmlns and xmlversion
					htmlString = HtmlStringUtils.removeXmlnsAndVersion(htmlString);
					// Remove line breaks
					htmlString = HtmlStringUtils.removeLineBreaks(htmlString);

					// decode the special symbols
					htmlString = SpecialXmlCharactersUtils.decodeSpecialSymbols(htmlString);

					LOGGER.debug("htmlString:" + htmlString);
				} else {
					for (int i = 0; i < parserErrors.size(); i++) {
						final TransformationError error = parserErrors.get(i);
						error.setXmlString(xmlStringBlock);
						errors.add(error);
					}
				}
			} catch (IOException ex) {
				// Error on reading the xml block
				LOGGER.error(ex.getMessage());
				errors.add(new TransformationError("", "", "", "", ex.getLocalizedMessage(), xmlStringBlock));
				htmlString = "IOException " + ex.getMessage() + " on reading " + xmlStringBlock;
			} catch (SAXException ex) {
				// Error generated by the parser
				LOGGER.error("\n** Parsing error");
				LOGGER.error(ex.getMessage());
				errors.add(new TransformationError("", "", "", "", ex.getLocalizedMessage(), xmlStringBlock));
				htmlString = "ERROR " + ex.getMessage() + " in " + xmlStringBlock;
			} catch (TransformerException te) {
				// Error generated by the transformer
				LOGGER.error("\n** Transformation error");
				LOGGER.error(te.getMessage());
				errors.add(new TransformationError("", "", "", "", te.getLocalizedMessage(), xmlStringBlock));
				htmlString = "ERROR " + te.getMessage() + " in " + xmlStringBlock;
			}
		}

		return htmlString;
	}
}
