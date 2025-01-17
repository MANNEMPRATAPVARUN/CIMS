package ca.cihi.cims.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.ClassificationLanguage;

public class XmlUtils {

	public static <T> T deserialize(Class<T> clazz, String xml) {
		if (StringUtils.isEmpty(xml)) {
			return null;
		} else {
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				spf.setFeature("http://xml.org/sax/features/validation", false);
				return deserialize(spf, clazz, xml);
			} catch (Exception ex) {
				throw new CIMSException("Error deserializing: " + ex.getMessage(), ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T deserialize(SAXParserFactory spf, Class<T> clazz, String xml) throws Exception {
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();
		InputSource inputSource = new InputSource(new StringReader(xml));
		SAXSource source = new SAXSource(xmlReader, inputSource);
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller m = context.createUnmarshaller();
		return (T) m.unmarshal(source);
	}

	public static String serialize(Object obj) {
		if (obj != null && obj instanceof ClassificationLanguage) {
			ClassificationLanguage cl = (ClassificationLanguage) obj;
			String classification = cl.getClassification();
			String language = cl.getLanguage();
			cl.setLanguage(null);
			cl.setClassification(null);
			String xml = serializeRaw(obj);
			cl.setLanguage(language);
			cl.setClassification(classification);
			XmlRootElement root = obj.getClass().getAnnotation(XmlRootElement.class);
			xml = StringUtils.replace(xml, "<" + root.name(), //
					"<" + root.name() + //
							" classification=\"" + StringEscapeUtils.escapeXml(classification) //
							+ "\" language=\"" + StringEscapeUtils.escapeXml(language) + "\"");
			return xml;
		} else {
			return serializeRaw(obj);
		}
	}

	public static String serializeRaw(Object obj) {
		if (obj == null) {
			return null;
		} else {
			try {
				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(obj.getClass());
				Marshaller m = context.createMarshaller();
				// m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
				// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(obj, writer);
				return writer.toString();
			} catch (Exception ex) {
				throw new CIMSException("Error serializing: " + ex.getMessage(), ex);
			}
		}
	}

}
