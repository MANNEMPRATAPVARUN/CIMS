package ca.cihi.cims.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.model.refset.RefsetResponse;

/**
 *
 * @author lzhu
 *
 */

public class RefsetUtils {

	public static final String STATUS_FAILED = "FAILED";

	public static final String ASSIGNEE_REVOKED_MESSAGE = "This refset has been assigned to other developer. Please contact System administrator.";
	public static final String REFSET_NO_PICKLIST_MESSAGE = "The Refset Version cannot be closed as there are no Picklist associated with the Refset version";

	public static final String ERROR_TYPE_ASSIGNEE_REVOKED = "REVOKED";
	public static final String ERROR_TYPE_NO_PICKLIST = "NO_PICKLIST";
	public static final String REFSET_VERSION_STATUS_CLOSED = "CLOSED";
	public static final String VERSION_NAME_SPACE = " ";
	public static final String VERSION_NAME_SEPERATOR = "-";
	public static final String CONTEXT_INFO_SEPERATOR = "_";

	public static void retrieveErrorMsg(BindingResult result, RefsetResponse response) {

		List<ObjectError> allErrors = result.getAllErrors();
		List<String> errorMesages = new ArrayList<String>();
		for (ObjectError objectError : allErrors) {
			String errorMessage = objectError.getDefaultMessage();
			errorMesages.add(errorMessage);
		}
		response.setErrors(errorMesages);
		response.setStatus(STATUS_FAILED);
	}

	public static void retrieveErrorMsg(String errorMessage, RefsetResponse response) {
		List<String> errorMesages = new ArrayList<String>();
		errorMesages.add(errorMessage);
		response.setErrors(errorMesages);
		response.setStatus(STATUS_FAILED);
	}

	public static List<String> dedupeList(List<String> oldList) {
		return new ArrayList<>(new LinkedHashSet<>(oldList));
	}

	public static String getRefsetVersionName(String refsetCode, Integer effectiveYearFrom, Integer effectiveYearTo,
			String versionCode) {
		StringBuffer refsetVersionName = new StringBuffer();
		if (effectiveYearTo != null) {
			refsetVersionName.append(refsetCode).append(VERSION_NAME_SPACE).append(effectiveYearFrom)
					.append(VERSION_NAME_SEPERATOR).append(effectiveYearTo).append(VERSION_NAME_SPACE)
					.append(versionCode);
		} else {
			refsetVersionName.append(refsetCode).append(VERSION_NAME_SPACE).append(effectiveYearFrom)
					.append(VERSION_NAME_SPACE).append(versionCode);
		}
		return refsetVersionName.toString();
	}
}
