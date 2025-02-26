package ca.cihi.cims.validator;

import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;

import java.util.regex.Pattern;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.model.tabular.TabularConceptType;

public class CodeValidator {

	public static final Pattern N = newPattern("(\\d{1,1})");
	public static final String N_EXPECTED = "Single digit number expected";

	public static final Pattern NN = newPattern("(\\d{2,2})");
	public static final String NN_EXPECTED = "Two digit number expected";

	public static final Pattern CNN = newPattern("([A-Z]{1,1})(\\d{2,2})");
	public static final String CNN_EXPECTED = "C## format expected";

	// ICD_CATEGORY (2)
	public static final Pattern CNNDN_OR_CNNDNN = newPattern("([A-Z]{1,1})(\\d{2,2})(\\.)(\\d{1,2})");
	public static final String CNNDN_OR_CNNDNN_EXPECTED = "C##.# or C##.## format expected";

	// ICD_CATEGORY (3)
	public static final Pattern CNNDNN_OR_CNNDNNN = newPattern("([A-Z]{1,1})(\\d{2,2})(\\.)(\\d{2,3})");
	public static final String CNNDNN_OR_CNNDNNN_EXPECTED = "C##.## or C##.### format expected";

	// ICD_CATEGORY (4)
	public static final Pattern CNNDNNN = newPattern("([A-Z]{1,1})(\\d{2,2})(\\.)(\\d{3,3})");
	public static final String CNNDNNN_EXPECTED = "C##.### format expected";

	// ICD_CATEGORY
	public static final Pattern NNNN_N = newPattern("(\\d{4,4})(\\/)(\\d{1,1})");
	public static final String NNNN_N_EXPECTED = "####/# format expected";

	// ICD_BLOCK (range)
	public static final Pattern CNN_MINUS_CNN = newPattern(CNN.pattern() + "(\\-)" + CNN.pattern());
	public static final String CNN_MINUS_CNN_EXPECTED = "C##-C## format expected";

	// ICD_BLOCK
	public static final Pattern NNN = newPattern("(\\d{3,3})");
	public static final Pattern NNN_MINUS_NNN = newPattern(NNN.pattern() + "(\\-)" + NNN.pattern());
	public static final String NNN_OR_NNN_MINUS_NNN_EXPECTED = "### or ###-### format expected";

	// CCI_BLOCK (range)
	public static final Pattern NCC = newPattern("(\\d{1,1})([A-Z]){2,2}");
	public static final Pattern NCC_MINUS_NCC = newPattern(NCC.pattern() + "(\\-)" + NCC.pattern());
	public static final String NCC_MINUS_NCC_EXPECTED = "#CC-#CC format expected";
	public static final String CCI_PARENT_CODE_EXPECTED = "# in #CC-#CC format must correspond to parent section code";
	
	// CCI_RUBRIC
	public static final Pattern NCCNN = newPattern("(\\d{1,1})(\\.)([A-Z]){2,2}(\\.)(\\d{2,2})");
	public static final String NCCNN_EXPECTED = "#.CC.## format expected";
	
	// CCI GROUP
	public static final Pattern N_CC = newPattern("(\\d{1,1})(\\.)([A-Z]){2,2}");
	public static final String N_CC_EXPECTED = "#.CC format expected";

	public static final String INVALID_BLOCK_RANGE = "First range value shall be less than second range value in alphanumeric sequence";

	// ---------------------------------------------------------------------------------

	private static Pattern newPattern(String expression) {
		return Pattern.compile(expression, Pattern.DOTALL);
	}

	// ---------------------------------------------------------------------------------

	private boolean isInvalid(Pattern pattern, String value) {
		return !pattern.matcher(value).matches();
	}

	private boolean isInvalidRange(String firstBlockRange, String secondBlockRange) {
		return !(firstBlockRange.compareTo(secondBlockRange) <= 0);
	}

