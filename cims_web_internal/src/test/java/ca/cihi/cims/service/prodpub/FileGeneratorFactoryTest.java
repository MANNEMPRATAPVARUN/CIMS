package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class FileGeneratorFactoryTest {

	@Autowired
	FileGeneratorFactory fileGeneratorFactory;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	@Test(expected = CIMSException.class)
	public void testCreateFileGenerator() {
		FileGenerator fileGenerator = fileGeneratorFactory.createFileGenerator("ICDValidationFile");
		assertEquals(pubDirectory, fileGenerator.getPubDirectory());
		assertNotNull(fileGenerator.getPublicationMapper());
		assertTrue(fileGenerator instanceof ICDValidationFileGenerator);

		FileGenerator cciFileGenerator = fileGeneratorFactory.createFileGenerator("CCIValidationFile");
		assertEquals(pubDirectory, cciFileGenerator.getPubDirectory());
		assertNotNull(cciFileGenerator.getPublicationMapper());
		assertTrue(cciFileGenerator instanceof CCIValidationFileGenerator);

		FileGenerator statusFileGenerator = fileGeneratorFactory.createFileGenerator("CCIStatusFile");
		assertEquals(pubDirectory, statusFileGenerator.getPubDirectory());
		assertNotNull(statusFileGenerator.getPublicationMapper());
		assertTrue(statusFileGenerator instanceof CCIStatusFileGenerator);

		FileGenerator locationFileGenerator = fileGeneratorFactory.createFileGenerator("CCILocationFile");
		assertEquals(pubDirectory, locationFileGenerator.getPubDirectory());
		assertNotNull(locationFileGenerator.getPublicationMapper());
		assertTrue(locationFileGenerator instanceof CCILocationFileGenerator);

		FileGenerator extentFileGenerator = fileGeneratorFactory.createFileGenerator("CCIExtentFile");
		assertEquals(pubDirectory, extentFileGenerator.getPubDirectory());
		assertNotNull(extentFileGenerator.getPublicationMapper());
		assertTrue(extentFileGenerator instanceof CCIExtentFileGenerator);

		fileGeneratorFactory.createFileGenerator("Test exception");

	}
}
