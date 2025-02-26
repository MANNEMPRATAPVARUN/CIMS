package ca.cihi.cims.service.sgsc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ViewService;

public class CCIGenericAttributeContentTest {

	CCIGenericAttributeContent content = null;

	@Mock
	ViewService viewService;

	private List<IdCodeDescription> mockExtentResults() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription extent1 = new IdCodeDescription();
		extent1.setCode("14");
		extent1.setDescription("Quatorze");
		results.add(extent1);

		return results;
	}

	private List<IdCodeDescription> mockLocationResults() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription location1 = new IdCodeDescription();
		location1.setCode("0");
		location1.setDescription("null");
		results.add(location1);

		return results;
	}

	private List<IdCodeDescription> mockModeResults() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription mode1 = new IdCodeDescription();
		mode1.setCode("DI");
		mode1.setDescription("Direct");
		results.add(mode1);

		return results;
	}

	private List<IdCodeDescription> mockStatusResults() {
		List<IdCodeDescription> results = new ArrayList<IdCodeDescription>();
		IdCodeDescription status1 = new IdCodeDescription();
		status1.setCode("0");
		status1.setDescription("Not applicable");
		results.add(status1);

		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new CCIGenericAttributeContent();
		content.setViewService(viewService);

		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "S", "ENG"))
				.thenReturn(mockStatusResults());
		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "L", "ENG"))
				.thenReturn(mockLocationResults());
		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "M", "ENG"))
				.thenReturn(mockModeResults());
		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "L", "FRA"))
				.thenReturn(mockLocationResults());
		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "M", "FRA"))
				.thenReturn(mockModeResults());
		when(viewService.getGenericAttributesForSupplement(SupplementContentGenerator.CCI, 1l, "E", "FRA"))
				.thenReturn(mockExtentResults());
	}

	@Test
	public void testGenerateContent() {
		SupplementContentRequest statusResuest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), "ENG", null, "status", null, 1l, 0l);
		String statusResult = content.generateSupplementContent(statusResuest);
		String statusResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 336px;width:336px;text-align:left' colspan='2'>Status (Generic Descriptions)</th></tr></thead></table></div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>0</td><td style='border: 1px solid black;min-width: 270px;width:270px'>Not applicable</td></tr></table></td></tr>";
		assertEquals(statusResultExpected, statusResult);

		SupplementContentRequest locationResuest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), "ENG", null, "location", null, 1l, 0l);
		String locationResult = content.generateSupplementContent(locationResuest);
		String locationResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 336px;width:336px;text-align:left' colspan='2'>Location (Generic Descriptions)</th></tr></thead></table></div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>0</td><td style='border: 1px solid black;min-width: 270px;width:270px'>null</td></tr><tr><td colspan='2' style='border: 1px solid black;font-weight:bold;'>Mode of Delivery</td></tr><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>DI</td><td style='border: 1px solid black;min-width: 270px;width:270px'>Direct</td></tr></table></td></tr>";
		assertEquals(locationResultExpected, locationResult);

		SupplementContentRequest locationResuestFRA = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), "FRA", null, "location", null, 1l, 0l);
		String locationResultFRA = content.generateSupplementContent(locationResuestFRA);
		String locationResultExpectedFRA = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 336px;width:336px;text-align:left' colspan='2'>Lieu</th></tr></thead></table></div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>0</td><td style='border: 1px solid black;min-width: 270px;width:270px'>null</td></tr><tr><td colspan='2' style='border: 1px solid black;font-weight:bold;'>Méthode utilisée</td></tr><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>DI</td><td style='border: 1px solid black;min-width: 270px;width:270px'>Direct</td></tr></table></td></tr>";
		assertEquals(locationResultExpectedFRA, locationResultFRA);

		SupplementContentRequest extentResuest = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), "FRA", null, "extent", null, 1l, 0l);
		String extentResult = content.generateSupplementContent(extentResuest);
		String extentResultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 336px;width:336px;text-align:left' colspan='2'>Étendue</th></tr></thead></table></div><table style='width:auto'><tr><td style='border: 1px solid black;min-width: 50px;width:50px;text-align:center'>14</td><td style='border: 1px solid black;min-width: 270px;width:270px'>Quatorze</td></tr></table></td></tr>";
		assertEquals(extentResultExpected, extentResult);
	}
}
