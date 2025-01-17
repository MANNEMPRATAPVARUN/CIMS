package ca.cihi.cims.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestNature;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;

public class ChangeRequestValidator implements Validator {

	private ChangeRequestService changeRequestService;

	private LookupService lookupService;

	private AdminService adminService;

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return ChangeRequestDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (!errors.hasErrors()) {
			ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
			if (StringUtils.isBlank(changeRequestDTO.getName())) {
				errors.rejectValue("name", null,
						"Change Request Name must not be empty and cannot contain only spaces");
			} else if (changeRequestDTO.getName().length() > 250) {
				errors.rejectValue("name", "Change Request Name cannot be over 250 characters");
			}
			boolean isNameExist = false;
			if (changeRequestDTO.getChangeRequestId() == null) { // for create a new one
				isNameExist = changeRequestService.isChangeRequestNameExistInContext(changeRequestDTO.getName().trim(), changeRequestDTO.getBaseContextId());
			} else if (!changeRequestDTO.getStatus().equals(ChangeRequestStatus.DEFERRED)
					&& !changeRequestDTO.getStatus().equals(ChangeRequestStatus.REJECTED)) {
				isNameExist = changeRequestService.isSameChangeRequestNameExist(changeRequestDTO.getName(),
						changeRequestDTO.getChangeRequestId());
			}
			if (isNameExist) {
				errors.rejectValue("name", null,
						"Sorry, there is already an existing change request with the same request name ");
			}

			ContextIdentifier baseContextIdentifier = lookupService
					.findContextIdentificationById(changeRequestDTO.getBaseContextId());
			AuxTableValue auxTableValue = adminService.getAuxTableValueByID(changeRequestDTO.getChangeNatureId());

			if (!baseContextIdentifier.isVersionYear() && ((ChangeRequestCategory.I == changeRequestDTO.getCategory())
					|| (ChangeRequestCategory.S == changeRequestDTO.getCategory()))) {
				errors.rejectValue("category", null,
						"Sorry, you cannot submit request for changes to index or supplements for non version year");
			}

			if (!baseContextIdentifier.isVersionYear() && ChangeRequestCategory.T == changeRequestDTO.getCategory()){
				if (!ChangeRequestNature.N.name().equals(auxTableValue.getAuxValueCode().trim()) &&
						!ChangeRequestNature.Z.name().equals(auxTableValue.getAuxValueCode().trim())){
					errors.rejectValue("changeNatureId", null,
							"Sorry, you cannot submit request for changes to " + auxTableValue.getAuxEngLable()
							+ " under category " + changeRequestDTO.getCategory().getCode() + " for non version year");
				}
			}

			if ((FreezingStatus.TAB == baseContextIdentifier.getFreezingStatus())
					&& (ChangeRequestCategory.T == changeRequestDTO.getCategory())) {
				errors.rejectValue("category", null, "The " + baseContextIdentifier.getVersionCode() + " "
						+ baseContextIdentifier.getBaseClassification() + " is frozen for tabular list changes.");
				// The <year> <classification: ICD-10-CA or CCI> classification tables are in progress. No changes can
				// be made
			}

			// valid evolution info
			if (changeRequestDTO.isEvolutionRequired()) { // if evolution required, all three evolution info required
				if (StringUtils.isBlank(changeRequestDTO.getEvolutionInfo().getEvolutionCodes())) {
					errors.rejectValue("evolutionInfo.evolutionCodes", null,
							"Evolution Codes can not be blank if Evolution Required checked ");
				}
				if (StringUtils.isBlank(changeRequestDTO.getEvolutionInfo().getEvolutionTextEng())) {
					errors.rejectValue("evolutionInfo.evolutionTextEng", null,
							"Evolution English comments can not be blank if Evolution Required checked ");
				}
				if (StringUtils.isBlank(changeRequestDTO.getEvolutionInfo().getEvolutionTextFra())) {
					errors.rejectValue("evolutionInfo.evolutionTextFra", null,
							"Evolution French comments can not be blank if Evolution Required checked ");
				}
			}

			if (changeRequestDTO.isPatternChange() && StringUtils.isBlank(changeRequestDTO.getPatternTopic())) {
				errors.rejectValue("patternTopic", null, "Pattern Topic can not be blank if Pattern Change checked ");
			}

			validateFirstQuestionForReviewer(changeRequestDTO, errors);

			List<DocumentReference> codingQuestions = changeRequestDTO.getCodingQuestions();
			// EQuery , must have equery coding question
			if (AuxTableValue.EQuery_AUX_VALUE_ID.longValue() == changeRequestDTO.getRequestorId().longValue()) {
				if ((codingQuestions == null) || (codingQuestions.size() == 0) || ((codingQuestions.size() == 1)
						&& StringUtils.isBlank(codingQuestions.get(0).geteQueryId()))) {
					errors.rejectValue("requestorId", null,
							"must have equery coding question Id if the requestor is eQuery");
				}
			}
			if ((codingQuestions != null) && (codingQuestions.size() > 0)) {
				for (DocumentReference codingQuestion : codingQuestions) {

					if (StringUtils.isBlank(codingQuestion.geteQueryId())
							&& StringUtils.isNotBlank(codingQuestion.getUrl())) {
						errors.rejectValue("codingQuestions", null,
								"Sorry, eQuery id can not be blank when you provide the URL");
					}
				}
			}
			// validate files, can't have two same files in the same section/ other or urc
			List<CommonsMultipartFile> urcFiles = changeRequestDTO.getUrcFiles();
			List<DocumentReference> urcAttachments = changeRequestDTO.getUrcAttachments();

			// have same urc files ?
			boolean haveDuplicatedUrcFiles = false;
			if ((urcFiles != null) && (urcFiles.size() > 0)) {
				List<String> urcFileNames = new ArrayList<String>();
				for (CommonsMultipartFile urcFile : urcFiles) {
					if (urcFile != null) {
						String urcFileName = urcFile.getOriginalFilename();
						if (StringUtils.isNotBlank(urcFileName)) {
							if (!urcFileNames.contains(urcFileName)) {
								urcFileNames.add(urcFileName);
							} else {
								haveDuplicatedUrcFiles = true;
								errors.rejectValue("urcAttachments", null,
										"Sorry, you can't submit same files for URC");
								break;
							}
							// check it in the urc attachments
							if ((urcAttachments != null) && (urcAttachments.size() > 0)) {
								for (DocumentReference urcAttachment : urcAttachments) {
									if (urcAttachment.getFileName().equalsIgnoreCase(urcFileName)) {
										haveDuplicatedUrcFiles = true;
										errors.rejectValue("urcAttachments", null,
												"Sorry, attachment uploading already exists");
										break;
									}
								}
								if (haveDuplicatedUrcFiles) {
									break;
								}
							}
						}
					}
				}

			}

			List<CommonsMultipartFile> otherFiles = changeRequestDTO.getOtherFiles();
			List<DocumentReference> otherAttachments = changeRequestDTO.getOtherAttachments();

			// have same Other files ?
			boolean haveDuplicatedOtherFiles = false;
			if ((otherFiles != null) && (otherFiles.size() > 0)) {
				List<String> otherFileNames = new ArrayList<String>();
				for (CommonsMultipartFile otherFile : otherFiles) {
					if (otherFile != null) {
						String otherFileName = otherFile.getOriginalFilename();
						if (StringUtils.isNotEmpty(otherFileName)) {
							if (!otherFileNames.contains(otherFileName)) {
								otherFileNames.add(otherFileName);
							} else {
								haveDuplicatedUrcFiles = true;
								errors.rejectValue("otherAttachments", null,
										"Sorry, you can't submit same files for Other Attachments");
								break;
							}
							// check it in the other attachments
							if ((otherAttachments != null) && (otherAttachments.size() > 0)) {
								for (DocumentReference otherAttachment : otherAttachments) {
									if (otherAttachment.getFileName().equalsIgnoreCase(otherFileName)) {
										haveDuplicatedOtherFiles = true;
										errors.rejectValue("urcAttachments", null,
												"Sorry, attachment uploading already exists");
										break;
									}
								}
								if (haveDuplicatedOtherFiles) {
									break;
								}
							}
						}
					}
				}

			}

		}

	}

	public void validateAddCommentForAdviceButton(Object target, Long adviceId, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		List<Advice> advices = changeRequestDTO.getAdvices();
		UserComment newEmptyAdviceComment = null;
		boolean commentEmpty = false;
		int i = 0;
		for (Advice advice : advices) {
			if (advice.getAdviceId().longValue() == adviceId.longValue()) {
				int j = 0;
				for (UserComment adviceComment : advice.getAdviceComments()) {
					if ((adviceComment.getUserCommentId() == null)
							&& (StringUtils.isBlank(adviceComment.getUserCommentTxt()))) { // new added
						newEmptyAdviceComment = adviceComment;
						commentEmpty = true;
						errors.rejectValue("advices[" + i + "].adviceComments[" + j + "].userCommentTxt", null,
								"advice comment can not be empty");
						break;
					}
					j++;
				}
				if (commentEmpty) {
					advice.getAdviceComments().remove(newEmptyAdviceComment);
					break;
				}
			}
		}
		i++;
	}

	public void validateAddCommentForQuestionButton(Object target, Long questionId, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		List<QuestionForReviewer> questionForReviewers = changeRequestDTO.getQuestionForReviewers();
		boolean commentEmpty = false;
		UserComment newEmptyComment = null;
		int i = 0;
		for (QuestionForReviewer questionForReviewer : questionForReviewers) {
			if (questionForReviewer.getQuestionForReviewerId().longValue() == questionId.longValue()) {
				int j = 0;
				for (UserComment questionComment : questionForReviewer.getQuestionComments()) {
					if ((questionComment.getUserCommentId() == null)
							&& (StringUtils.isBlank(questionComment.getUserCommentTxt()))) {
						newEmptyComment = questionComment;
						commentEmpty = true;
						errors.rejectValue("questionForReviewers[" + i + "].questionComments[" + j + "].userCommentTxt",
								null, "comment for question can not be empty");
						break;
					}
					j++;
				}
				if (commentEmpty) {
					questionForReviewer.getQuestionComments().remove(newEmptyComment);
					break;
				}
			}
			i++;
		}
	}

	public void validateDeferButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		if (StringUtils.isBlank(changeRequestDTO.getRationaleForClosedDeferred())) {
			errors.rejectValue("rationaleForClosedDeferred", null, "Rationale for Closed Deferred can not be empty");
		}
		if (changeRequestDTO.getDeferredToBaseContextId() == null) {
			errors.rejectValue("deferredToBaseContextId", null, "Defer to Year can not be empty");
		} else {

			ContextIdentifier deferredTobaseContextIdentifier = lookupService
					.findContextIdentificationById(changeRequestDTO.getDeferredToBaseContextId());

			if (!deferredTobaseContextIdentifier.isVersionYear()
					&& ((ChangeRequestCategory.I == changeRequestDTO.getCategory())
							|| (ChangeRequestCategory.S == changeRequestDTO.getCategory()))) {
				errors.rejectValue("category", null,
						"Sorry, you cannot defer request for changes to index or supplements to non version year");
			}
		}

	}

	private void validateFirstQuestionForReviewer(ChangeRequestDTO changeRequestDTO, Errors errors) {
		List<QuestionForReviewer> questionForReviewers = changeRequestDTO.getQuestionForReviewers();
		List<QuestionForReviewer> validquestionForReviewers = new ArrayList<QuestionForReviewer>();

		if ((questionForReviewers != null) && (questionForReviewers.size() > 0)) {
			for (QuestionForReviewer questionForReviewer : questionForReviewers) {
				if ((questionForReviewer.getReviewerId() != null)
						|| StringUtils.isNotEmpty(questionForReviewer.getQuestionForReviewerTxt())) {
					validquestionForReviewers.add(questionForReviewer);
				}
			}
			if (validquestionForReviewers.size() > 0) {
				QuestionForReviewer firstQuestion = validquestionForReviewers.get(0);
				if ((firstQuestion != null) && (firstQuestion.getReviewerId()
						.longValue() != Distribution.DL_ID_Classification.longValue())) {
					errors.rejectValue("questionForReviewers[0].reviewerId", null,
							"the Reviewer has to be DL - Classification for the first question. ");
				}
			}
			changeRequestDTO.setQuestionForReviewers(validquestionForReviewers);
		}

	}

	public void validateGetAdviceButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		if (StringUtils.isBlank(changeRequestDTO.getAdvice().getMessage())) {
			errors.rejectValue("advice.message", null, "Advice Message can not be empty");
		}
	}

	public void validateRejectButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		if (StringUtils.isBlank(changeRequestDTO.getRationaleForReject())) {
			errors.rejectValue("rationaleForReject", null, "Rationale for Reject can not be empty");
		}
	}

	public void validateSendBackButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		if (StringUtils.isBlank(changeRequestDTO.getRationaleForIncomplete().trim())) {
			errors.rejectValue("rationaleForIncomplete", null, "Send back message can not be empty");
		} else {
			if (changeRequestDTO.getRationaleForIncomplete().length() > 200) {
				errors.rejectValue("rationaleForIncomplete", null,
						"Send back message should be less than 200 characters");
			}
		}
	}

	public void validateSendForReviewButton(Object target, int questionIndex, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		List<QuestionForReviewer> questionForReviewers = changeRequestDTO.getQuestionForReviewers();
		if ((questionForReviewers == null) || (questionForReviewers.size() == 0)) {
			errors.rejectValue("questionForReviewers[" + questionIndex + "].questionForReviewerTxt", null,
					"Question can not be empty");
		} else {
			QuestionForReviewer sendQuestion = questionForReviewers.get(questionIndex);
			if (StringUtils.isBlank(sendQuestion.getQuestionForReviewerTxt())) {
				errors.rejectValue("questionForReviewers[" + questionIndex + "].questionForReviewerTxt", null,
						"Question can not be empty");
			}
		}
	}

	public void validateTakeOverButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		// validate whether it is taken over by others
		boolean takenOver = false;
		ChangeRequestDTO changeRequestInTable = changeRequestService
				.findCourseGrainedChangeRequestDTOById(changeRequestDTO.getChangeRequestId());
		if (changeRequestDTO.getAssigneeUserId() != null) {
			if (changeRequestInTable.getAssigneeUserId().longValue() != changeRequestDTO.getAssigneeUserId()
					.longValue()) {
				takenOver = true;
			}
		} else {
			if (changeRequestInTable.getAssigneeUserId() != null) {
				takenOver = true;
			}
		}
		if (takenOver) {
			errors.rejectValue("userAssignee.username", null,
					"The Request was already taken over by " + changeRequestInTable.getUserAssignee().getUsername()
							+ ", Please close current browser and refresh the main page");
		}
	}

	public void validateValidButton(Object target, Errors errors) {
		ChangeRequestDTO changeRequestDTO = (ChangeRequestDTO) target;
		validate(target, errors);
		if (StringUtils.isBlank(changeRequestDTO.getRationaleForValid())) {
			errors.rejectValue("rationaleForValid", null, "Rationale for Valid can not be empty");
		}
	}

}
