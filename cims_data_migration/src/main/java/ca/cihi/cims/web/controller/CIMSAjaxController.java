package ca.cihi.cims.web.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.bll.ContextProvider;

@Controller
public class CIMSAjaxController {

	private static final Log LOGGER = LogFactory.getLog(CIMSAjaxController.class);

	public final static String AJAX_VERSION = "/getAjaxVersions.htm";

	private ContextProvider contextProvider;

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	/**
	 * This method will return a list of versions for the given classification
	 * 
	 * @return Collection of versions.
	 */
	@RequestMapping(value = AJAX_VERSION, method = RequestMethod.GET)
	public @ResponseBody
	Map<String, String> getVersionForClassification(@RequestParam("classification") final String classification) {

		LOGGER.info("Enter getVersionForClassification()");

		Map<String, String> resultMap = new HashMap<String, String>();

		final Collection<String> versions = contextProvider.findVersionCodes(classification);

		for (String versionCode : versions) {

			if (!StringUtils.isEmpty(versionCode) && versionCode.length() == 4) {
				resultMap.put(versionCode, versionCode);
			}
		}

		LOGGER.info("Exit getVersionForClassification()");

		return resultMap;
	}

	@Autowired
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}
}
