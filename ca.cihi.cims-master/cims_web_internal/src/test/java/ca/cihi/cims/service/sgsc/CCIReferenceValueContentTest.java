package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ViewService;

public class CCIReferenceValueContentTest {

	CCIReferenceValueContent content = null;
	@Mock
	ViewService viewService;

	private List<CCIReferenceAttribute> mockExtentResults() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("E01");
		attribute2.setDescription("3D reconstruction");
		List<CCIGenericAttribute> genericAttributes2 = new ArrayList<CCIGenericAttribute>();

		CCIGenericAttribute geAttribute21 = new CCIGenericAttribute();
		geAttribute21.setCode("3D");
		geAttribute21.setDescription("With 3-dimensional (3D) recons");
		genericAttributes2.add(geAttribute21);

		CCIGenericAttribute geAttribute22 = new CCIGenericAttribute();
		geAttribute22.setCode("PL");
		geAttribute22.setDescription("With planar reconstruction");
		genericAttributes2.add(geAttribute22);

		attribute2.setGenericAttributes(genericAttributes2);
		attributes.add(attribute2);

		return attributes;
	}

	private List<CCIReferenceAttribute> mockLocationResults() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("L15");
		attribute1.setDescription("Abdominal veins");
		List<CCIGenericAttribute> genericAttributes1 = new ArrayList<CCIGenericAttribute>();
		CCIGenericAttribute geAttribute1 = new CCIGenericAttribute();
		geAttribute1.setCode("1");
		geAttribute1.setDescription("One vessel");
		genericAttributes1.add(geAttribute1);

		CCIGenericAttribute geAttribute21 = new CCIGenericAttribute();
		geAttribute21.setCode("2");
		geAttribute21.setDescription("Two vessels");
		genericAttributes1.add(geAttribute21);

		attribute1.setGenericAttributes(genericAttributes1);
		attributes.add(attribute1);

		return attributes;
	}

	private List<CCIReferenceAttribute> mockModeResults() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("M01");
		attribute1.setDescription("Type of delivery 1");
		List<CCIGenericAttribute> genericAttributes1 = new ArrayList<CCIGenericAttribute>();
		CCIGenericAttribute geAttribute1 = new CCIGenericAttribute();
		geAttribute1.setCode("DI");
		geAttribute1.setDescription("Direct (service delivered in person by health care provider)");
		genericAttributes1.add(geAttribute1);

		CCIGenericAttribute geAttribute21 = new CCIGenericAttribute();
		geAttribute21.setCode("IN");
		geAttribute21.setDescription(
				"Indirect (service delivered at a distance e.g. telephone, telemedicine with health care provider not physically present with client)");
		genericAttributes1.add(geAttribute21);

		CCIGenericAttribute geAttribute22 = new CCIGenericAttribute();
		geAttribute22.setCode("SD");
		geAttribute22.setDescription(
				"Self directed (e.g. using tapes, video, books, interactive computer) with or without supervision or coaching from a health care provider");
		genericAttributes1.add(geAttribute22);

		attribute1.setGenericAttributes(genericAttributes1);
		attributes.add(attribute1);

		return attributes;
	}

	private List<CCIReferenceAttribute> mockStatusResults() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("S01");
		attribute1.setDescription("Intra-operative");
		List<CCIGenericAttribute> genericAttributes1 = new ArrayList<CCIGenericAttribute>();
		CCIGenericAttribute geAttribute1 = new CCIGenericAttribute();
		geAttribute1.setCode("I");
		geAttribute1.setDescription("Intra-operative");
		genericAttributes1.add(geAttribute1);
		attribute1.setGenericAttributes(genericAttributes1);
		attributes.add(attribute1);

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("S05");
		attribute2.setDescription("Tx Status 1");
		List<CCIGenericAttribute> genericAttributes2 = new ArrayList<CCIGenericAttribute>();

		CCIGenericAttribute geAttribute21 = new CCIGenericAttribute();
		geAttribute21.setCode("A");
		geAttribute21.setDescription("Abandoned after onset");
		genericAttributes2.add(geAttribute21);

		CCIGenericAttribute geAttribute22 = new CCIGenericAttribute();
		geAttribute22.setCode("R");
		geAttribute22.setDescription("Revision");
		genericAttributes2.add(geAttribute22);

		attribute2.setGenericAttributes(genericAttributes2);
		attributes.add(attribute2);

		return attributes;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		content = new CCIReferenceValueContent();
		content.setViewService(viewService);

		when(viewService.getCCIReferenceAttributesForSupplement(1l, "S", "ENG")).thenReturn(mockStatusResults());
		when(viewService.getCCIReferenceAttributesForSupplement(1l, "L", "ENG")).thenReturn(mockLocationResults());
		when(viewService.getCCIReferenceAttributesForSupplement(1l, "M", "ENG")).thenReturn(mockModeResults());
		when(viewService.getCCIReferenceAttributesForSupplement(1l, "E", "FRA")).thenReturn(mockExtentResults());
	}

	@Test
	public void testGeneratContent() {
		SupplementContentRequest statusRequest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIREFVALUE.getSrc(), Language.ENGLISH.getCode(), 0l, "status", null, 1l,
				0l);
		String statusResult = content.generateSupplementContent(statusRequest);
		String statusResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Reference Number</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Reference Description</th><th style='min-width:81px;width:81px;border: 1px solid black;'>Status Attibute Code</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Status Attribute Description</th></tr></thead></table></div><table style='width:auto;'><tr><td style='min-width:77px;width:77px;border: 1px solid black;' >S01</td><td style='min-width:200px;width:200px;border: 1px solid black;' >Intra-operative</td><td style='min-width:81px;width:81px;border: 1px solid black;'>I</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Intra-operative</td></tr><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='2'>S05</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='2'>Tx Status 1</td><td style='min-width:81px;width:81px;border: 1px solid black;'>A</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Abandoned after onset</td></tr><td style='min-width:81px;width:81px;border: 1px solid black;'>R</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Revision</td></tr></table></tr>";
		assertEquals(statusResultExpected, statusResult);

		SupplementContentRequest locationRequest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIREFVALUE.getSrc(), Language.ENGLISH.getCode(), 0l, "location", null, 1l,
				0l);
		String locationResult = content.generateSupplementContent(locationRequest);
		String locationResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Reference Number</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Reference Description</th><th style='min-width:81px;width:81px;border: 1px solid black;'>Location Attibute Code</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Location Attribute Description</th></tr></thead></table></div><table style='width:auto;'><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='2'>L15</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='2'>Abdominal veins</td><td style='min-width:81px;width:81px;border: 1px solid black;'>1</td><td style='min-width:300px;width:300px;border: 1px solid black;'>One vessel</td></tr><td style='min-width:81px;width:81px;border: 1px solid black;'>2</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Two vessels</td></tr><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='3'>M01</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='3'>Type of delivery 1</td><td style='min-width:81px;width:81px;border: 1px solid black;'>DI</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Direct (service delivered in person by health care provider)</td></tr><td style='min-width:81px;width:81px;border: 1px solid black;'>IN</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Indirect (service delivered at a distance e.g. telephone, telemedicine with health care provider not physically present with client)</td></tr><td style='min-width:81px;width:81px;border: 1px solid black;'>SD</td><td style='min-width:300px;width:300px;border: 1px solid black;'>Self directed (e.g. using tapes, video, books, interactive computer) with or without supervision or coaching from a health care provider</td></tr></table></tr>";
		assertEquals(locationResultExpected, locationResult);

		SupplementContentRequest extentRequest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIREFVALUE.getSrc(), Language.FRENCH.getCode(), 0l, "extent", null, 1l,
				0l);
		String extentResult = content.generateSupplementContent(extentRequest);
		String extentResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Numéro de référence</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Description de référence</th><th style='min-width:81px;width:81px;border: 1px solid black;'>Codes des attributs d' Étendue</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Descriptions des attributs d' Étendue</th></tr></thead></table></div><table style='width:auto;'><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='2'>E01</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='2'>3D reconstruction</td><td style='min-width:81px;width:81px;border: 1px solid black;'>3D</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With 3-dimensional (3D) recons</td></tr><td style='min-width:81px;width:81px;border: 1px solid black;'>PL</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With planar reconstruction</td></tr></table></tr>";
		assertEquals(extentResultExpected, extentResult);

	}
}
