package ca.cihi.cims.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.cihi.cims.CIMSException;

public class CihiDefaultXmlTemplates {

	public enum TemplateType {
		NOTE, DEFINITION, INCLUDE, EXCLUDE, CODE_ALSO, OMIT_CODE, TABLE;
	}

	private final Map<TemplateType, String> map = new HashMap<TemplateType, String>();

	// ---------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public CihiDefaultXmlTemplates() throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		InputStream input = getClass().getResourceAsStream("/cihi_default_xml_templates.xml");
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(input);
		NodeList rootNodes = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < rootNodes.getLength(); i++) {
			Node node = rootNodes.item(i);
			if (node.getNodeName().equals("template")) {
				String id = node.getAttributes().getNamedItem("id").getTextContent();
				StringBuilder xmlBuilder = new StringBuilder();
				NodeList childNodes = node.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					StringWriter stringWriter = new StringWriter();
					transformer.transform(new DOMSource(childNodes.item(j)), new StreamResult(stringWriter));
					xmlBuilder.append(stringWriter.toString());
				}
				String xml = StringUtils.remove(xmlBuilder.toString(), "\t");
				map.put(TemplateType.valueOf(id.toUpperCase()), xml);
			}
		}
		if (map.size() != TemplateType.values().length) {
			Collection<TemplateType> missing = ListUtils.removeAll(Arrays.asList(TemplateType.values()), map.keySet());
			throw new CIMSException("Not all XML templates are specified: " + missing);
		}
	}

	public String get(TemplateType type) {
		return map.get(type);
	}

}
