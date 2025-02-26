package ca.cihi.cims.dal.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.cihi.cims.dal.BooleanPropertyVersion;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.DataPropertyVersion;
import ca.cihi.cims.dal.Element;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.GraphicsPropertyVersion;
import ca.cihi.cims.dal.HtmlPropertyVersion;
import ca.cihi.cims.dal.NumericPropertyVersion;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.StructureElement;
import ca.cihi.cims.dal.StructureVersion;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.dal.ValueDomainVersion;
import ca.cihi.cims.dal.XmlPropertyVersion;

/**
 * This is a fragment of Java-based configuration used by Spring because as a
 * supplement to its normal approach.
 * 
 * @author MPrescott
 */
@Configuration
public class ORMappingConfigSource {

	private static final StringTranslator STRING = new StringTranslator();
	private static final LongTranslator LONG = new LongTranslator();
	private static final BooleanTranslator BOOLEAN = new BooleanTranslator();
	private static final IntegerTranslator INTEGER = new IntegerTranslator();
	private static final DateTranslator DATE = new DateTranslator();
	private static final ByteArrayTranslator BYTEARRAY = new ByteArrayTranslator();

	/**
	 * This method defines the ORConfig bean in the spring context, which
	 * contains all the information necessary to translate from the
	 * ElementVersion and related subclasses to the database structure, and back
	 * again.
	 */
	@Bean
	public ORConfig buildMappings() {

		ORConfig config = new ORConfig();

		ClassORMapping element = new ClassORMapping(null, ElementVersion.class, "ElementVersion", "elementVersionId");
		element.column("elementId", "elementId", LONG, true);
		element.column("elementVersionId", "elementVersionId", LONG);
		element.column("status", "status", STRING, true);
		element.column("versionCode", "versionCode", STRING);
		element.column("classId", "classId", LONG, true);
		element.column("versionTimeStamp", "versionTimeStamp", DATE);
		element.column("changedFromVersionId", "changedFromVersionId", LONG);
		element.column("originatingContextId", "originatingContextId", LONG);
		config.addClassMapping(element);
		ClassORMapping conceptVersion = new ClassORMapping(element, ConceptVersion.class, "ConceptVersion", "conceptId");
		conceptVersion.column("elementVersionId", "conceptId", LONG);
		config.addClassMapping(conceptVersion);

		ClassORMapping property = new ClassORMapping(element, PropertyVersion.class, "PropertyVersion", "propertyId");
		property.column("elementVersionId", "propertyId", LONG);
		property.column("domainElementId", "domainElementId", LONG, true);
		config.addClassMapping(property);

		ClassORMapping conceptProperty = new ClassORMapping(property, ConceptPropertyVersion.class,
						"ConceptPropertyVersion", "conceptPropertyId");
		conceptProperty.column("elementVersionId", "conceptPropertyId", LONG);
		conceptProperty.column("rangeElementId", "rangeElementId", LONG);
		config.addClassMapping(conceptProperty);

		ClassORMapping dataProperty = new ClassORMapping(property, DataPropertyVersion.class, "DataPropertyVersion",
						"dataPropertyId");
		dataProperty.column("elementVersionId", "dataPropertyId", LONG);
		config.addClassMapping(dataProperty);

		ClassORMapping xmlProperty = new ClassORMapping(dataProperty, XmlPropertyVersion.class, "XMLPropertyVersion",
						"xmlPropertyId");
		xmlProperty.column("value", "xmlText", STRING);
		xmlProperty.column("languageCode", "languageCode", STRING);
		xmlProperty.column("elementVersionId", "xmlPropertyId", LONG);
		config.addClassMapping(xmlProperty);

		ClassORMapping htmlProperty = new ClassORMapping(dataProperty, HtmlPropertyVersion.class,
						"HTMLPropertyVersion", "htmlPropertyId");
		htmlProperty.column("value", "htmlText", STRING);
		htmlProperty.column("languageCode", "languageCode", STRING);
		htmlProperty.column("elementVersionId", "htmlPropertyId", LONG);
		config.addClassMapping(htmlProperty);

		ClassORMapping booleanProperty = new ClassORMapping(dataProperty, BooleanPropertyVersion.class,
						"BooleanPropertyVersion", "booleanPropertyId");
		booleanProperty.column("value", "booleanValue", BOOLEAN);
		booleanProperty.column("elementVersionId", "booleanPropertyId", LONG);
		config.addClassMapping(booleanProperty);

		ClassORMapping textProperty = new ClassORMapping(dataProperty, TextPropertyVersion.class,
						"TextPropertyVersion", "textPropertyId");
		textProperty.column("value", "text", STRING);
		textProperty.column("languageCode", "languageCode", STRING);
		textProperty.column("elementVersionId", "textPropertyId", LONG);
		config.addClassMapping(textProperty);

		ClassORMapping numericProperty = new ClassORMapping(dataProperty, NumericPropertyVersion.class,
						"NumericPropertyVersion", "numericPropertyId");
		numericProperty.column("value", "numericValue", INTEGER);
		numericProperty.column("elementVersionId", "numericPropertyId", LONG);		
		config.addClassMapping(numericProperty);

		ClassORMapping graphicsProperty = new ClassORMapping(dataProperty, GraphicsPropertyVersion.class,
						"GraphicsPropertyVersion", "graphicsPropertyId");
		graphicsProperty.column("value", "graphicsBlobValue", BYTEARRAY);
		graphicsProperty.column("elementVersionId", "graphicsPropertyId", LONG);
		graphicsProperty.column("languageCode", "languageCode", STRING);
		config.addClassMapping(graphicsProperty);

		ClassORMapping structureVersion = new ClassORMapping(element, StructureVersion.class, "StructureVersion",
						"structureId");
		structureVersion.column("structureId", "structureId", LONG);
		structureVersion.column("baseStructureId", "baseStructureId", LONG);
		structureVersion.column("contextStatus", "contextStatus", STRING);
		structureVersion.column("contextStatusDate", "contextStatusDate", DATE);
		structureVersion.column("isVersionYear", "isVersionYear", BOOLEAN);
		structureVersion.column("changeRequestId", "change_request_id", LONG);
		structureVersion.column("freezingStatusId", "freezing_status_id", LONG);

		config.addClassMapping(structureVersion);

		ClassORMapping valueDomainVersion = new ClassORMapping(element, ValueDomainVersion.class, "ValueDomainVersion",
						"domainId");
		config.addClassMapping(valueDomainVersion);

		ClassORMapping structureElementVersion = new ClassORMapping(null, StructureElement.class,
						"StructureElementVersion", "elementVersionId");
		structureElementVersion.column("structureId", "structureId", LONG);
		structureElementVersion.column("elementId", "elementId", LONG, true);
		structureElementVersion.column("elementVersionId", "elementVersionId", LONG);
		config.addClassMapping(structureElementVersion);

		ClassORMapping domainElement = new ClassORMapping(null, Element.class, "Element", "elementId");
		domainElement.column("elementId", "elementId", LONG, true);
		domainElement.column("classId", "classId", LONG, true);
		domainElement.column("businessKey", "elementUUId", STRING, true);
		config.addClassMapping(domainElement);

		return config;
	}
}
