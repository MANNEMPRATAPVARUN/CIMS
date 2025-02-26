package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.SGSCMapper;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ViewService;

public class CCIDisabledMandatoryRefValueContentTest {

	CCIDisabledMandatoryReferenceCodesContent content = null;

	@Mock
	SGSCMapper sgscMapper;

	@Mock
	ViewService viewService;

	private List<CCIReferenceAttribute> mockResults() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("L15");
		attribute1.setDescription("Abdominal veins");
		attributes.add(attribute1);

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

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new CCIDisabledMandatoryReferenceCodesContent();
		content.setSgscMapper(sgscMapper);
		content.setViewService(viewService);

		when(viewService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"))
				.thenReturn(112l);
		when(viewService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV")).thenReturn(111l);
		when(viewService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode")).thenReturn(107l);
		when(viewService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription")).thenReturn(106l);
		when(viewService.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"))
				.thenReturn(108l);
		when(viewService.getCCIClassID(WebConstants.CONCEPT_VERSION, "ReferenceAttribute")).thenReturn(104l);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId", 112l);
		params.put("genericAttributeCPVClassId", 111l);
		params.put("attributeCodeClassId", 107l);
		params.put("attributeDescriptionClassId", 106l);
		params.put("attributeMandatoryIndicatorClassId", 108l);
		params.put("referenceAttributeClassId", 104l);
		params.put("currentContextId", 2l);
		params.put("priorContextId", 1l);
		params.put("languageCode", "ENG");

		when(sgscMapper.findCCIDisabledMandatoryReferenceCodes(params)).thenReturn(mockResults());

		Map<String, Object> paramsFRA = new HashMap<String, Object>();
		paramsFRA.put("referenceAttributeCPVClassId", 112l);
		paramsFRA.put("genericAttributeCPVClassId", 111l);
		paramsFRA.put("attributeCodeClassId", 107l);
		paramsFRA.put("attributeDescriptionClassId", 106l);
		paramsFRA.put("attributeMandatoryIndicatorClassId", 108l);
		paramsFRA.put("referenceAttributeClassId", 104l);
		paramsFRA.put("currentContextId", 2l);
		paramsFRA.put("priorContextId", 1l);
		paramsFRA.put("languageCode", "FRA");

		when(sgscMapper.findCCIDisabledMandatoryReferenceCodes(paramsFRA)).thenReturn(mockResults());
	}

	@Test
	public void testGenerateContent() {
		SupplementContentRequest request = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIDISABLEDREFVALUE.getSrc(), "ENG", null, null, null, 2l, 1l);
		String result = content.generateSupplementContent(request);
		String resultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Reference Number</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Reference Description</th><th style='min-width:67px;width:67px;border: 1px solid black;'>Attribute Code</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Attribute Description</th></tr></thead></table></div><table style='width:auto;'><tr><td style='min-width:77px;width:77px;border: 1px solid black;' >L15</td><td style='min-width:200px;width:200px;border: 1px solid black;' >Abdominal veins</td><td style='min-width:67px;width:67px;border: 1px solid black;'>&nbsp;</td><td style='min-width:300px;width:300px;border: 1px solid black;'>&nbsp;</td></tr><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='2'>E01</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='2'>3D reconstruction</td><td style='min-width:67px;width:67px;border: 1px solid black;'>3D</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With 3-dimensional (3D) recons</td></tr><td style='min-width:67px;width:67px;border: 1px solid black;'>PL</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With planar reconstruction</td></tr></table></tr>";
		assertEquals(resultExpected, result);

		SupplementContentRequest requestFRA = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIDISABLEDREFVALUE.getSrc(), "FRA", null, null, null, 2l, 1l);
		String resultFRA = content.generateSupplementContent(requestFRA);
		String resultExpectedFRA = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Numéro de référence</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Description de référence</th><th style='min-width:67px;width:67px;border: 1px solid black;'>Codes des attributs</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Descriptions des attributs</th></tr></thead></table></div><table style='width:auto;'><tr><td style='min-width:77px;width:77px;border: 1px solid black;' >L15</td><td style='min-width:200px;width:200px;border: 1px solid black;' >Abdominal veins</td><td style='min-width:67px;width:67px;border: 1px solid black;'>&nbsp;</td><td style='min-width:300px;width:300px;border: 1px solid black;'>&nbsp;</td></tr><tr><td style='min-width:77px;width:77px;border: 1px solid black;' rowspan='2'>E01</td><td style='min-width:200px;width:200px;border: 1px solid black;' rowspan='2'>3D reconstruction</td><td style='min-width:67px;width:67px;border: 1px solid black;'>3D</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With 3-dimensional (3D) recons</td></tr><td style='min-width:67px;width:67px;border: 1px solid black;'>PL</td><td style='min-width:300px;width:300px;border: 1px solid black;'>With planar reconstruction</td></tr></table></tr>";
		assertEquals(resultExpectedFRA, resultFRA);
	}
}
