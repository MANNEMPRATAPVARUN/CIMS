package ca.cihi.cims.util;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import ca.cihi.cims.content.icd.IcdValidationXml;

public class XmlUtilsTest {

	@Test
	public void testDeserilization() throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\">"
				+ "<validation classification=\"ICD-10-CA\" language=\"\">" + "<ELEMENT_ID>972318</ELEMENT_ID>"
				+ "<GENDER_CODE>A</GENDER_CODE>" + "<GENDER_DESC_ENG>Male, Fem &amp; Other</GENDER_DESC_ENG>"
				+ "<GENDER_DESC_FRA>Hom, Fem &amp; Autre</GENDER_DESC_FRA>" + "<AGE_RANGE>0-130</AGE_RANGE>"
				+ "<MRDX_MAIN>Y</MRDX_MAIN><DX_TYPE_1>Y</DX_TYPE_1><DX_TYPE_2>Y</DX_TYPE_2>"
				+ "<DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9>"
				+ "<DX_TYPE_W>Y</DX_TYPE_W><DX_TYPE_X>Y</DX_TYPE_X><DX_TYPE_Y>Y</DX_TYPE_Y><NEW_BORN>N</NEW_BORN>"
				+ "</validation>";
		IcdValidationXml obj = XmlUtils.deserialize(IcdValidationXml.class, xml);
		assertEquals("ICD-10-CA", obj.getClassification());
		assertEquals("", obj.getLanguage());
		assertEquals(972318, obj.getElementId());
		assertEquals("0-130", obj.getAgeRange());
		assertEquals("N", obj.getNewBorn());

	}

	@Test
	public void testSerilization() throws Exception {
		IcdValidationXml obj = new IcdValidationXml();
		obj.setClassification("ICD-10-CA");
		obj.setLanguage("EN");
		obj.setAgeRange("0-130");
		obj.setElementId(972318);
		obj.setNewBorn("N");
		String xml = XmlUtils.serialize(obj);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><validation classification=\"ICD-10-CA\" language=\"EN\"><ELEMENT_ID>972318</ELEMENT_ID><AGE_RANGE>0-130</AGE_RANGE><NEW_BORN>N</NEW_BORN></validation>",
				xml);
	}

}
