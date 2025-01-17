package ca.cihi.cims.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateFileStatus;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;
import ca.cihi.cims.validator.PublicationValidator;

/*
 * this class is in the admin package, actually, it is part of publication and release 
 */

@Controller
public class CloseYearController {

	public static final String CLOSE_YEAR = "/admin/closeYear";

	@Autowired
	private LookupService lookupService;
	@Autowired
	private PublicationService publicationService;

	@Autowired
	private PublicationValidator publicationValidator;

	@RequestMapping(value = "/admin/closeYear", method = RequestMethod.POST)
	public String closeYear(final Model model, GenerateReleaseTablesCriteria generateReleaseTablesCriteria,
			final BindingResult result, HttpSession session) {
		publicationValidator.validateCloseYearBtn(generateReleaseTablesCriteria, result);
		if (result.hasErrors()) {
			Long nextOpenYear = lookupService.findCCICurrentOpenYear();
			generateReleaseTablesCriteria.setCurrentOpenYear(nextOpenYear);
			prepareModel(model, generateReleaseTablesCriteria);
			return CLOSE_YEAR;
		}
		// do close year
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		List<Long> newOpenedYears = publicationService.closeYear(generateReleaseTablesCriteria.getCurrentOpenYear(),
				currentUser);
		Long closedYear = generateReleaseTablesCriteria.getCurrentOpenYear();
		Long nextOpenYear = lookupService.findCCICurrentOpenYear();
		generateReleaseTablesCriteria.setCurrentOpenYear(nextOpenYear);
		prepareModel(model, generateReleaseTablesCriteria);
		if (newOpenedYears != null) {
			model.addAttribute("newOpenedYears", newOpenedYears);
		}
		model.addAttribute("closedYear", closedYear);
		model.addAttribute("closeYearSuccess", Boolean.TRUE);

		return CLOSE_YEAR;
	}
	
	@RequestMapping(value = "/admin/covidException/createSingleContextYear", method = RequestMethod.GET)
	public String createSingleContextYear(HttpSession session, @RequestParam(value = "code") String code, @RequestParam(value = "icdFlag") String icd, @RequestParam(value = "cciFlag") String cci) {
		if(code.equalsIgnoreCase("pass123abc")){
		    publicationService.addSingleYearContext(icd, cci);
		}
		else{
			return "Invalid code provided...";
		}

		return "Context created!";
	}

	private void prepareModel(final Model model, GenerateReleaseTablesCriteria generateReleaseTablesCriteria) {
		Long currentOpenYear = generateReleaseTablesCriteria.getCurrentOpenYear();
		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				String.valueOf(currentOpenYear));
		FreezingStatus icdFreezingStatus = icdBaseContext.getFreezingStatus();
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(currentOpenYear));
		FreezingStatus cciFreezingStatus = cciBaseContext.getFreezingStatus();
		boolean bothClassificationFrozen = false;
		if (icdFreezingStatus != null && cciFreezingStatus != null) {
			bothClassificationFrozen = true;
		}

		boolean officialReleased = false;
		PublicationRelease latestPublicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(String.valueOf(currentOpenYear));
		if (latestPublicationRelease != null && ReleaseType.OFFICIAL == latestPublicationRelease.getReleaseType()
				&& GenerateFileStatus.E == latestPublicationRelease.getStatus()) {
			officialReleased = true;
		}
		model.addAttribute("bothClassificationFrozen", bothClassificationFrozen);
		model.addAttribute("officialReleased", officialReleased);

		model.addAttribute("generateReleaseTablesCriteria", generateReleaseTablesCriteria);
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setPublicationService(PublicationService publicationService) {
		this.publicationService = publicationService;
	}

	public void setPublicationValidator(PublicationValidator publicationValidator) {
		this.publicationValidator = publicationValidator;
	}

	@RequestMapping(value = "/admin/initCloseYear", method = RequestMethod.GET)
	public String showCloseYearPage(final Model model, HttpSession session) {
		Long currentOpenYear = lookupService.findCCICurrentOpenYear();
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setCurrentOpenYear(currentOpenYear);
		prepareModel(model, generateReleaseTablesCriteria);
		return CLOSE_YEAR;
	}

}
