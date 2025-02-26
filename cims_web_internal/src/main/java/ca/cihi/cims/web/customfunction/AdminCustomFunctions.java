package ca.cihi.cims.web.customfunction;

import org.apache.commons.lang.WordUtils;

import ca.cihi.cims.ConceptStatus;

public class AdminCustomFunctions {
	public static String statusConvertFromLetter(String statusLetter) {
		if (statusLetter.equalsIgnoreCase("A")) {
			return WordUtils.capitalizeFully(ConceptStatus.ACTIVE.name());
		} else if (statusLetter.equalsIgnoreCase("D")) {
			return WordUtils.capitalizeFully(ConceptStatus.DISABLED.name());
		} else {
			return "UNK";
		}
	}

	public static String yesNoConvertFromLetter(String yesNoLetter) {
		if (yesNoLetter.equalsIgnoreCase("Y")) {
			return "Yes";
		} else if (yesNoLetter.equalsIgnoreCase("N")) {
			return "No";
		} else {
			return "UNK";
		}
	}

	private AdminCustomFunctions() {
	}
}
