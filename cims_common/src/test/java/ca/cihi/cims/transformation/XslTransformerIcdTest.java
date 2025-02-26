package ca.cihi.cims.transformation;

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
public class XslTransformerIcdTest {

	private static final Log LOGGER = LogFactory.getLog(XslTransformerIcdTest.class);
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims.dtd\">";

	private String expectedHtml;
	private XslTransformer icdTransformer;
	private String sourceXmlString;

	@Before
	public void setUp() {
		final Resource cimsXSL = new ClassPathResource("/stylesheet/cims.xsl", this.getClass());

		Assert.assertTrue("The test xsl file must exist.", cimsXSL.exists());

		icdTransformer = new XslTransformerFactory().create(cimsXSL);

	}

	@Test
	public void testAlso() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>01_A</CODE><PRESENTATION_CODE>01_A</PRESENTATION_CODE><TYPE_CODE>CODE_ALSO</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE_ALSO</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB><qualifierlist type=\"also\"><also><label>See Test</label></also></qualifierlist>   </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"codealso\"/><td class=\"codealsolabel\">Code Also:</td><td class=\"codealso\">See Test<br/></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testBL1() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A00-A09</CODE><PRESENTATION_CODE>A00-A09</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>BLOCK1</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Intestinal infectious diseases (A00-A09)</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"bl1\" colspan=\"3\"><a name=\"A00-A09\">Intestinal infectious diseases (A00-A09)</a></td><td/></tr><tr><td colspan=\"4\" height=\"5px\"/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testBraceA154I() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A154_I</CODE><PRESENTATION_CODE>A154_I</PRESENTATION_CODE><TYPE_CODE>INCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>INCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A154_I</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A154_I</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB> <qualifierlist type=\"includes\"><include>"
				+ "<!-- *** BRACE *** --><brace cols=\"3\"> <label>Tuberculosis of lymph nodes:</label> <segment bracket=\"right\" size=\"03\">  <item>  <ulist>   <listitem>hilar</listitem>   <listitem>mediastinal</listitem>   <listitem>tracheobronchial</listitem>  </ulist>  </item> </segment> <segment>  <item>confirmed bacteriologically and histologically</item> </segment></brace>"
				+ "</include></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\"><table width=\"100%\"><tbody class=\"sm-text\"><tr><td colspan=\"3\">Tuberculosis of lymph nodes:</td></tr><tr><td width=\"25%\" nowrap=\"true\"><ul style=\"margin-top: 5; margin-bottom: 0;\"><li class=\"sm-text\">hilar</li><li class=\"sm-text\">mediastinal</li><li class=\"sm-text\">tracheobronchial</li></ul></td><td><img src=\"img/icd/bracket_03.gif\" alt=\"bracket_03.gif\"/></td><td wrappable=\"true\">confirmed bacteriologically and histologically<br/></td></tr></tbody></table></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testBraceCaEnhancement() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A1500</CODE><PRESENTATION_CODE>A1500</PRESENTATION_CODE><TYPE_CODE>CODE</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>true</CA_ENHANCEMENT_FLAG><USER_DESC>Tuberculosis of lung, confirmed by sputum microscopy with or without culture, with cavitation</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A15.00</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A15.00</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"code\"><a name=\"A1500\">A15.00</a> <img align=\"texttop\" src=\"img/icd/cleaf.gif\" height=\"15\" width=\"15\"/></td><td class=\"code\" colspan=\"2\">Tuberculosis of lung, confirmed by sputum microscopy with or without culture, with cavitation</td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testCAT1A00() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A00</CODE><PRESENTATION_CODE>A00</PRESENTATION_CODE><TYPE_CODE>CATEGORY</TYPE_CODE><PRESENTATION_TYPE_CODE>CATEGORY1</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>Cholera</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A00</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A00</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"cat1\"><a name=\"A00\">A00</a></td><td class=\"cat1\" colspan=\"2\">Cholera<img src=\"img/v grey.png\" alt=\" V \"/></td><td/></tr><tr><td colspan=\"4\" height=\"3px\"/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testChapter() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>01</CODE><PRESENTATION_CODE>I</PRESENTATION_CODE><TYPE_CODE>CHAPTER</TYPE_CODE><PRESENTATION_TYPE_CODE>CHAPTER</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Certain infectious and parasitic diseases (A00-B99)</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"chp\" colspan=\"3\"><a name=\"01\">Chapter I - Certain infectious and parasitic diseases (A00-B99)</a></td><td/></tr><tr><td colspan=\"4\" height=\"10px\"/></tr><br/>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	private void testCimsXsl(final String xmlString, final String expectedHtml) {
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = icdTransformer.transform(xmlString, errors);
		LOGGER.debug("testCimsXsl resultHtml:" + html);
		System.out.println("html:" + html);
		System.out.println("expectedHtml:" + expectedHtml);

