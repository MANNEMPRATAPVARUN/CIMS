package ca.cihi.cims.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.stereotype.Component;

@Component
public class DisplayTagUtilServiceImpl implements DisplayTagUtilService {

	protected static final String MODEL_KEY_DT_PAGE_KEY = "displayTagPageKey";
	protected static final String MODEL_KEY_DT_SORT_KEY = "displayTagSortKey";
	protected static final String MODEL_KEY_DT_ORDR_KEY = "displayTagOrderKey";

	protected static final String MODEL_KEY_DT_PAGE_NUM = "displayTagPageNum";
	protected static final String MODEL_KEY_DT_SORT_NUM = "displayTagSortNum";
	protected static final String MODEL_KEY_DT_ORDR_NUM = "displayTagOrderNum";

	protected static final String MODEL_KEY_DT_PAGE_JMP = "pageJump";
	public static final int pageJump = 10;

	@Override
	public Map<String, Object> addForPageLinks(HttpServletRequest request, String paramName) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		// Page Jump
		modelMap.put(MODEL_KEY_DT_PAGE_JMP, pageJump);

		// Keys
		modelMap.put(MODEL_KEY_DT_PAGE_KEY, getDisplayTagPageParameterName(paramName));
		modelMap.put(MODEL_KEY_DT_SORT_KEY, getDisplayTagSortParameterName(paramName));
		modelMap.put(MODEL_KEY_DT_ORDR_KEY, getDisplayTagOrderParameterName(paramName));

		// Values
		modelMap.put(MODEL_KEY_DT_PAGE_NUM, getPageValue(request, paramName));
		modelMap.put(MODEL_KEY_DT_SORT_NUM, getSortValue(request, paramName));
		modelMap.put(MODEL_KEY_DT_ORDR_NUM, getOrderValue(request, paramName));

		return modelMap;
	}

	private String getDisplayTagOrderParameterName(String paramName) {
		String pageKey = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_ORDER);
		return pageKey;
	}

	private String getDisplayTagPageParameterName(String paramName) {
		// "componentTable"
		String pageKey = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_PAGE);
		return pageKey;
	}

	private String getDisplayTagSortParameterName(String paramName) {

		String pageKey = new ParamEncoder(paramName).encodeParameterName(TableTagParameters.PARAMETER_SORT);
		return pageKey;
	}

	private String getOrderValue(HttpServletRequest request, String paramName) {

		String value = request.getParameter(new ParamEncoder(paramName)
				.encodeParameterName(TableTagParameters.PARAMETER_ORDER));
		if (value == null) {
			value = "2";
		}

		return value;
	}

	private String getPageValue(HttpServletRequest request, String paramName) {

		String value = request.getParameter(new ParamEncoder(paramName)
				.encodeParameterName(TableTagParameters.PARAMETER_PAGE));
		if (value == null) {
			value = "1";
		}

		return value;
	}

	private String getSortValue(HttpServletRequest request, String paramName) {

		String value = request.getParameter(new ParamEncoder(paramName)
				.encodeParameterName(TableTagParameters.PARAMETER_SORT));
		if (value == null) {
			value = "1";
		}

		return value;
	}

}
