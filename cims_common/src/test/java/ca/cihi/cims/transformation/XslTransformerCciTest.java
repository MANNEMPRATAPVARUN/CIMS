package ca.cihi.cims.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import ca.cihi.cims.model.TransformationError;

/**
 * Test class of XslTransformer
 * 
 * @author wxing
 * 
 */
public class XslTransformerCciTest {

	private static final Log LOGGER = LogFactory.getLog(XslTransformerCciTest.class);
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\">";
	private static final String SPACE = "\\s+";

	private XslTransformer cimsTransformer;
	private String sourceXmlString;
	private String expectedHtml;

	@Before
	public void setUp() {
		final Resource cimsXSL = new ClassPathResource("/stylesheet/cims.xsl", this.getClass());

		Assert.assertTrue("The test xsl file must exist.", cimsXSL.exists());

		cimsTransformer = new XslTransformerFactory().create(cimsXSL);

	}

	@Test
	public void testBlockTransformation() {
		LOGGER.debug("XslTransformerCciTest.testBlockTransformation()...");
		// Block 1
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>2AA-2ZZ</CODE><PRESENTATION_CODE>2AA-2ZZ</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK1</PRESENTATION_TYPE_CODE><USER_DESC>Tabular List of Diagnostic Interventions</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"cci_bl1\" colspan=\"3\"><a name=\"2AA-2ZZ\">Tabular List of Diagnostic Interventions</a></td><td/></tr><tr><td colspan=\"4\" height=\"5px\"/></tr>";
		List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());

