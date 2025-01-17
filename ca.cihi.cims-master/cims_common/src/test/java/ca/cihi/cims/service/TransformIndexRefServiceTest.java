package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.TransformationError;

/**
 * Test class of TransformIndexRefServiceImpl.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformIndexRefServiceTest {

	private static final Log LOGGER = LogFactory.getLog(TransformIndexRefServiceTest.class);

	@Autowired
	private TransformIndexRefServiceImpl transformIndexRefService;

	@Test
	public void testTransformShrotPresentation() {

		List<TransformationError> errors = new ArrayList<TransformationError>();

		// Alpha IndexRefDefinition
		String indexRefDefinition = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE index SYSTEM \"/dtd/cihi_cims_index.dtd\"><index language=\"ENG\" classification=\"ICD-10-CA\"><BOOK_INDEX_TYPE>A</BOOK_INDEX_TYPE><ELEMENT_ID>482636</ELEMENT_ID><INDEX_TYPE>INDEX_TERM</INDEX_TYPE><LEVEL_NUM>1</LEVEL_NUM><SEE_ALSO_FLAG/><REFERENCE_LIST><CATEGORY_REFERENCE_LIST><CATEGORY_REFERENCE><MAIN_CODE_PRESENTATION>E72.0</MAIN_CODE_PRESENTATION><MAIN_CONTAINER_CONCEPT_ID>/38/38873/60647/60871/60883</MAIN_CONTAINER_CONCEPT_ID><MAIN_CODE>E72.0</MAIN_CODE><MAIN_DAGGER_ASTERISK/><PAIRED_FLAG>X</PAIRED_FLAG><SORT_STRING>aaa-sort-string-ccc###E72.0</SORT_STRING></CATEGORY_REFERENCE></CATEGORY_REFERENCE_LIST></REFERENCE_LIST></index>";
		String expectedShortPresentation = "E72.0";
		String shortPresentation = transformIndexRefService.transformShortPresentation(indexRefDefinition, errors);

		Assert.assertEquals(expectedShortPresentation.replaceAll("\\s+", ""), shortPresentation.replaceAll("\\s+", ""));

		// Neoplasm indexRefDefinition
		indexRefDefinition = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE index SYSTEM \"/dtd/cihi_cims_index.dtd\"><index language=\"ENG\" classification=\"ICD-10-CA\"><BOOK_INDEX_TYPE>N</BOOK_INDEX_TYPE><ELEMENT_ID>602747</ELEMENT_ID><INDEX_TYPE>INDEX_TERM</INDEX_TYPE><LEVEL_NUM>2</LEVEL_NUM><SEE_ALSO_FLAG/><SITE_INDICATOR>$</SITE_INDICATOR><REFERENCE_LIST/><NEOPLASM_DETAIL><TABULAR_REF type=\"MALIGNANT_PRIMARY\"><TF_CONTAINER_CONCEPT_ID>/38/16987/16999/17011/20679/20691/20701</TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION>C40.0</CODE_PRESENTATION></TABULAR_REF><TABULAR_REF type=\"MALIGNANT_SECONDARY\"><TF_CONTAINER_CONCEPT_ID>/38/16987/16999/25143/25699/25799</TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION>C79.5</CODE_PRESENTATION></TABULAR_REF><TABULAR_REF type=\"IN_SITU\"><TF_CONTAINER_CONCEPT_ID/><CODE_PRESENTATION/></TABULAR_REF><TABULAR_REF type=\"BENIGN\"><TF_CONTAINER_CONCEPT_ID>/38/16987/29757/30621/30633</TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION>D16.0</CODE_PRESENTATION></TABULAR_REF><TABULAR_REF type=\"UU_BEHAVIOUR\"><TF_CONTAINER_CONCEPT_ID>/38/16987/33247/34867/34879</TF_CONTAINER_CONCEPT_ID><CODE_PRESENTATION>D48.0</CODE_PRESENTATION></TABULAR_REF></NEOPLASM_DETAIL></index>";
		expectedShortPresentation = "<td/><td>&diams;</td><td>C40.0</td><td>C79.5</td><td/><td>D16.0</td><td>D48.0</td>";
		shortPresentation = transformIndexRefService.transformShortPresentation(indexRefDefinition, errors);

		Assert.assertEquals(expectedShortPresentation.replaceAll("\\s+", ""), shortPresentation.replaceAll("\\s+", ""));
	}

}