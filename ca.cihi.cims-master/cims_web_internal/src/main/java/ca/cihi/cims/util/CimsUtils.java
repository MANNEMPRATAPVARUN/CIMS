package ca.cihi.cims.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ca.cihi.cims.model.changerequest.DocumentReference;

public class CimsUtils {

	public static boolean areTwoDocumentReferencesListSame(List<DocumentReference> newDocumentReferences,
			List<DocumentReference> oldDocumentReferences) {
		boolean same = true;
		if ((newDocumentReferences == null || newDocumentReferences.size() == 0)
				&& (oldDocumentReferences == null || oldDocumentReferences.size() == 0)) {
			same = true;
		} else {
			if (newDocumentReferences != null && oldDocumentReferences != null
					&& newDocumentReferences.size() == oldDocumentReferences.size()) {
				Collections.sort(newDocumentReferences);
				Collections.sort(oldDocumentReferences);
				for (int i = 0; i < newDocumentReferences.size(); i++) {
					if (newDocumentReferences.get(i).compareTo(oldDocumentReferences.get(i)) != 0) {
						same = false;
						break;
					}
				}
			} else {
				same = false;
			}
		}
		return same;
	}

	public static boolean getBooleanValue(String svalue) {
		return "Y".equalsIgnoreCase(svalue) ? true : false;
	}

	public static String getStringValue(boolean bvalue) {
		return bvalue ? "Yes" : "No";
	}

	public static String getDateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}
	
	/**
	 * 
	 * @param dateFormat
	 * @return
	 */
	public static String getDate(String dateFormat){
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    Calendar c1 = Calendar.getInstance(); 
	    return sdf.format(c1.getTime());
	}

}
