package ca.cihi.cims.sct.web.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.sct.web.domain.Term;
import ca.cihi.cims.sct.web.service.DescriptionService;

@RestController
public class SnomedSearchController {
	private final Logger logger = LoggerFactory.getLogger(SnomedSearchController.class);
	private static final String SNOMED_VIEW = "snomed";
	private static final String STATUS_CODE = "ACTIVE";

	@Autowired
	private DescriptionService descriptionService;

	public DescriptionService getDescriptionService() {
		return descriptionService;
	}

	public void setDescriptionService(DescriptionService descriptionService) {
		this.descriptionService = descriptionService;
	}

	@RequestMapping(value = "/snomed.htm", method = RequestMethod.GET)
	public ModelAndView getPage(@RequestParam("version") String version) {
		logger.debug("############## request for /snomed.htm");
		ModelAndView mav = new ModelAndView();
		mav.addObject("sctVersion", version);
		mav.addObject("conceptTypeList", descriptionService.getConceptTypes());
		mav.setViewName(SNOMED_VIEW);
		return mav;
	}

	@RequestMapping(value = "/getTerms.htm", method = RequestMethod.GET)
	public List<Term> getTermDescriptions(@RequestParam("term") String term, @RequestParam("version") String version,  @RequestParam("conceptType") String conceptType) {
		logger.debug("search term pased: " + term);
		List<Term> searchResult = null;
		boolean isNum = StringUtils.isNumeric(term);

		if (isNum) {
			long id = Long.parseLong(term);
			searchResult = descriptionService.getDesciptionByConceptIdOrDespId(id, version,conceptType);
		} else {
			List<String> words = Arrays.asList(term.trim().split("\\s+"));
			searchResult = descriptionService.getDesciptionByTerm(words, version, conceptType);
		}
		return searchResult;
	}

	@RequestMapping(value = "/getConceptDesps.htm", method = RequestMethod.GET)
	public List<Term> getConceptDescriptions(@RequestParam("conceptId") long conceptId,
			@RequestParam("version") String version,  @RequestParam("conceptType") String conceptType) {
		logger.debug("search conceptId pased: " + conceptId);
		List<Term> searchResult = null;
		searchResult = descriptionService.getDesciptionByConceptIdOrDespId(conceptId, version, conceptType);
		return searchResult;
	}

	@RequestMapping(value = "/test.htm", method = RequestMethod.GET)
	public ModelAndView getData() throws Exception {

		ModelAndView modelView = new ModelAndView("test");
		// modelView.addObject("versions", descriptionService.getAllVersons());
		modelView.addObject("SCTVersionList", descriptionService.getSCTVersionList(STATUS_CODE));
		return modelView;
	}

}