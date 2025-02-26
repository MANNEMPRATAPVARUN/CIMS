package ca.cihi.cims.service.sgsc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;

@Service
public class SupplementContentGeneratorFactoryImpl implements SupplementContentGeneratorFactory {

	private final Map<String, SupplementContentGenerator> generators = new HashMap<String, SupplementContentGenerator>();

	public SupplementContentGeneratorFactoryImpl() {
		generators.put(SupplementContentRequest.SRC.ICDNEWCODE.getSrc(), new ICDNewCodeContent());
		generators.put(SupplementContentRequest.SRC.ICDDISABLEDCODE.getSrc(), new ICDDisabledCodeContent());
		generators.put(SupplementContentRequest.SRC.CCIGROUP.getSrc(), new CCIGroupContent());
		generators.put(SupplementContentRequest.SRC.CCIINTERVENTION.getSrc(), new CCIInterventionContent());
		generators.put(SupplementContentRequest.SRC.CCIQUALIFIER.getSrc(), new CCIQualifierContent());
		generators.put(SupplementContentRequest.SRC.CCIRUBRICFINDER.getSrc(), new CCIRubricFinderContent());
		generators.put(SupplementContentRequest.SRC.CCIRUBRICFINDER8.getSrc(), new CCIRubricFinder8Content());
		generators.put(SupplementContentRequest.SRC.CCIGENATTRB.getSrc(), new CCIGenericAttributeContent());
		generators.put(SupplementContentRequest.SRC.CCIREFVALUE.getSrc(), new CCIReferenceValueContent());
		generators.put(SupplementContentRequest.SRC.CCIAGENTATC.getSrc(), new AgentATCCodeContent());
		generators.put(SupplementContentRequest.SRC.CCINEWCODE.getSrc(), new CCINewCodeContent());
		generators.put(SupplementContentRequest.SRC.CCIDISABLEDCODE.getSrc(), new CCIDisabledCodeContent());
		generators.put(SupplementContentRequest.SRC.CCINEWREFVALUE.getSrc(),
				new CCINewMandatoryReferenceCodesContent());
		generators.put(SupplementContentRequest.SRC.CCIDISABLEDREFVALUE.getSrc(),
				new CCIDisabledMandatoryReferenceCodesContent());
	}

	@Override
	public <T extends SupplementContentGenerator> T createGenerator(SupplementContentRequest request) {
		@SuppressWarnings("unchecked")
		T generator = (T) generators.get(request.getReportSrc());
		if (generator != null) {
			return generator;
		} else {
			throw new CIMSException("Report is undefined: " + request.getReportSrc());
		}
	}

}
