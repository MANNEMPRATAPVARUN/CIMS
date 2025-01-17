package ca.cihi.cims.refset.util;

import ca.cihi.cims.refset.config.RefsetConstants;

public class RefsetUtils {

	public static String generateVersionCode(String versionCode, String versionType) {
		StringBuilder sb = new StringBuilder();

		if ("major".equals(versionType)) {
			sb.append(RefsetConstants.INCEPTION_VERSION_CODE);
		} else {
			String minorVersion = versionCode.substring(versionCode.indexOf(".") + 1);
			sb.append(versionCode.substring(0, versionCode.indexOf(".") + 1))
					.append(Integer.parseInt(minorVersion) + 1);
		}
		return sb.toString();
	}
}
