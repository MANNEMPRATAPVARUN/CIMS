package ca.cihi.cims.validator;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.prodpub.GenerateFileStatus;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.PublicationService;

public class PublicationValidator implements Validator {

	private ChangeRequestService changeRequestService;

	private PublicationService publicationService;

	private LookupService lookupService;

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public PublicationService getPublicationService() {
		return publicationService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setPublicationService(PublicationService publicationService) {
		this.publicationService = publicationService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return GenerateReleaseTablesCriteria.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object arg0, Errors errors) {
		// TODO Auto-generated method stub

	}

	public void validateCloseYearBtn(Object target, Errors errors) {
		GenerateReleaseTablesCriteria generateTablesModel = (GenerateReleaseTablesCriteria) target;
		Long currentOpenYear = generateTablesModel.getCurrentOpenYear();
		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				String.valueOf(currentOpenYear));
		FreezingStatus icdFreezingStatus = icdBaseContext.getFreezingStatus();
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(currentOpenYear));
		FreezingStatus cciFreezingStatus = cciBaseContext.getFreezingStatus();

		List<ChangeRequest> cciOpenChanges = changeRequestService.findOpenChangeRequestsByClassificationAndVersionYear(
				"CCI", currentOpenYear);
		List<ChangeRequest> icdOpenChanges = changeRequestService.findOpenChangeRequestsByClassificationAndVersionYear(
				"ICD-10-CA", currentOpenYear);

		if ((cciOpenChanges != null && cciOpenChanges.size() > 0)
				|| (icdOpenChanges != null && icdOpenChanges.size() > 0)) {
			errors.rejectValue(
					"note",
					null,
					"Year "
							+ currentOpenYear
							+ " cannot be closed as All change requests, including supplements and indexes, must be in \"Rejected\", \"Closed-Deferred\" or \"Closed-Approved\" status");
		}

		// boolean bothClassificationFrozen = false;

		if (icdFreezingStatus == null || cciFreezingStatus == null) {
			errors.rejectValue("note", null, "Year " + currentOpenYear
					+ " cannot be closed when at least one classification is not frozen.");
		}

