package ca.cihi.cims.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ca.cihi.cims.content.icd.CategoryReferenceXml;
import ca.cihi.cims.content.icd.IcdIndexDrugsAndChemicalsXml;
import ca.cihi.cims.content.icd.IcdIndexNeoplasmXml;
import ca.cihi.cims.content.icd.IndexReferenceXml;
import ca.cihi.cims.content.icd.IndexXml;
import ca.cihi.cims.content.icd.TabularRefXml;

public class IndexXmlSerializationTest {

	private String getFileContent(String file) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("xml/" + file);
		return IOUtils.toString(is);
	}

	@Test
	public void testIcdIndexDrugsAndChemicals() throws Exception {
		IcdIndexDrugsAndChemicalsXml obj = XmlUtils.deserialize(IcdIndexDrugsAndChemicalsXml.class,
				getFileContent("IndexDrugsAndChemicals.xml"));
		assertEquals("D", obj.getBookIndexType());
		assertEquals(1980470, obj.getElementId());
		assertEquals("INDEX_TERM", obj.getIndexType());
		assertEquals(1, obj.getLevelNum());
		assertEquals("X", obj.getSeeAlsoFlag());
		assertEquals("", obj.getSiteIndicator());

		assertNotSame(null, obj.getReferenceList().getIndexReferenceList());
		assertNotSame(null, obj.getReferenceList().getCategoryReferenceList());

		List<TabularRefXml> tabulars = obj.getDrugsDetail();
		assertEquals(5, tabulars.size());
		{
			TabularRefXml ref = tabulars.get(0);
			assertEquals("CHAPTER_XIX", ref.getType());
			assertEquals("/885945/1441736/1534809/1540675/1541043", ref.getContainerConceptIdPath());
			assertEquals("T50.8", ref.getCodePresentation());
		}
	}

	@Test
	public void testIndex1() throws Exception {
		IndexXml obj = XmlUtils.deserialize(IndexXml.class, getFileContent("IndexReference.xml"));
		assertEquals("A", obj.getBookIndexType());
		assertEquals(1709389, obj.getElementId());
		assertEquals("INDEX_TERM", obj.getIndexType());
		assertEquals(1, obj.getLevelNum());
		assertEquals("Y", obj.getSeeAlsoFlag());
		assertEquals("", obj.getSiteIndicator());

		List<IndexReferenceXml> indexList = obj.getReferenceList().getIndexReferenceList();
		List<CategoryReferenceXml> categoryList = obj.getReferenceList().getCategoryReferenceList();
		assertNotSame(null, categoryList);
		assertNotSame(null, indexList);
		assertEquals(1, indexList.size());

		IndexReferenceXml indexRef = indexList.get(0);
		assertEquals("/885945/1709265/1709299/1709389", indexRef.getContainerIndexIdPath());
		assertEquals("condition", indexRef.getReferenceLinkDescription());
	}

	@Test
	public void testIndex2() throws Exception {
		IndexXml obj = XmlUtils.deserialize(IndexXml.class, getFileContent("CategoryReference.xml"));
		assertEquals("A", obj.getBookIndexType());
		assertEquals(1716587, obj.getElementId());
		assertEquals("INDEX_TERM", obj.getIndexType());
		assertEquals(2, obj.getLevelNum());
		assertEquals("X", obj.getSeeAlsoFlag());
		assertEquals("", obj.getSiteIndicator());

		List<IndexReferenceXml> indexList = obj.getReferenceList().getIndexReferenceList();
		List<CategoryReferenceXml> categoryList = obj.getReferenceList().getCategoryReferenceList();
		assertNotSame(null, indexList);
		assertNotSame(null, categoryList);
		assertEquals(2, categoryList.size());

		{
			CategoryReferenceXml indexRef = categoryList.get(0);
			assertEquals("F45.3", indexRef.getMainCodePresentation());
			assertEquals("F45.3", indexRef.getMainCode());
			assertEquals("", indexRef.getMainCodeDaggerAsteriskCode());
			assertEquals("X", indexRef.getPairedFlag());
			assertEquals("aaa-sort-string-ccc###F45.3", indexRef.getSortString());
			assertEquals("/885945/1027114/1037012/1038544/1038706", indexRef.getMainContainerConceptIdPath());
		}
		{
			CategoryReferenceXml indexRef = categoryList.get(1);
			assertEquals("E10.0", indexRef.getMainCodePresentation());
			assertEquals("E10.0", indexRef.getMainCode());
			assertEquals("", indexRef.getMainCodeDaggerAsteriskCode());
			assertEquals("Y", indexRef.getPairedFlag());
			assertEquals("aaa-sort-string-aaa###E10.0", indexRef.getSortString());
			assertEquals("/885945/971918/973958/973972/973986", indexRef.getMainContainerConceptIdPath());
			assertEquals("/885945/971918/973958/973972/974034/974046", indexRef.getPairedContainerConceptIdPath());
			assertEquals("E10.10", indexRef.getPairedCodePresentation());
			assertEquals("E10.10", indexRef.getPairedCode());
		}
	}

	@Test
	public void testNeoplasm() throws Exception {
		IcdIndexNeoplasmXml obj = XmlUtils.deserialize(IcdIndexNeoplasmXml.class, getFileContent("IndexNeoplasm.xml"));
		assertEquals("N", obj.getBookIndexType());
		assertEquals(1959607, obj.getElementId());
		assertEquals("INDEX_TERM", obj.getIndexType());
		assertEquals(2, obj.getLevelNum());
		assertEquals("X", obj.getSeeAlsoFlag());
		assertEquals("", obj.getSiteIndicator());

		assertNotSame(null, obj.getReferenceList().getIndexReferenceList());
		assertNotSame(null, obj.getReferenceList().getCategoryReferenceList());

		List<TabularRefXml> tabulars = obj.getNeoplasmDetail();
		assertEquals(5, tabulars.size());
		{
			TabularRefXml ref = tabulars.get(0);
			assertEquals("MALIGNANT_PRIMARY", ref.getType());
			assertEquals("/885945/923304/923316/923328/931416/931704/931958", ref.getContainerConceptIdPath());
			assertEquals("C41.4", ref.getCodePresentation());
		}
	}

}