	public String validate(TabularConceptType typeCode, String code, String cciBlockParentSectionCode,
			int icdCategoryNestedLevel, boolean icdBlockOrCategoryMorphology, String icdCategory1OrCategory2Code) {
		switch (typeCode) {
		case CCI_SECTION:
			if (isInvalid(N, code)) {
				return N_EXPECTED;
			}
			break;
		case CCI_BLOCK:
			if (isInvalid(NCC_MINUS_NCC, code)) {
				return NCC_MINUS_NCC_EXPECTED;
			} else {
				if (isInvalid(N, cciBlockParentSectionCode)) {
					return "Invalid parent section code: " + N_EXPECTED;
				} else if (isInvalidRange(code.substring(1, 3), code.substring(5))) {
					return INVALID_BLOCK_RANGE;
				} else {
					if (!code.substring(0, 1).equals(cciBlockParentSectionCode)
							|| !code.substring(4, 5).equals(cciBlockParentSectionCode)) {
						return CCI_PARENT_CODE_EXPECTED;
					}
				}
			}
			break;
		case ICD_CHAPTER:
			if (isInvalid(NN, code)) {
				return NN_EXPECTED;
			}
			break;
		case ICD_BLOCK:
			if (icdBlockOrCategoryMorphology) {
				if (isInvalid(NNN, code) && isInvalid(NNN_MINUS_NNN, code)) {
					return NNN_OR_NNN_MINUS_NNN_EXPECTED;
				}
			} else {
				if (isInvalid(CNN_MINUS_CNN, code)) {
					return CNN_MINUS_CNN_EXPECTED;
				} else if (isInvalidRange(code.substring(1, 3), code.substring(5))) {
					return INVALID_BLOCK_RANGE;
				}
			}
			break;
		case ICD_CATEGORY:
			if (icdBlockOrCategoryMorphology) {
				if (isInvalid(NNNN_N, code)) {
					return NNNN_N_EXPECTED;
				}
			} else {
				switch (icdCategoryNestedLevel) {
				case 1:
					if (isInvalid(CNN, code)) {
						return CNN_EXPECTED;
					}
					break;
				case 2:
					return validateIcdCategory23(code, icdCategory1OrCategory2Code, CNNDN_OR_CNNDNN,
							CNNDN_OR_CNNDNN_EXPECTED);
				case 3:
					return validateIcdCategory23(code, icdCategory1OrCategory2Code, CNNDNN_OR_CNNDNNN,
							CNNDNN_OR_CNNDNNN_EXPECTED);
				case 4:
					return validateIcdCategory23(code, icdCategory1OrCategory2Code, CNNDNNN, CNNDNNN_EXPECTED);
				default:
					throw new CIMSException("Unsupported nested level");
				}
			}
			break;
		case CCI_RUBRIC:
			if(isInvalid(NCCNN, code)){
				return NCCNN_EXPECTED;
			}
			break;
		case CCI_GROUP:
			if(isInvalid(N_CC, code)) {
				return N_CC_EXPECTED;
			}
			break;
		}
			
		return null;
	}

	public String validateCciBlock(String code, String cciBlockParentSectionCode) {
		return validate(CCI_BLOCK, code, cciBlockParentSectionCode, 0, false, null);
	}

	public String validateIcdBlock(String code, boolean icdBlockOrCategoryMorphology) {
		return validate(ICD_BLOCK, code, null, 0, icdBlockOrCategoryMorphology, null);
	}

	public String validateIcdCategory(String code, int icdCategoryNestedLevel, boolean icdBlockOrCategoryMorphology,
			String icdCategory1OrCategory2Code) {
		return validate(ICD_CATEGORY, code, null, icdCategoryNestedLevel, icdBlockOrCategoryMorphology,
				icdCategory1OrCategory2Code);
	}

	private String validateIcdCategory23(String code, String icdCategory1OrCategory2Code, Pattern pattern,
			String patternError) {
		if (isInvalid(pattern, code)) {
			return patternError;
		} else if (!code.startsWith(icdCategory1OrCategory2Code)) {
			return "Must start with: " + icdCategory1OrCategory2Code;
		} else if (code.length() - icdCategory1OrCategory2Code.length() < 1) {
			return "Must be longer then parent: " + icdCategory1OrCategory2Code;
		} else {
			return null;
		}
	}

	public String validateOthers(TabularConceptType typeCode, String code) {
		return validate(typeCode, code, null, 0, false, null);
	}

}
