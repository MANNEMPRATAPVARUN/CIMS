package ca.cihi.cims.refset.service;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.refset.service.concept.Refset;

public class ValueRefreshFactory {
	private final Map<String, IValueRefresh> availableRefreshTypes;

	private ValueRefreshFactory() {
		availableRefreshTypes = new HashMap<>();
	}

	public void addRefreshType(String name, IValueRefresh valueRefresh) {
		availableRefreshTypes.put(name, valueRefresh);
	}

	public void doValueRefresh(String name, Refset oldRefset, Refset newRefset) {
		if (availableRefreshTypes.containsKey(name)) {
			availableRefreshTypes.get(name).refreshValues(oldRefset, newRefset);
		}
	}

	public static ValueRefreshFactory init() {
		ValueRefreshFactory vrf = new ValueRefreshFactory();
		vrf.addRefreshType("ICD10CA", new ICD10CAValueRefresh());
		vrf.addRefreshType("CCIRubric", new CCIRubricValueRefresh());
		vrf.addRefreshType("CCICODE", new CCICODEValueRefresh());
		vrf.addRefreshType("SNOMEDExpired", new SNOMEDExpiredConceptValueRefresh());
		vrf.addRefreshType("SNOMEDChagedFSN", new SNOMEDChangedFSNValueRefresh());
		vrf.addRefreshType("SNOMEDChangedPreferred", new SNOMEDChangedPreferredValueRefresh());
		vrf.addRefreshType("SNOMEDChagedSynonym", new SNOMEDChangedSynoymValueRefresh());

		return vrf;
	}

	public void RefreshAll(Refset oldRefset, Refset newRefset) {
		availableRefreshTypes.keySet().forEach(name -> doValueRefresh(name, oldRefset, newRefset));
	}
}
