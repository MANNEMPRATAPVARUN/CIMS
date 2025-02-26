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

import ca.cihi.cims.Language;
import ca.cihi.cims.data.mapper.SGSCMapper;
import ca.cihi.cims.model.sgsc.AgentGroupDTO;
import ca.cihi.cims.model.sgsc.DeviceAgentATC;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.ViewService;

public class AgentATCContentTest {

	@Mock
	ConceptService conceptService;

	AgentATCCodeContent content = null;

	@Mock
	SGSCMapper sgscMapper;

	@Mock
	ViewService viewService;

	private List<AgentGroupDTO> mockResults() {
		List<AgentGroupDTO> results = new ArrayList<AgentGroupDTO>();
		AgentGroupDTO result1 = new AgentGroupDTO();
		result1.setAgentGroupDescription("ALIMENTARY TRACT AND METABOLISM AGENTS");
		List<DeviceAgentATC> agents1 = new ArrayList<DeviceAgentATC>();
		DeviceAgentATC agent1 = new DeviceAgentATC();
		agent1.setCode("A0");
		agent1.setAgentType("Alimentary tract and metabolism agent NOS");
		agent1.setAgentExample("agent not specified");
		agent1.setAtcCode("--");
		agents1.add(agent1);
		result1.setDeviceAgentATCs(agents1);

		return results;
	}

	private List<AgentGroupDTO> mockResultsFra() {
		List<AgentGroupDTO> results = new ArrayList<AgentGroupDTO>();
		AgentGroupDTO result1 = new AgentGroupDTO();
		result1.setAgentGroupDescription("APPAREIL DIGESTIF ET MÉTABOLISME");
		List<DeviceAgentATC> agents1 = new ArrayList<DeviceAgentATC>();
		DeviceAgentATC agent1 = new DeviceAgentATC();
		agent1.setCode("A0");
		agent1.setAgentType("Agent intervenant sur l' appareil digestif et le métabolisme SAI");
		agent1.setAgentExample("agent non spécifié");
		agent1.setAtcCode("---");
		agents1.add(agent1);
		result1.setDeviceAgentATCs(agents1);

		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		content = new AgentATCCodeContent();
		content.setConceptService(conceptService);
		content.setSgscMapper(sgscMapper);
		content.setViewService(viewService);

		when(conceptService.getCCISectionIdBySectionCode("1", 1l)).thenReturn(1l);
		when(viewService.getCCIClassID("ConceptVersion", "DeviceAgent")).thenReturn(82l);
		when(viewService.getCCIClassID("ConceptPropertyVersion", "DeviceAgentToSectionCPV")).thenReturn(99l);
		when(viewService.getCCIClassID("ConceptVersion", "AgentGroup")).thenReturn(124l);
		when(viewService.getCCIClassID("ConceptPropertyVersion", "AgentGroupIndicator")).thenReturn(125l);
		when(viewService.getCCIClassID("TextPropertyVersion", "DomainValueCode")).thenReturn(118l);
		when(viewService.getCCIClassID("TextPropertyVersion", "DomainValueDescription")).thenReturn(119l);
		when(viewService.getCCIClassID("TextPropertyVersion", "ComponentCode")).thenReturn(89l);
		when(viewService.getCCIClassID("TextPropertyVersion", "AgentTypeDescription")).thenReturn(90l);
		when(viewService.getCCIClassID("TextPropertyVersion", "AgentExample")).thenReturn(91l);
		when(viewService.getCCIClassID("TextPropertyVersion", "AgentATCCode")).thenReturn(92l);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("languageCode", Language.ENGLISH.getCode());
		params.put("sectionId", 1l);
		params.put("deviceAgentClassId", 82l);
		params.put("deviceAgentCpvClassId", 99l);
		params.put("agentGroupClassId", 124l);
		params.put("agentGroupIndicatorClassId", 125l);
		params.put("agentGroupCodeClassId", 118l);
		params.put("agentGroupDescriptionClassId", 119l);
		params.put("componentCodeClassId", 89l);
		params.put("agentTypeDescriptionClassId", 90l);
		params.put("agentExampleClassId", 91l);
		params.put("agentATCCodeClassId", 92l);

		when(sgscMapper.findAgentATCCodes(params)).thenReturn(mockResults());

		Map<String, Object> paramsFra = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("languageCode", Language.FRENCH.getCode());
		params.put("sectionId", 1l);
		params.put("deviceAgentClassId", 82l);
		params.put("deviceAgentCpvClassId", 99l);
		params.put("agentGroupClassId", 124l);
		params.put("agentGroupIndicatorClassId", 125l);
		params.put("agentGroupCodeClassId", 118l);
		params.put("agentGroupDescriptionClassId", 119l);
		params.put("componentCodeClassId", 89l);
		params.put("agentTypeDescriptionClassId", 90l);
		params.put("agentExampleClassId", 91l);
		params.put("agentATCCodeClassId", 92l);

		when(sgscMapper.findAgentATCCodes(paramsFra)).thenReturn(mockResultsFra());
	}

	@Test
	public void testGenerateContent() {
		SupplementContentRequest request = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIAGENTATC.getSrc(), Language.ENGLISH.getCode(), 1l, null, null, 1l, 0l);
		String result = content.generateSupplementContent(request);
		String resultExpected = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 104px;width:104px;text-align:center'>CCI Qualifier Codes</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Agents (Local and Systemic)</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Examples</th><th style='border: 1px solid black;min-width: 79px;width:79px;text-align:center'>ATC Codes</th></tr></thead></table></div><table style='width:auto'></table></td></tr>";
		assertEquals(resultExpected, result);

		SupplementContentRequest requestFra = new SupplementContentRequest(
				SupplementContentRequest.SRC.CCIAGENTATC.getSrc(), Language.FRENCH.getCode(), 1l, null, null, 1l, 0l);
		String resultFra = content.generateSupplementContent(requestFra);
		String resultExpectedFra = "<tr><td colspan='4'><div id='sticker'><table style='width:auto'><thead><tr><th style='border: 1px solid black;min-width: 104px;width:104px;text-align:center'>Codes qualificateurs de la CCI</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Agents (local et systémique)</th><th style='border: 1px solid black;min-width: 300px;width:300px;text-align:center'>Examples</th><th style='border: 1px solid black;min-width: 79px;width:79px;text-align:center'>Codes ATC</th></tr></thead></table></div><table style='width:auto'></table></td></tr>";
		assertEquals(resultExpectedFra, resultFra);
	}
}