		if (FreezingStatus.BLK == cciBaseContext.getFreezingStatus()) { // close year in progress or year closed
			errors.rejectValue("note", null, "Year closure in progress or year already closed");
		}

	}

	public void validateGenerateTablesBtn(Object target, Errors errors) {
		GenerateReleaseTablesCriteria generateTablesModel = (GenerateReleaseTablesCriteria) target;
		Long versionYear = generateTablesModel.getCurrentOpenYear();
		String classifcation = generateTablesModel.getClassification();

		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(versionYear));

		if (FreezingStatus.BLK == cciBaseContext.getFreezingStatus()) { // close year in progress or year closed
			errors.rejectValue("note", null,
					"Year closure in progress or year closed, classification tables cannot be generated");
		}

		List<ChangeRequest> openChanges = null;
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equalsIgnoreCase(classifcation)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equalsIgnoreCase(classifcation)) {
			openChanges = changeRequestService.findOpenTabularChangeRequestsByClassificationAndVersionYear("CCI",
					versionYear);

		}

		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equalsIgnoreCase(classifcation)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equalsIgnoreCase(classifcation)) {
			openChanges = changeRequestService.findOpenTabularChangeRequestsByClassificationAndVersionYear("ICD-10-CA",
					versionYear);
		}
		if (openChanges != null && openChanges.size() > 0) {
			errors.rejectValue(
					"note",
					null,
					"All tabular list change requests have to be Closed-Approved, Rejected or Closed-Deferred before classification tables can be generated.");
		}

		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equalsIgnoreCase(classifcation)) {
			Long cciCurrentOpenYear = lookupService.findCCICurrentOpenYear();
			Long cciLastClosedYear = cciCurrentOpenYear - 1;
			ContextIdentifier cciCurrentOpenBaseContext = lookupService
					.findBaseContextIdentifierByClassificationAndYear("CCI", String.valueOf(cciCurrentOpenYear));
			ContextIdentifier cciLastClosedBaseContext = lookupService
					.findBaseContextIdentifierByClassificationAndYear("CCI", String.valueOf(cciLastClosedYear));

			List<ComponentAndAttributeElementModel> unusedComponents = publicationService.findUnusedComponentElements(
					cciCurrentOpenBaseContext.getContextId(), cciLastClosedBaseContext.getContextId());
			List<ComponentAndAttributeElementModel> unusedGenericAttributes = publicationService
					.findUnusedGenericAttributes(cciCurrentOpenBaseContext.getContextId(),
							cciLastClosedBaseContext.getContextId());
			List<ComponentAndAttributeElementModel> unusedReferenceValues = publicationService
					.findUnusedReferenceValues(cciCurrentOpenBaseContext.getContextId(),
							cciLastClosedBaseContext.getContextId());
			if ((unusedComponents != null && unusedComponents.size() > 0)
					|| (unusedGenericAttributes != null && unusedGenericAttributes.size() > 0)
					|| (unusedReferenceValues != null && unusedReferenceValues.size() > 0)) {
				errors.rejectValue(
						"note",
						null,
						"The "
								+ cciCurrentOpenYear
								+ " CCI classification tables cannot be generated when there are unpublished code components, reference values or generic attributes codes that have "
								+ " reference links/dependents. Please see the Unused Components and Attributes Report for details.");
			}

		}

	}

	public void validateReleaseBtn(Object target, Errors errors) {
		GenerateReleaseTablesCriteria generateTablesModel = (GenerateReleaseTablesCriteria) target;
		Long versionYear = generateTablesModel.getCurrentOpenYear();
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(versionYear));
		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				String.valueOf(versionYear));
		PublicationSnapShot latestCCISnapShot = publicationService.findLatestSnapShotByContextId(cciBaseContext
				.getContextId());

		if (FreezingStatus.BLK == cciBaseContext.getFreezingStatus()) { // close year in progress or year closed
			errors.rejectValue("note", null,
					"Year closure in progress or year closed, tables package cannot be released");
		}

		if (latestCCISnapShot != null && GenerateFileStatus.I == latestCCISnapShot.getStatus()) {
			errors.rejectValue("note", null, " Release of " + versionYear
					+ " ICD-10-CA and CCI classification tables package cannot be released when the " + versionYear
					+ " CCI tables package generation process in progress.");

		}
		PublicationSnapShot latestICDSnapShot = publicationService.findLatestSnapShotByContextId(icdBaseContext
				.getContextId());
		if (latestICDSnapShot != null && GenerateFileStatus.I == latestICDSnapShot.getStatus()) {
			errors.rejectValue("note", null, " Release of " + versionYear
					+ " ICD-10-CA and CCI classification tables package cannot be released when the " + versionYear
					+ " ICD-10-CA tables package generation process in progress.");

		}
		PublicationRelease latestHighestSuccessPublicationRelease = publicationService
				.findLatestHighestSuccessPublicationReleaseByFiscalYear(String.valueOf(generateTablesModel
						.getCurrentOpenYear()));
		if (generateTablesModel.getReleaseType() == null) {
			errors.rejectValue("releaseType", null, "Please specify release type.");
		} else {
			ReleaseType currentReleaseType = ReleaseType.fromString(generateTablesModel.getReleaseType());
			if (currentReleaseType == null) {
				errors.rejectValue("releaseType", null, "Please specify release type.");
			} else {

				if (latestHighestSuccessPublicationRelease != null) {
					if (latestHighestSuccessPublicationRelease.getReleaseType().getReleaseTypeId() > currentReleaseType
							.getReleaseTypeId()) {
						errors.rejectValue(
								"note",
								null,
								" Release of "
										+ versionYear
										+ " "
										+ generateTablesModel.getReleaseType()
										+ " ICD-10-CA and CCI classification tables package cannot be released while there is "
										+ latestHighestSuccessPublicationRelease.getReleaseType().getReleaseTypeCode()
										+ " release in the system");
					}
				}

				PublicationRelease latestPublicationRelease = publicationService
						.findLatestPublicationReleaseByFiscalYear(String.valueOf(versionYear));
				if (latestPublicationRelease != null && GenerateFileStatus.I == latestPublicationRelease.getStatus()) {
					errors.rejectValue("note", null, " Release of " + versionYear
							+ " ICD-10-CA and CCI classification tables package cannot be released when the "
							+ versionYear + " other release process in progress.");

				}
			}
		}

	}

	public void validateUnfreezeCCIBtn(Object target, Errors errors) {
		GenerateReleaseTablesCriteria generateTablesModel = (GenerateReleaseTablesCriteria) target;
		Long versionYear = generateTablesModel.getCurrentOpenYear();
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(versionYear));
		PublicationSnapShot latestCCISnapShot = publicationService.findLatestSnapShotByContextId(cciBaseContext
				.getContextId());

		if (FreezingStatus.BLK == cciBaseContext.getFreezingStatus()) { // close year in progress or year closed
			errors.rejectValue("note", null, "Year closure in progress or year closed, can not unfreeze");
		}

		if (latestCCISnapShot != null && GenerateFileStatus.I == latestCCISnapShot.getStatus()) {
			errors.rejectValue("note", null, "CCI generating file in progress, can not unfreeze");
		}

		PublicationRelease latestPublicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(String.valueOf(versionYear));
		if (latestPublicationRelease != null && GenerateFileStatus.I == latestPublicationRelease.getStatus()) {
			errors.rejectValue("note", null, "releasing in progress, can not unfreeze");

		}

	}

	public void validateUnfreezeICDBtn(Object target, Errors errors) {
		GenerateReleaseTablesCriteria generateTablesModel = (GenerateReleaseTablesCriteria) target;
		Long versionYear = generateTablesModel.getCurrentOpenYear();

		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				String.valueOf(versionYear));
		// FreezingStatus icdFreezingStatus = icdBaseContext.getFreezingStatus();

		// FreezingStatus cciFreezingStatus = cciBaseContext.getFreezingStatus();

		PublicationSnapShot latestICDSnapShot = publicationService.findLatestSnapShotByContextId(icdBaseContext
				.getContextId());

		if (FreezingStatus.BLK == icdBaseContext.getFreezingStatus()) { // close year in progress or year closed
			errors.rejectValue("note", null, "Year closure in progress or year closed, can not unfreeze");
		}

		if (latestICDSnapShot != null && GenerateFileStatus.I == latestICDSnapShot.getStatus()) {
			errors.rejectValue("note", null, "ICD generating file in progress, can not unfreeze");
		}

		PublicationRelease latestPublicationRelease = publicationService
				.findLatestPublicationReleaseByFiscalYear(String.valueOf(versionYear));
		if (latestPublicationRelease != null && GenerateFileStatus.I == latestPublicationRelease.getStatus()) {
			errors.rejectValue("note", null, "releasing in progress, can not unfreeze");

		}

	}
}
