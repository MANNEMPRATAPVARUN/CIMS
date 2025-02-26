package ca.cihi.cims.validator.refset;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.ContextStatus;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

public class RefsetValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return RefsetConfigDetailBean.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object target, Errors result) {
		RefsetConfigDetailBean bean = (RefsetConfigDetailBean) target;
		if (bean.getRefsetCode().isEmpty() || bean.getRefsetNameENG().isEmpty() || (bean.getCategoryId() == null)
				|| (bean.getEffectiveYearFrom() == null) || bean.getICD10CAContextInfo().isEmpty()
				|| bean.getCCIContextInfo().isEmpty() || bean.getSCTVersionCode().isEmpty()
				|| bean.getDefinition().isEmpty()) {

			result.reject("", "Please fill the mandatory fields.");
		}
		if ((bean.getEffectiveYearTo() != null) && (bean.getEffectiveYearTo() < bean.getEffectiveYearFrom())) {
			result.reject("", "Refset Effective Year To should be greater than or equal to Refset Effective Year From");
		}
	}

	public void validateEditPage(Object target, Errors result) {
		RefsetConfigDetailBean bean = (RefsetConfigDetailBean) target;
		if ((bean.getCategoryId() == null) || bean.getDefinition().isEmpty()) {
			result.reject("", "Please fill the mandatory fields.");
		}
	}

	public void validateCreate(Object target, Errors result) {
		RefsetConfigDetailBean bean = (RefsetConfigDetailBean) target;
		if ((bean.getEffectiveYearTo() != null) && (bean.getEffectiveYearTo() < bean.getEffectiveYearFrom())) {
			result.reject("",
					"The Refset Effective Year To value to should be greater than or equal to the Refset Effective Year From value");
		}
		if ("major".equalsIgnoreCase(bean.getVersionType())
				&& (bean.getEffectiveYearFrom() <= bean.getOldEffectiveYearFrom())) {
			result.reject("",
					"For Major Version Change the Refset Effective Year From must be changed and it should be greater than the Base Version's Refset Effective Year From");
		}
		if (bean.getSCTVersionCode().compareTo(bean.getOldSCTVersionCode()) < 0) {
			result.reject("",
					"The selected SNOMED CT Version Year should be greater than or equal to the Base Version's SNOMED CT Version Year");
		}
		String icd10CAYear = bean.getICD10CAContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1];
		if (icd10CAYear.compareTo(bean.getOldICD10CAYear()) < 0) {
			result.reject("",
					"The selected ICD-10-CA Classification Year should be greater than or equal to the Base Version's ICD-10-CA Classification Year");
		}
		String cciYear = bean.getCCIContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1];
		if (cciYear.compareTo(bean.getOldCCIYear()) < 0) {
			result.reject("",
					"The selected CCI Classification Year should be greater than or equal to the Base Version's CCI Classification Year");
		}
	}

	public void validateDisabledStatus(Object target, Status newStatus, Errors result) {
		Refset refset = (Refset) target;
		if ((Status.DISABLED == newStatus)
				&& (ContextStatus.valueOf(refset.getVersionStatus().getStatus()) == ContextStatus.OPEN)) {
			result.reject("", "This refset can not be disabled because its associated version is in open state.");
		}

	}

}
