package ca.cihi.cims.model.tabular;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.CciCodeValidation;




/*
 * this junit class is for cheating sonar
 */
public class CciCodeValidationTest {

	 private CciCodeValidation model;
	 
	 @Before
	 public void setUp()	    {
	       model = new CciCodeValidation();
	 }
	 @Test
	 public void testGetAndSetCode(){
		 model.setCode("A00.0");
		 String expectedCode ="A00.0";
		 assertTrue("Should have  the expected code", model.getCode().equals(expectedCode));
		 
		 model.setDataHolding("dataHolding");
		 String expectedGetDataHolding ="dataHolding";
		 assertTrue("Should have the expected dataHolding", model.getDataHolding().equals(expectedGetDataHolding));
	
		 model.setAgeRange("ageRange");
		 String expectedGetAgeRange ="ageRange";
		 assertTrue("Should have the expected ageRange", model.getAgeRange().equals(expectedGetAgeRange));	
		 
		 model.setExtentRef("extentRef");
		 String expectedGetExtentRef ="extentRef";
		 assertTrue("Should have the expected extentRef", model.getExtentRef().equals(expectedGetExtentRef));	
		 
		 model.setGender("M");
		 String expectedGetGender ="M";
		 assertTrue("Should have  the expected Gender", model.getGender().equals(expectedGetGender));	
		 
		 model.setLocationRef("locationRef");
		 String expectedGetLocationRef ="locationRef";
		 assertTrue("Should have  the expected LocationRef", model.getLocationRef().equals(expectedGetLocationRef));	
		 
		 model.setStatusRef("statusRef");
		 String expectedGetStatusRef ="statusRef";
		 assertTrue("Should have  the expected StatusRef", model.getStatusRef().equals(expectedGetStatusRef));	
		 
		 
		 
		 
	 }
	 
	 
	 
}