		if (html == null) {
			Assert.assertTrue(errors.isEmpty());
		} else {
			html = html.replaceAll("\\s+", "");
			LOGGER.debug("testCimsXsl resultHtml:" + html);

			final String expectedHtmlStr = expectedHtml.replaceAll("\\s+", "");

			Assert.assertTrue(html.equals(expectedHtmlStr));
			Assert.assertEquals(0, errors.size());
		}
	}

	@Test
	public void testCodeA000() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A000</CODE><PRESENTATION_CODE>A000</PRESENTATION_CODE><TYPE_CODE>CODE</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Cholera due to Vibrio cholerae 01, biovar cholerae</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A00.0 </CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A00.0</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr>\r\n<td class=\"code\"><a name=\"A000\">A00.0 </a></td><td class=\"code\" colspan=\"2\">Cholera due to Vibrio cholerae 01, biovar cholerae</td>\r\n<td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testCodeA001() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A001</CODE><PRESENTATION_CODE>A001</PRESENTATION_CODE><TYPE_CODE>CODE</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Cholera due to Vibrio cholerae 01, biovar eltor</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A00.1</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A00.1</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr>\r\n<td class=\"code\"><a name=\"A001\">A00.1</a></td><td class=\"code\" colspan=\"2\">Cholera due to Vibrio cholerae 01, biovar eltor</td>\r\n<td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	/**
	 * Tests sorting of 'Code Also' in cases where descriptions contain commas
	 *
	 * @see CSRE-148
	 */
	@Test
	public void testCodeAlsoWithCommaSort() {
		sourceXmlString = XML_HEADER
				+ "<concept> <language>ENG</language> <classification>ICD-10-CA</classification> <CODE>E10.28</CODE> <PRESENTATION_CODE>E10.28</PRESENTATION_CODE> <TYPE_CODE>CATEGORY</TYPE_CODE> <PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE> <CA_ENHANCEMENT_FLAG>true</CA_ENHANCEMENT_FLAG> <USER_DESC>Type 1 diabetes mellitus with other specified kidney complication not elsewhere classified</USER_DESC> <CONCEPT_CODE_WITH_DECIMAL_DAGGAR>E10.28</CONCEPT_CODE_WITH_DECIMAL_DAGGAR> <CONCEPT_CODE_WITH_DECIMAL>E10.28</CONCEPT_CODE_WITH_DECIMAL> <CONCEPT_DETAIL> <CLOB><qualifierlist type=\"also\"> 	<also> 		<ulist mark=\"bullet\"> 			<label>Code separately any of the following associated conditions: </label> 			<listitem>acute renal failure, unspecified (<xref refid=\"N17.9\">N17.9</xref>) </listitem> 			<listitem>acute renal failure with medullary (papillary) necrosis (<xref refid=\"N17.2\">N17.2</xref>)</listitem> 			<listitem>postprocedural renal failure (<xref refid=\"N99.0\">N99.0</xref>) </listitem> 		</ulist> 	</also> </qualifierlist> </CLOB> </CONCEPT_DETAIL> <BLOCK_LIST hasBlock=\"false\" /> <ASTERISK_LIST hasAsterisk=\"false\" /> </concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"code\"><a name=\"E10.28\">E10.28</a> <img align=\"texttop\" src=\"img/icd/cleaf.gif\" height=\"15\" width=\"15\"/></td><td class=\"code\" colspan=\"2\">Type 1 diabetes mellitus with other specified kidney complication not elsewhere classified</td><td/></tr><tr valign=\"top\"><td class=\"codealso\"/><td class=\"codealsolabel\">Code Also:</td><td class=\"codealso\">Code separately any of the following associated conditions: <ul style=\"margin-top: 0; margin-bottom: 0;\"><li class=\"sm-text\">acute renal failure with medullary (papillary) necrosis (<a href=\"#N17.2\">N17.2</a>)</li><li class=\"sm-text\">acute renal failure, unspecified (<a href=\"#N17.9\">N17.9</a>) </li><li class=\"sm-text\">postprocedural renal failure (<a href=\"#N99.0\">N99.0</a>) </li></ul></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	// note: this TYPE_CODE get changed to ROMAN Number
	@Test
	public void testDTD() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>02</CODE><PRESENTATION_CODE>II</PRESENTATION_CODE><TYPE_CODE>CHAPTER</TYPE_CODE><PRESENTATION_TYPE_CODE>CHAPTER</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Certain infectious and parasitic diseases (A00-B99)</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr><td colspan=\"4\" height=\"10px\"/></tr><tr><td class=\"chp\" colspan=\"3\"><a name=\"02\">Chapter II - Certain infectious and parasitic diseases (A00-B99)</a></td><td/></tr><tr><td colspan=\"4\" height=\"10px\"/></tr><br/>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testExceptionHandler() {
		sourceXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE concept SYSTEM \"/dtd/cihi_cims_test.dtd\">"
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>02</CODE><TYPE_CODE>CHAPTER</TYPE_CODE><PRESENTATION_TYPE_CODE>CHAPTER</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Certain infectious and parasitic diseases (A00-B99)</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String html = icdTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(html.indexOf("IOException") != -1);
		Assert.assertFalse(errors.isEmpty());

		// SAXException
		sourceXmlString = XML_HEADER
				+ "<concept1><language>ENG</language><classification>ICD-10-CA</classification><CODE>02</CODE><PRESENTATION_CODE>02</PRESENTATION_CODE><TYPE_CODE>CHAPTER</TYPE_CODE><PRESENTATION_TYPE_CODE>CHAPTER</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>Certain infectious and parasitic diseases (A00-B99)</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		errors.clear();
		html = icdTransformer.transform(sourceXmlString, errors);
		Assert.assertTrue(html.indexOf("ERROR") != -1);
		Assert.assertFalse(errors.isEmpty());
	}

	@Test
	public void testExclude() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A04_E</CODE><PRESENTATION_CODE>A04_E</PRESENTATION_CODE><TYPE_CODE>EXCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>EXCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A04_E</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A04_E</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB><qualifierlist type=\"excludes\"><exclude><label>foodborne intoxications, elsewhere classified (<xref refid=\"A05\">A05</xref>.-)"
				+ "</label></exclude><exclude><label>tuberculous enteritis (<xref refid=\"A183\">A18.3</xref>)</label></exclude></qualifierlist>    </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		expectedHtml = "<tr valign=\"top\"><td class=\"exclude\"/><td class=\"excludelabel\">Excludes:</td><td class=\"exclude\">foodborne intoxications, elsewhere classified (<a href=\"#A05\">A05</a>.-)<br/>tuberculous enteritis (<a href=\"#A183\">A18.3</a>)<br/></td><td/></tr>";

		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testExcludeAndSortAlphabetically() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A04_E</CODE><PRESENTATION_CODE>A04_E</PRESENTATION_CODE><TYPE_CODE>EXCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>EXCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A04_E</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A04_E</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB><qualifierlist type=\"excludes\"><exclude><label>tuberculous enteritis (<xref refid=\"A183\">A18.3</xref>)"
				+ "</label></exclude><exclude><label>foodborne intoxications, elsewhere classified (<xref refid=\"A05\">A05</xref>.-)</label></exclude></qualifierlist>    </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		expectedHtml = "<tr valign=\"top\"><td class=\"exclude\"/><td class=\"excludelabel\">Excludes:</td><td class=\"exclude\">foodborne intoxications, elsewhere classified (<a href=\"#A05\">A05</a>.-)<br/>tuberculous enteritis (<a href=\"#A183\">A18.3</a>)<br/></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testGlobalSpecification() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A022_I</CODE><PRESENTATION_CODE>A022_I</PRESENTATION_CODE><TYPE_CODE>INCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>INCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A022_I</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A022_I</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB>  <qualifierlist type=\"includes\"><include><ulist><label>Salmonella</label><listitem>arthritis   </listitem><listitem>meningitis   </listitem><listitem>osteomyelitis    </listitem><listitem>pneumonia   </listitem><listitem>renal tubulo-interstitial disease   </listitem></ulist></include></qualifierlist>  </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Salmonella<ul style=\"margin-top: 0; margin-bottom: 0;\"><li class=\"sm-text\">arthritis   </li><li class=\"sm-text\">meningitis   </li><li class=\"sm-text\">osteomyelitis    </li><li class=\"sm-text\">pneumonia   </li><li class=\"sm-text\">renal tubulo-interstitial disease   </li></ul></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testInclude() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>01_I</CODE><PRESENTATION_CODE>01_I</PRESENTATION_CODE><TYPE_CODE>INCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>INCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB><qualifierlist type=\"includes\"><include><label>diseases generally recognized as communicable or transmissible</label></include></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">diseases generally recognized as communicable or transmissible<br/></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testIncludeAndSortAlphabetically() {

		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>01_I</CODE><PRESENTATION_CODE>01_I</PRESENTATION_CODE><TYPE_CODE>INCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>INCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL>"
				+ "<CLOB><qualifierlist type=\"includes\"><include><label>b diseases generally</label></include><include><label>a diseases generally</label></include><include><label>NOS</label></include></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">NOS<br/>a diseases generally<br/>b diseases generally<br/></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testLink() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>01_A</CODE><PRESENTATION_CODE>01_A</PRESENTATION_CODE><TYPE_CODE>CODE_ALSO</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE_ALSO</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR/><CONCEPT_CODE_WITH_DECIMAL/><CONCEPT_DETAIL><CLOB>   <qualifierlist type=\"also\"><also><label><xref refid=\"U82\">U82-U84</xref></label></also></qualifierlist>   </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"codealso\"/><td class=\"codealsolabel\">Code Also:</td><td class=\"codealso\"><a href=\"#U82\">U82-U84</a><br/></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}

	@Test
	public void testList() {
		sourceXmlString = XML_HEADER
				+ "<concept><language>ENG</language><classification>ICD-10-CA</classification><CODE>A022_I</CODE><PRESENTATION_CODE>A022_I</PRESENTATION_CODE><TYPE_CODE>INCLUDE</TYPE_CODE><PRESENTATION_TYPE_CODE>INCLUDE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG></CA_ENHANCEMENT_FLAG><USER_DESC>null</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A022_I</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A022_I</CONCEPT_CODE_WITH_DECIMAL><CONCEPT_DETAIL><CLOB>  <qualifierlist type=\"includes\"><include><ulist><label>Salmonella</label><listitem>arthritis   </listitem><listitem>meningitis   </listitem><listitem>osteomyelitis    </listitem><listitem>pneumonia   </listitem><listitem>renal tubulo-interstitial disease   </listitem></ulist></include></qualifierlist>  </CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"></BLOCK_LIST><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";
		expectedHtml = "<tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Salmonella<ul style=\"margin-top: 0; margin-bottom: 0;\"><li class=\"sm-text\">arthritis   </li><li class=\"sm-text\">meningitis   </li><li class=\"sm-text\">osteomyelitis    </li><li class=\"sm-text\">pneumonia   </li><li class=\"sm-text\">renal tubulo-interstitial disease   </li></ul></td><td/></tr>";
		testCimsXsl(sourceXmlString, expectedHtml);
	}
}