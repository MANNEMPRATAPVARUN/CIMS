package ca.cihi.cims.framework.handler;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.domain.Classs;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.domain.Element;
import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.dto.ContextDTO;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.refset.concept.RefsetImpl;
import ca.cihi.cims.refset.config.MetadataConfigSource;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class HandlerTest {

	@Autowired
	private ClasssHandler classsHandler;

	@Autowired
	private ConceptHandler conceptHandler;

	@Autowired
	private ContextHandler contextHandler;

	@Autowired
	@Qualifier("frameworkElementHandler")
	private ElementHandler elementHandler;

	private Logger LOGGER = LogManager.getLogger(this.getClass());
	@Autowired
	private SearchHandler searchHandler;

	@Transactional
	@Test
	public void testHandlers() {
		ClasssDTO newClasss = new ClasssDTO("BaseRefset", "CDEX-TEST", "CDEX-TEST", "CDEX-TEST Friendly");

		classsHandler.createClasss(newClasss);

		assertTrue(newClasss.getClasssId() != null);

		ClasssDTO newClasssRefset = new ClasssDTO("ConceptVersion", "CDEX-TEST", "Refset", "Refset");

		classsHandler.createClasss(newClasssRefset);

		ClasssDTO newClasssPickList = new ClasssDTO("ConceptVersion", "CDEX-TEST", "PickList", "PickList");

		classsHandler.createClasss(newClasssPickList);

		ClasssDTO classsDTO = classsHandler.getClasss(newClasss.getClasssId());

		assertNotNull(classsDTO);

		LOGGER.debug("Create context Element.......");
		Long elementId = contextHandler.createContextElement(classsDTO.getClasssId(), "CDEX-TEST");
		assertNotNull(elementId);

		assertTrue(!elementHandler.existsInContext("This is a test", 1l));

		LOGGER.debug("Create context version......");
		Long contextId = contextHandler.createContextVersion(newClasss.getClasssId(), elementId, "CDEX-TEXT V1.0",
				null);

		ContextDTO contextDTO = contextHandler.getContext(contextId);
		assertNotNull(contextDTO);
		assertNotNull(contextDTO.getElementStatus());
		assertNotNull(contextDTO.getElementUUID());
		assertNotNull(contextDTO.getVersionTimestamp());
		assertTrue(contextDTO.getChangedFromVersionId() == null);
		assertTrue(contextDTO.getBaseContextId() == null);

		Classs classs = new Classs(classsDTO);

		Context context = new Context(new ElementIdentifier(elementId, contextId), classs, "CDEX-TEXT V1.0",
				ContextStatus.OPEN, null);

		LOGGER.debug("Create refset concept......");
		ElementIdentifier eIdRefsetConcept = conceptHandler.createConcept(context, "CDEX-TEST", "Refset",
				"REFSET_CONCEPT_CDEX-TEST");

		assertNotNull(eIdRefsetConcept);

		// test elementIdentifier
		assertEquals(contextDTO.getElementIdentifier(), context.getElementIdentifier());
		assertNotEquals(context.getElementIdentifier(), eIdRefsetConcept);

		LOGGER.debug("Create picklist concept......");
		ElementIdentifier eIdPicklistConcept = conceptHandler.createConcept(context, "CDEX-TEST", "PickList",
				"PickList1_CDEX-TEST");

		Long elementIdRefset = eIdRefsetConcept.getElementId();

		ElementDTO refsetConcpetElement = elementHandler.findElementInContext(contextId, elementIdRefset);

		assertNotNull(refsetConcpetElement);

		Long elementVersionIdRefset = eIdRefsetConcept.getElementVersionId();

		assertTrue(elementHandler.existsInContext("REFSET_CONCEPT_CDEX-TEST", contextId));

		assertNotNull(elementVersionIdRefset);

		Long elementVersionIdRefsetNew = elementHandler.updateElementInContext(contextId, elementIdRefset);

		assertEquals(elementVersionIdRefset, elementVersionIdRefsetNew);

		PropertyHandler textPropertyHandler = PropertyHandler.findHandler(PropertyType.TextProperty);

		ClasssDTO nameClass = new ClasssDTO("TextPropertyVersion", "CDEX-TEST", "Name", "Name");

		classsHandler.createClasss(nameClass);

		ElementIdentifier nameEid = textPropertyHandler.updateProperty(contextId,
				new ElementIdentifier(elementIdRefset, elementVersionIdRefsetNew), nameClass.getClasssId(),
				Language.ENG, null, "CDEX-TEST");

		assertNotNull(nameEid);

		ClasssDTO cciContextIdClasss = new ClasssDTO("NumericPropertyVersion", "CDEX-TEST", "CCIContextId",
				"CCIContextId");

		classsHandler.createClasss(cciContextIdClasss);

		PropertyHandler numericHandler = PropertyHandler.findHandler(PropertyType.NumericProperty);

		ElementIdentifier cciContextIdEid = numericHandler.updateProperty(contextId,
				new ElementIdentifier(elementIdRefset, elementVersionIdRefsetNew), cciContextIdClasss.getClasssId(),
				Language.NOLANGUAGE, null, 2015l);

		assertNotNull(cciContextIdEid);

		ClasssDTO partOfClasss = new ClasssDTO("ConceptPropertyVersion", "CDEX-TEST", "PartOf", "PartOf");

		classsHandler.createClasss(partOfClasss);

		PropertyHandler conceptPropertyHandler = PropertyHandler.findHandler(PropertyType.ConceptProperty);

		ElementIdentifier picklistConceptPropertyEid = conceptPropertyHandler.updateProperty(contextId,
				eIdPicklistConcept, partOfClasss.getClasssId(), Language.NOLANGUAGE, null,
				eIdRefsetConcept.getElementId());

		assertNotNull(picklistConceptPropertyEid);

		LOGGER.debug("Start columns ........");

		ClasssDTO columnOfClasss = new ClasssDTO("ConceptPropertyVersion", "CDEX-TEST", "ColumnOf", "ColumnOf");
		classsHandler.createClasss(columnOfClasss);

		ClasssDTO columnTypeClasss = new ClasssDTO("TextPropertyVersion", "CDEX-TEST", "ColumnType", "ColumnType");
		classsHandler.createClasss(columnTypeClasss);

		ClasssDTO columnNameClasss = new ClasssDTO("TextPropertyVersion", "CDEX-TEST", "ColumnName", "ColumnName");
		classsHandler.createClasss(columnNameClasss);

		ClasssDTO columnOrderClasss = new ClasssDTO("NumericPropertyVersion", "CDEX-TEST", "ColumnOrder",
				"ColumnOrder");
		classsHandler.createClasss(columnOrderClasss);

		ClasssDTO textValueClasss = new ClasssDTO("TextPropertyVersion", "CDEX-TEST", "TextValue", "TextValue");
		classsHandler.createClasss(textValueClasss);

		ClasssDTO idValueClasss = new ClasssDTO("NumericPropertyVersion", "CDEX-TEST", "IdValue", "IdValue");
		classsHandler.createClasss(idValueClasss);

		ClasssDTO columnClasss = new ClasssDTO("ConceptVersion", "CDEX-TEST", "Column", "Column");
		classsHandler.createClasss(columnClasss);

		ElementIdentifier column1 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column1, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, eIdPicklistConcept.getElementId());
		textPropertyHandler.updateProperty(contextId, column1, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "CIMS ICD-10-CA Code");
		textPropertyHandler.updateProperty(contextId, column1, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "CIMS ICD-10-CA Code");
		numericHandler.updateProperty(contextId, column1, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				1);

		ElementIdentifier column2 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column2, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, eIdPicklistConcept.getElementId());
		textPropertyHandler.updateProperty(contextId, column2, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "ICD-10-CA Code");
		textPropertyHandler.updateProperty(contextId, column2, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "ICD-10-CA Code");
		numericHandler.updateProperty(contextId, column2, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				2);

		ElementIdentifier column3 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column3, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, eIdPicklistConcept.getElementId());
		textPropertyHandler.updateProperty(contextId, column3, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "CIMS ICD-10-CA Description(ENG)");
		textPropertyHandler.updateProperty(contextId, column3, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "CIMS ICD-10-CA Description(ENG)");
		numericHandler.updateProperty(contextId, column3, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				3);

		ElementIdentifier column4 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column4, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, eIdPicklistConcept.getElementId());
		textPropertyHandler.updateProperty(contextId, column4, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "Sublist Column");
		textPropertyHandler.updateProperty(contextId, column4, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "Snomed Sublist");
		numericHandler.updateProperty(contextId, column4, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				4);

		ElementIdentifier column5 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column5, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column5, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT Concept ID");
		textPropertyHandler.updateProperty(contextId, column5, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT Concept ID");
		numericHandler.updateProperty(contextId, column5, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				1);

		ElementIdentifier column6 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column6, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column6, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT-Fully Specified Name ID");
		textPropertyHandler.updateProperty(contextId, column6, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT-Fully Specified Name ID");
		numericHandler.updateProperty(contextId, column6, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				2);

		ElementIdentifier column7 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column7, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column7, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT-Fully Specified Name");
		textPropertyHandler.updateProperty(contextId, column7, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT-Fully Specified Name");
		numericHandler.updateProperty(contextId, column7, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				3);

		ElementIdentifier column8 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column8, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column8, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "Concept Type");
		textPropertyHandler.updateProperty(contextId, column8, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "Concept Type");
		numericHandler.updateProperty(contextId, column8, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				4);

		ElementIdentifier column9 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column9, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column9, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT- Preferred Term ID");
		textPropertyHandler.updateProperty(contextId, column9, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT- Preferred Term ID");
		numericHandler.updateProperty(contextId, column9, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				5);

		ElementIdentifier column10 = conceptHandler.createConcept(context, "CDEX-TEST", "Column",
				Element.generateBusinessKey());
		conceptPropertyHandler.updateProperty(contextId, column10, columnOfClasss.getClasssId(), Language.NOLANGUAGE,
				null, column4.getElementId());
		textPropertyHandler.updateProperty(contextId, column10, columnTypeClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT- Preferred Term");
		textPropertyHandler.updateProperty(contextId, column10, columnNameClasss.getClasssId(), Language.NOLANGUAGE,
				null, "SCT- Preferred Term");
		numericHandler.updateProperty(contextId, column10, columnOrderClasss.getClasssId(), Language.NOLANGUAGE, null,
				6);

		ClasssDTO recordClasss = new ClasssDTO("ConceptVersion", "CDEX-TEST", "Record", "Record");
		classsHandler.createClasss(recordClasss);

		ClasssDTO sublistClasss = new ClasssDTO("ConceptVersion", "CDEX-TEST", "Sublist", "Sublist");

		classsHandler.createClasss(sublistClasss);

		ClasssDTO valueClasss = new ClasssDTO("ConceptVersion", "CDEX-TEST", "Value", "Value");

		classsHandler.createClasss(valueClasss);

		ClasssDTO describedByClasss = new ClasssDTO("ConceptPropertyVersion", "CDEX-TEST", "DescribedBy",
				"DescribedBy");

		classsHandler.createClasss(describedByClasss);

		// mock 1000 records with 10 columns

		int testLength = 10;
		for (int i = 0; i < testLength; i++) {

			// create record concept and link it to the picklist
			ElementIdentifier record = conceptHandler.createConcept(context, "CDEX-TEST", "Record",
					Element.generateBusinessKey());
			conceptPropertyHandler.updateProperty(contextId, record, partOfClasss.getClasssId(), Language.NOLANGUAGE,
					null, eIdPicklistConcept.getElementId());

			// create value 1 link it to the record and column and update its properties
			ElementIdentifier value1 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
					Element.generateBusinessKey());
			conceptPropertyHandler.updateProperty(contextId, value1, partOfClasss.getClasssId(), Language.NOLANGUAGE,
					null, record.getElementId());

			conceptPropertyHandler.updateProperty(contextId, value1, describedByClasss.getClasssId(),
					Language.NOLANGUAGE, null, column1.getElementId());

			numericHandler.updateProperty(contextId, value1, idValueClasss.getClasssId(), Language.NOLANGUAGE, null,
					250863l);

			textPropertyHandler.updateProperty(contextId, value1, textValueClasss.getClasssId(), Language.NOLANGUAGE,
					null, "S00.0");

			// create value 1 link it to the record and column and update its properties
			ElementIdentifier value2 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
					Element.generateBusinessKey());
			conceptPropertyHandler.updateProperty(contextId, value2, partOfClasss.getClasssId(), Language.NOLANGUAGE,
					null, record.getElementId());

			conceptPropertyHandler.updateProperty(contextId, value2, describedByClasss.getClasssId(),
					Language.NOLANGUAGE, null, column2.getElementId());

			textPropertyHandler.updateProperty(contextId, value2, textValueClasss.getClasssId(), Language.NOLANGUAGE,
					null, "S000");

			// create value 1 link it to the record and column and update its properties
			ElementIdentifier value3 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
					Element.generateBusinessKey());
			conceptPropertyHandler.updateProperty(contextId, value3, partOfClasss.getClasssId(), Language.NOLANGUAGE,
					null, record.getElementId());

			conceptPropertyHandler.updateProperty(contextId, value3, describedByClasss.getClasssId(),
					Language.NOLANGUAGE, null, column3.getElementId());

			textPropertyHandler.updateProperty(contextId, value3, textValueClasss.getClasssId(), Language.ENG, null,
					"Superficial injury of scalp");

			// create sublist 1 link it to the record and column
			ElementIdentifier sublist1 = conceptHandler.createConcept(context, "CDEX-TEST", "Sublist",
					Element.generateBusinessKey());
			conceptPropertyHandler.updateProperty(contextId, sublist1, partOfClasss.getClasssId(), Language.NOLANGUAGE,
					null, record.getElementId());

			conceptPropertyHandler.updateProperty(contextId, sublist1, describedByClasss.getClasssId(),
					Language.NOLANGUAGE, null, column4.getElementId());

			for (int j = 0; j < 2; j++) {
				// create record in the sublist and link it to the sublist
				ElementIdentifier record2 = conceptHandler.createConcept(context, "CDEX-TEST", "Record",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, record2, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, sublist1.getElementId());

				// create value and link it to the record and column and update its properties
				ElementIdentifier value5 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value5, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value5, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column5.getElementId());

				numericHandler.updateProperty(contextId, value5, idValueClasss.getClasssId(), Language.NOLANGUAGE, null,
						306065003l);

				// create value and link it to the record and column and update its properties
				ElementIdentifier value6 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value6, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value6, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column6.getElementId());

				numericHandler.updateProperty(contextId, value6, idValueClasss.getClasssId(), Language.NOLANGUAGE, null,
						702431015l);

				// create value and link it to the record and column and update its properties
				ElementIdentifier value7 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value7, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value7, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column7.getElementId());

				textPropertyHandler.updateProperty(contextId, value7, textValueClasss.getClasssId(),
						Language.NOLANGUAGE, null, "Referral by podiatrist (procedure)");

				// create value and link it to the record and column and update its properties
				ElementIdentifier value8 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value8, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value8, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column8.getElementId());

				textPropertyHandler.updateProperty(contextId, value7, textValueClasss.getClasssId(),
						Language.NOLANGUAGE, null, "Procedure");

				// create value and link it to the record and column and update its properties
				ElementIdentifier value9 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value9, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value9, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column9.getElementId());

				numericHandler.updateProperty(contextId, value9, idValueClasss.getClasssId(), Language.NOLANGUAGE, null,
						448876010l);

				// create value and link it to the record and column and update its properties
				ElementIdentifier value10 = conceptHandler.createConcept(context, "CDEX-TEST", "Value",
						Element.generateBusinessKey());
				conceptPropertyHandler.updateProperty(contextId, value10, partOfClasss.getClasssId(),
						Language.NOLANGUAGE, null, record2.getElementId());

				conceptPropertyHandler.updateProperty(contextId, value10, describedByClasss.getClasssId(),
						Language.NOLANGUAGE, null, column10.getElementId());

				textPropertyHandler.updateProperty(contextId, value10, textValueClasss.getClasssId(),
						Language.NOLANGUAGE, null, "Referral by podiatrist");
			}

		}
		long start = new Date().getTime();
		System.out.println("Search handler test " + testLength + " records started at: " + start);

		List<Long> propertyClassIds = new ArrayList<>();
		propertyClassIds.add(idValueClasss.getClasssId());
		propertyClassIds.add(textValueClasss.getClasssId());
		propertyClassIds.add(describedByClasss.getClasssId());

		List<PropertyHierarchyDTO> dtos = searchHandler.searchHierarchyForProperties(eIdPicklistConcept.getElementId(),
				contextId, partOfClasss.getClasssId(), propertyClassIds, 3);

		for (int i = 0; i < dtos.size(); i++) {
			System.out.println("No " + i + " properties: " + dtos.get(i).getPropertyValue());
		}

		long end = new Date().getTime();
		System.out.println("Search handler test " + testLength + " records ended at: " + end);

		System.out.println("Total used: " + ((end - start)) + " milli seconds.......");

		start = new Date().getTime();
		conceptHandler.findReferencingConcepts(partOfClasss.getClasssId(), propertyClassIds, contextId,
				eIdPicklistConcept.getElementId(), new ArrayList<>(), columnClasss.getClasssId());
		end = new Date().getTime();

		System.out.println("Total query for one steps used: " + ((end - start)) + " milli seconds.......");

		conceptHandler.findReferencedConcept(partOfClasss.getClasssId(), propertyClassIds, contextId,
				eIdPicklistConcept.getElementId(), new ArrayList<>(), newClasssRefset.getClasssId());

		Map<PropertyKey, PropertyDTO> maps = conceptHandler.findPropertiesForConcept(contextId,
				eIdRefsetConcept.getElementId(), MetadataConfigSource.getMetadata(RefsetImpl.class)
						.getPropertyConfigurations().get(ConceptLoadDegree.REGULAR));

		assertNotNull(maps);

		long startRemoveColumn = new Date().getTime();
		System.out.println("Handler test remove one column concept with " + testLength + " records started at: "
				+ startRemoveColumn);
		conceptHandler.remove(contextId, column4);

		long endRemoveColumn = new Date().getTime();
		System.out
				.println("Handler test remove one column with " + testLength + " records ended at: " + endRemoveColumn);

		System.out.println("Total used: " + ((endRemoveColumn - startRemoveColumn)) + " milli seconds.......");

		ElementDTO removedColumn = elementHandler.findElementInContext(contextId, column4.getElementId());
		assertTrue(removedColumn == null);

		long startRemoveConcept = new Date().getTime();
		System.out.println(
				"Search handler test remove concept with " + testLength + " records started at: " + startRemoveConcept);
		conceptHandler.remove(contextId, eIdPicklistConcept);

		long endRemoveConcept = new Date().getTime();
		System.out.println(
				"Search handler test remove concept with " + testLength + " records ended at: " + endRemoveConcept);

		System.out.println("Total used: " + ((endRemoveConcept - startRemoveConcept)) + " milli seconds.......");

		ElementDTO removedPicklist = elementHandler.findElementInContext(contextId, eIdPicklistConcept.getElementId());
		assertTrue(removedPicklist == null);

		long startRemove = new Date().getTime();
		System.out.println(
				"Search handler test remove concept with " + testLength + " records started at: " + startRemove);
		contextHandler.remove(contextId);

		long endRemove = new Date().getTime();
		System.out.println("Search handler test remove concept with " + testLength + " records ended at: " + end);

		System.out.println("Total used: " + ((endRemove - startRemove)) + " milli seconds.......");

		contextHandler.closeContext(context.getContextId());

		conceptHandler.findDisabledConceptIds(3103461L, 5245284L, 5L);
	}
}
