package ca.cihi.cims.framework.domain;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.dto.ClasssDTO;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;
import ca.cihi.cims.framework.handler.ElementHandler;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class PropertyTest {

	@Autowired
	@Qualifier("frameworkElementHandler")
	private ElementHandler elementHandler;

	@Test
	public void testProperty() {

		Long contextId = 3103461l;
		Long conceptId = 250823l;

		// this test used ICD-10-CA 2015 data to test, based on the fact that this data never change.
		Context context = Context.findById(contextId);
		PropertyKey key = new PropertyKey("LongTitle", Language.ENG, PropertyType.TextProperty);

		Assert.assertEquals("LongTitle_ENG", key.generateKeyIdentifier());

		Property property = Property.loadProperty(context, conceptId, key);

		Assert.assertNotNull(property);

		Assert.assertEquals("LongTitle", property.getKey().getClassName());
		Assert.assertEquals(Language.ENG, property.getKey().getLanguage());
		Assert.assertEquals(PropertyType.TextProperty, property.getKey().getPropertyType());

		Assert.assertEquals("Injury, poisoning and certain other consequences of external causes (S00-T98)",
				property.getValue().getValue());

		Assert.assertNotNull(property.getElementIdentifier());

		ElementDTO conceptElement = elementHandler.findElementInContext(contextId, conceptId);

		property.setValue(new PropertyValue("This is a test"), conceptElement.getElementIdentifier());

		Assert.assertEquals("This is a test", property.getValue().getValue());

		property.setValue(new PropertyValue("This is a test"), conceptElement.getElementIdentifier());

		PropertyKey keyName = new PropertyKey("DiagramFileName", Language.ENG, PropertyType.TextProperty);

		Property propertyNotExists = Property.loadProperty(context, 250823l, keyName);
		Assert.assertNotNull(propertyNotExists);
		Assert.assertNull(propertyNotExists.getElementIdentifier());
		Assert.assertNull(propertyNotExists.getValue());

		// Test xlsxproperty, test purpose only
		ClasssDTO classsDTO = new ClasssDTO("XlsxPropertyVersion", "ICD-10-CA", "Content", "Content");
		Classs.create(classsDTO);

		PropertyKey keyXlsx = new PropertyKey("Content", Language.ENG, PropertyType.XLSXProperty);
		Property xlsxProperty = Property.loadProperty(context, conceptId, keyXlsx);

		byte[] content;
		try {
			Resource schemaFile = new ClassPathResource("/stylesheet/cims_index.xsl", this.getClass());
			content = IOUtils.toByteArray(schemaFile.getInputStream());
			xlsxProperty.setValue(new PropertyValue(content), conceptElement.getElementIdentifier());

			Assert.assertNotNull(xlsxProperty.getValue());

			Property xlsxLoadFromDB = Property.loadProperty(context, conceptId, keyXlsx);
			Assert.assertNotNull(xlsxLoadFromDB);
			Blob blob = (Blob) xlsxLoadFromDB.getValue().getValue();

			blob.getBinaryStream();

		} catch (IOException | SQLException e) {

			e.printStackTrace();
		}

		Context icd2015 = Context.findById(3103461l);
		Assert.assertTrue(Property.checkDuplicateValue(null, "Category", "Code", "A00", icd2015.getElementIdentifier(),
				"BaseClassification"));

	}
}