		// Block 2
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>2AA-2BX</CODE><PRESENTATION_CODE>2AA-2BX</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK2</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Nervous System (2AA - 2BX)</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"cci_bl2\" colspan=\"3\"><a name=\"2AA-2BX\">Diagnostic Interventions on the Nervous System (2AA - 2BX)</a></td><td/></tr><tr><td colspan=\"4\" height=\"5px\"/></tr>";
		errors.clear();
		html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());

		// Block 3
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>2AA-2AZ</CODE><PRESENTATION_CODE>2AA-2AZ</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK3</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Brain and Spinal Cord (2AA - 2AZ)</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"cci_bl3\" colspan=\"3\"><a name=\"2AA-2AZ\">Diagnostic Interventions on the Brain and Spinal Cord (2AA - 2AZ)</a></td><td/></tr><tr><td colspan=\"4\" height=\"5px\"/></tr>";
		errors.clear();
		html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testGrpTransformation() {
		LOGGER.debug("XslTransformerCciTest.testGrpTransformation()...");
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\">"
				+ "<concept><language>ENG</language><classification>CCI</classification><CODE>2AA</CODE><PRESENTATION_CODE>2AA</PRESENTATION_CODE><TYPE_CODE>GROUP</TYPE_CODE><PRESENTATION_TYPE_CODE>GROUP</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Meninges and Dura Mater of Brain</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"includes\"><include><label>Bursa, capsule, cartilage, ligament and synovial lining of coracohumeral and glenohumeral joints</label></include><include><label>Shoulder ligaments [coracohumeral, glenohumeral, glenoid labrum, humeral and rotator interval]</label></include><include><label>Glenoid cavity</label></include><include><label>Labrum</label></include><include><label>Humeral head and surgical neck</label></include><include><label>Greater tuberosity [tubercle] of humerus</label></include><include><label>Shoulder joint NOS</label></include></qualifierlist><qualifierlist type=\"excludes\"><exclude><label>Acromioclavicular and sternoclavicular joints </label></exclude></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"grp\"><a name=\"2AA\">2AA</a></td><td class=\"grp\" colspan=\"2\">Diagnostic Interventions on the Meninges and Dura Mater of Brain</td><td/></tr><tr><td colspan=\"4\" height=\"3px\"/></tr><tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Bursa, capsule, cartilage, ligament and synovial lining of coracohumeral and glenohumeral joints<br/>Glenoid cavity<br/>Greater tuberosity [tubercle] of humerus<br/>Humeral head and surgical neck<br/>Labrum<br/>Shoulder joint NOS<br/>Shoulder ligaments [coracohumeral, glenohumeral, glenoid labrum, humeral and rotator interval]<br/></td><td/></tr><tr valign=\"top\"><td class=\"exclude\"/><td class=\"excludelabel\">Excludes:</td><td class=\"exclude\">Acromioclavicular and sternoclavicular joints <br/></td><td/></tr>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());
	}

	/**
	 * Tests sorting of 'Includes' in cases where descriptions contain commas
	 * 
	 * @see CSRE-148
	 */
	@Test
	public void testIncludesTransformationWithCommaSorted() {
		LOGGER.debug("XslTransformerCciTest.testIncludesTransformationWithCommaSorted()...");
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>1.PE.57.^^</CODE><PRESENTATION_CODE>1.PE.57.^^</PRESENTATION_CODE><TYPE_CODE>SECTION</TYPE_CODE><PRESENTATION_TYPE_CODE>SECTION</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"includes\"><include><label>Extraction, kidney stones</label></include><include><label>Extraction with manipulation [calculi], kidney</label></include><include><label>Extraction with manipulation [calculi], ureteropelvic junction (UPJ)</label></include><include><label>Nephrotomy for removal of stones [nephrolithotomy]</label></include><include><label>Nephrostolithotomy</label></include><include><label>Pelviolithotomy, renal</label></include><include><label>Pyelolithotomy</label></include><include><label>Pyelostolithotomy</label></include><include><label>Pyelotomy for removal of stones</label></include><include><label>Removal of calculi [or clot], renal pelvis (or renal calyx)</label></include><include><label>Removal of calculi [or clot], ureteropelvic junction (UPJ)</label></include><include><label>Ureteropyelotomy with manipulation [calculi], renal pelvis</label></include></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"sec\" colspan=\"3\"><a name=\"1.PE.57.^^\">Section 1.PE.57.^^ - Diagnostic Interventions</a></td><td/></tr><tr><td colspan=\"4\" height=\"10px\"/></tr><br/><tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Extraction with manipulation [calculi], kidney<br/>Extraction with manipulation [calculi], ureteropelvic junction (UPJ)<br/>Extraction, kidney stones<br/>Nephrostolithotomy<br/>Nephrotomy for removal of stones [nephrolithotomy]<br/>Pelviolithotomy, renal<br/>Pyelolithotomy<br/>Pyelostolithotomy<br/>Pyelotomy for removal of stones<br/>Removal of calculi [or clot], renal pelvis (or renal calyx)<br/>Removal of calculi [or clot], ureteropelvic junction (UPJ)<br/>Ureteropyelotomy with manipulation [calculi], renal pelvis<br/></td><td/></tr>";

		List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);
		assertEquals(0, errors.size());
		assertNotNull(html);
		assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
	}

	@Test
	public void testRubTableTransformation() {
		LOGGER.debug("XslTransformerCciTest.testRubTableTransformation()...");
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>1.AB.86.^^</CODE><PRESENTATION_CODE>1.AB.86.^^</PRESENTATION_CODE><TYPE_CODE>RUBRIC</TYPE_CODE><PRESENTATION_TYPE_CODE>RUBRIC</PRESENTATION_TYPE_CODE><USER_DESC>Closure of fistula, subarachnoid</USER_DESC><CONCEPT_DETAIL><CLOB><table cols=\"4\" colwidth=\"216pt 72pt 88pt 84pt\" type=\"portrait\"><thead><tr><td><xref refid=\"1AB86\">1.AB.86.^^</xref> Closure of fistula, subarachnoid</td><td>using apposition technique [e.g. suture]</td><td>using autograft [e.g. fascia lata, pericranium, fat, muscle or bone]</td><td>using fibrin [glue]</td></tr></thead><tbody><tr><td>for fistula terminating at skin</td><td><xref refid=\"1AB86MB\">1.AB.86.MB</xref></td><td><xref refid=\"1AB86MBXXA\">1.AB.86.MB-XX-A</xref></td><td><xref refid=\"1AB86MBW3\">1.AB.86.MB-W3</xref></td></tr><tr><td>for fistula terminating in ear</td><td><xref refid=\"1AB86MS\">1.AB.86.MS</xref></td><td><xref refid=\"1AB86MSXXA\">1.AB.86.MS-XX-A</xref></td><td><xref refid=\"1AB86MSW3\">1.AB.86.MS-W3</xref></td></tr><tr><td>for fistula terminating in head and neck [e.g. subdural space]</td><td><xref refid=\"1AB86MJ\">1.AB.86.MJ</xref></td><td><xref refid=\"1AB86MJXXA\">1.AB.86.MJ-XX-A</xref></td><td><xref refid=\"1AB86MJW3\">1.AB.86.MJ-W3</xref></td></tr><tr><td>for fistula terminating in nasal (oral) cavity</td><td><xref refid=\"1AB86ML\">1.AB.86.ML</xref></td><td><xref refid=\"1AB86MLXXA\">1.AB.86.ML-XX-A</xref></td><td><xref refid=\"1AB86MLW3\">1.AB.86.ML-W3</xref></td></tr></tbody></table><qualifierlist type=\"includes\"><include><label>Repair, fistula, CSF of brain</label></include></qualifierlist><qualifierlist type=\"also\"><also><label>Any concomitant insertion of shunt system (see <xref refid=\"1AC52\">1.AC.52.^^</xref>)</label></also></qualifierlist></CLOB></CONCEPT_DETAIL></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"rub\" valign=\"top\"><a name=\"1.AB.86.^^\">1.AB.86.^^</a></td><td class=\"rub\" colspan=\"2\"><table border=\"0\" valign=\"bottom\"><tr><td width=\"610px\" valign=\"bottom\">Closure of fistula, subarachnoid</td><td width=\"50px\"><img src=\"img/v grey.png\" alt=\" V \"/></td></tr></table></td><td/></tr><tr><td colspan=\"4\" height=\"3px\"/></tr><tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Repair, fistula, CSF of brain<br/></td><td/></tr><tr valign=\"top\"><td class=\"codealso\"/><td class=\"codealsolabel\">Code Also:</td><td class=\"codealso\">Any concomitant insertion of shunt system (see <a  href=\"#1AC52\">1.AC.52.^^</a>)<br/></td><td/></tr><tr><td colspan=\"4\"><table border=\"1\" style=\"width:auto !important; margin:0 auto;\"><thead class=\"rubhead\"><tr valign=\"top\"><td width=\"287.28000000000003px\" style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86\">1.AB.86.^^</a> Closure of fistula, subarachnoid</td><td width=\"95.76px\" style=\"text-align: left;vertical-align: text-top;\">using apposition technique [e.g. suture]</td><td width=\"117.04px\" style=\"text-align: left;vertical-align: text-top;\">using autograft [e.g. fascia lata, pericranium, fat, muscle or bone]</td><td width=\"111.72px\" style=\"text-align: left;vertical-align: text-top;\">using fibrin [glue]</td></tr></thead><tbody class=\"rubbody\"><tr valign=\"center\"><td style=\"text-align: left;vertical-align: text-top;\">for fistula terminating at skin</td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MB\">1.AB.86.MB</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MBXXA\">1.AB.86.MB-XX-A</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MBW3\">1.AB.86.MB-W3</a></td></tr><tr valign=\"center\"><td style=\"text-align: left;vertical-align: text-top;\">for fistula terminating in ear</td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MS\">1.AB.86.MS</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MSXXA\">1.AB.86.MS-XX-A</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MSW3\">1.AB.86.MS-W3</a></td></tr><tr valign=\"center\"><td style=\"text-align: left;vertical-align: text-top;\">for fistula terminating in head and neck [e.g. subdural space]</td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MJ\">1.AB.86.MJ</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MJXXA\">1.AB.86.MJ-XX-A</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MJW3\">1.AB.86.MJ-W3</a></td></tr><tr valign=\"center\"><td style=\"text-align: left;vertical-align: text-top;\">for fistula terminating in nasal (oral) cavity</td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86ML\">1.AB.86.ML</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MLXXA\">1.AB.86.ML-XX-A</a></td><td style=\"text-align: left;vertical-align: text-top;\"><a  href=\"#1AB86MLW3\">1.AB.86.ML-W3</a></td></tr></tbody></table></td></tr><tr><td colspan=\"4\" height=\"15px\"/></tr>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testRubTransformation() {
		LOGGER.debug("XslTransformerCciTest.testRubTransformation()...");
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>2.AF.71.^^</CODE><PRESENTATION_CODE>2.AF.71.^^</PRESENTATION_CODE><TYPE_CODE>RUBRIC</TYPE_CODE><PRESENTATION_TYPE_CODE>RUBRIC</PRESENTATION_TYPE_CODE><HAS_VALIDATION>true</HAS_VALIDATION><ATTRIBUTES><ATTRIBUTE><TYPE>S</TYPE><HAS_REF>true</HAS_REF><MANDATORY>true</MANDATORY><REF_CODE>S32</REF_CODE></ATTRIBUTE><ATTRIBUTE><TYPE>L</TYPE><HAS_REF>true</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE>L22</REF_CODE></ATTRIBUTE><ATTRIBUTE><TYPE>E</TYPE><HAS_REF>true</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE></REF_CODE></ATTRIBUTE></ATTRIBUTES><USER_DESC>Biopsy, pituitary region</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"also\"><also><label>Any intraoperative stereotactic or computer guidance (see <xref refid=\"3AN94\">3.AN.94.^^</xref>)</label></also></qualifierlist></CLOB></CONCEPT_DETAIL><CODE_LIST hasCode=\"true\"><codeConcept><CODE_CONCEPT_CODE>2.AF.71.GR</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using percutaneous transluminal approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB><qualifierlist type=\"includes\"><include><label>Petrosal sinus sampling (for elevated ACTH secretions) </label></include></qualifierlist></CODE_CLOB></CODE_DETAIL></codeConcept><codeConcept><CODE_CONCEPT_CODE>2.AF.71.QS</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using open trans sphenoidal [trans ethmoidal] approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB /></CODE_DETAIL></codeConcept><codeConcept><CODE_CONCEPT_CODE>2.AF.71.SZ</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using open transfrontal [craniotomy flap] approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB /></CODE_DETAIL></codeConcept></CODE_LIST></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"rub\" valign=\"top\"><a name=\"2.AF.71.^^\">2.AF.71.^^</a></td><td class=\"rub\" colspan=\"2\"><table border=\"0\" valign=\"bottom\"><tr><td width=\"610px\" valign=\"bottom\">Biopsy, pituitary region</td><td width=\"50px\"><a href=\"javascript:popupAttribute('attributePopup.htm?refid=S32&language=ENG&classification=CCI');\"><img src=\"img/cci/s pink.png\" alt=\"S\"/></a><span class=\"superscript\">32</span></td><td width=\"50px\"><a href=\"javascript:popupAttribute('attributePopup.htm?refid=L22&language=ENG&classification=CCI');\"><img src=\"img/cci/l yellow.png\" alt=\"L\"/></a><span class=\"superscript\">22</span></td><td width=\"50px\"><a href=\"javascript:popupAttribute('attributePopup.htm?refid=&language=ENG&classification=CCI');\"><img src=\"img/cci/e yellow.png\" alt=\"E\"/></a><span class=\"superscript\"/></td><td width=\"50px\"><a href=\"javascript:popupCciValidation('cciValidationPopup.htm?refid=2.AF.71.^^&language=ENG&classification=CCI');\"><img src=\"img/v purple.png\" alt=\" V \"/></a></td></tr></table></td><td/></tr><tr><td colspan=\"4\" height=\"3px\"/></tr><tr valign=\"top\"><td class=\"codealso\"/><td class=\"codealsolabel\">Code Also:</td><td class=\"codealso\">Any intraoperative stereotactic or computer guidance (see <a href=\"#3AN94\">3.AN.94.^^</a>)<br/></td><td/></tr><tr valign=\"top\"><td/><td colspan=\"2\"><table width=\"80%\" class=\"codeTable\"><tr class=\"sm-text\"><td width=\"20%\"><a name=\"2.AF.71.GR\">2.AF.71.GR</a></td><td>using percutaneous transluminal approach</td></tr><tr><td valign=\"top\" class=\"includelabel\">Includes:</td><td valign=\"top\" class=\"tbl-note\">Petrosal sinus sampling (for elevated ACTH secretions) <br/></td></tr><tr class=\"sm-text\"><td width=\"20%\"><a name=\"2.AF.71.QS\">2.AF.71.QS</a></td><td>using open trans sphenoidal [trans ethmoidal] approach</td></tr><tr class=\"sm-text\"><td width=\"20%\"><a name=\"2.AF.71.SZ\">2.AF.71.SZ</a></td><td>using open transfrontal [craniotomy flap] approach</td></tr></table><br/></td><td/></tr><tr><td colspan=\"4\" height=\"10px\"/></tr>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);

		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testSecTransformation() {
		LOGGER.debug("XslTransformerCciTest.testSecTransformation()...");
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\"><concept><language>ENG</language><classification>CCI</classification><CODE>02</CODE><PRESENTATION_CODE>02</PRESENTATION_CODE><TYPE_CODE>SECTION</TYPE_CODE><PRESENTATION_TYPE_CODE>SECTION</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"excludes\"><exclude><label>Diagostic imaging interventions (see Section 3)</label></exclude><exclude><label>Diagostic interventions unique to the state of pregnancy or to the fetus (see Section 5)</label></exclude><exclude><label>Routine, preventative or screening dental, health or eye examinations (see Section 7)</label></exclude></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"sec\" colspan=\"3\"><a name=\"02\">Section 02 - Diagnostic Interventions</a></td><td/></tr><tr><td colspan=\"4\" height=\"10px\"/></tr><br/><tr valign=\"top\"><td class=\"exclude\"/><td class=\"excludelabel\">Excludes:</td><td class=\"exclude\">Diagostic imaging interventions (see Section 3)<br/>Diagostic interventions unique to the state of pregnancy or to the fetus (see Section 5)<br/>Routine, preventative or screening dental, health or eye examinations (see Section 7)<br/></td><td/></tr>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = cimsTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(expectedHtml.replaceAll(SPACE, "").equals(html.replaceAll(SPACE, "")));
		Assert.assertEquals(0, errors.size());
	}
}