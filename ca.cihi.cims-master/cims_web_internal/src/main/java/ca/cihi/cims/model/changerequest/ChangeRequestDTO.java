package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.ReferenceTable;
import ca.cihi.cims.model.User;
import ca.cihi.cims.util.CimsUtils;

public class ChangeRequestDTO extends ChangeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AuxTableValue changeNature;

	private AuxTableValue requestor;

	private AuxTableValue changeType;

	private User userAssignee;

	private Distribution dlAssignee;

	private User owner;

	private User assignor;
	@Valid
	private ChangeRequestEvolution evolutionInfo;
	@Valid
	private List<QuestionForReviewer> questionForReviewers;
	@Valid
	private List<UserComment> commentDiscussions; // comments for Change Request;

	private List<DocumentReference> codingQuestions;

	private List<DocumentReference> urcAttachments;
	private List<DocumentReference> urcLinks;

	private List<DocumentReference> otherAttachments;
	private List<DocumentReference> otherLinks;

	private List<Advice> advices;

	@NotNull(message = "Review Groups must not be blank")
	private List<Distribution> reviewGroups;

	// private User currentUser; // the current login user, who do query and modify

	private List<CommonsMultipartFile> urcFiles;

	private List<CommonsMultipartFile> otherFiles;

	private String assignedTo; // can be DL_+dl id or USER_+ user id

	private String adviceRecipient; // can be DL_+dl id or USER_+ user id

	private Advice advice; // new advice

	private Long transferedTo; // only can be userId

	private ChangeRequest deferredTo; // when the change request is deferred,

	public Advice getAdvice() {
		return advice;
	}

	public String getAdviceRecipient() {
		return adviceRecipient;
	}

	public List<Advice> getAdvices() {
		return advices;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public User getAssignor() {
		return assignor;
	}

	public AuxTableValue getChangeNature() {
		return changeNature;
	}

	public AuxTableValue getChangeType() {
		return changeType;
	}

	public List<DocumentReference> getCodingQuestions() {
		return codingQuestions;
	}

	public List<UserComment> getCommentDiscussions() {
		return commentDiscussions;
	}

	public ChangeRequest getDeferredTo() {
		return deferredTo;
	}

	public Distribution getDlAssignee() {
		return dlAssignee;
	}

	public ChangeRequestEvolution getEvolutionInfo() {
		return evolutionInfo;
	}

	public List<DocumentReference> getOtherAttachments() {
		return otherAttachments;
	}

	public List<CommonsMultipartFile> getOtherFiles() {
		return otherFiles;
	}

	public List<DocumentReference> getOtherLinks() {
		return otherLinks;
	}

	public User getOwner() {
		return owner;
	}

	public List<QuestionForReviewer> getQuestionForReviewers() {
		return questionForReviewers;
	}

	public AuxTableValue getRequestor() {
		return requestor;
	}

	public List<Distribution> getReviewGroups() {
		return reviewGroups;
	}

	public List<TrackingItem> getTrackingItemsForCreating(List<TrackingItem> trackingItems) {
		TrackingItem languageTracking = new TrackingItem(LabelType.Language, getLanguageCode(),
				ReferenceTable.CHANGE_REQUEST_LANGUAGE);
		trackingItems.add(languageTracking);
		// baseClassification and year are not in changeRequest object when creating, but in changeRequest object after
		// created
		// in getCourseGrainedChangeRequestDTO
		TrackingItem baseClassificationTracking = new TrackingItem(LabelType.Classification, getBaseClassification());
		trackingItems.add(baseClassificationTracking);
		// version code
		TrackingItem yearTracking = new TrackingItem(LabelType.Year, getBaseVersionCode());
		trackingItems.add(yearTracking);
		// request name
		TrackingItem nameTracking = new TrackingItem(LabelType.RequestName, getName());
		trackingItems.add(nameTracking);
		// Request Category
		TrackingItem requestCategoryTracking = new TrackingItem(LabelType.RequestCategory, getCategory().getCode());
		trackingItems.add(requestCategoryTracking);
		// Nature of Change
		TrackingItem changeNatureTracking = new TrackingItem(LabelType.NatureOfChange,
				String.valueOf(getChangeNatureId()), ReferenceTable.AUX_TABLE_VALUE);
		trackingItems.add(changeNatureTracking);
		// Type of Change
		TrackingItem changeTypeTracking = new TrackingItem(LabelType.TypeOfChange, String.valueOf(getChangeTypeId()),
				ReferenceTable.AUX_TABLE_VALUE);
		trackingItems.add(changeTypeTracking);
		// status
		TrackingItem statusTracking = new TrackingItem(LabelType.RequestStatus, getStatus().getStatusCode());
		trackingItems.add(statusTracking);
		// Requestor
		TrackingItem requestorTracking = new TrackingItem(LabelType.Requestor, String.valueOf(getRequestorId()),
				ReferenceTable.AUX_TABLE_VALUE);
		trackingItems.add(requestorTracking);
		// review groups
		StringBuilder sbReviewGroups = new StringBuilder();
		for (int i = 0; i < reviewGroups.size() - 1; i++) {
			sbReviewGroups.append(reviewGroups.get(i).getName()).append(",");
		}
		sbReviewGroups.append(reviewGroups.get(reviewGroups.size() - 1).getName());
		TrackingItem reviewGroupsTracking = new TrackingItem(LabelType.ReviewGroups, sbReviewGroups.toString());
		trackingItems.add(reviewGroupsTracking);
		// Index Required
		TrackingItem indexRequiredTracking = new TrackingItem(LabelType.IndexRequired,
				CimsUtils.getStringValue(isIndexRequired()));
		trackingItems.add(indexRequiredTracking);
		// Evolution Required
		TrackingItem evolutionRequiredTracking = new TrackingItem(LabelType.EvolutionRequired,
				CimsUtils.getStringValue(isEvolutionRequired()));
		trackingItems.add(evolutionRequiredTracking);
		// Conversion Required
		TrackingItem conversionRequiredTracking = new TrackingItem(LabelType.ConversionRequired,
				CimsUtils.getStringValue(isConversionRequired()));
		trackingItems.add(conversionRequiredTracking);
		// Pattern Change
		TrackingItem patternChangeTracking = new TrackingItem(LabelType.PatternChange,
				CimsUtils.getStringValue(isPatternChange()));
		trackingItems.add(patternChangeTracking);
		// Pattern Topic
		if (isPatternChange()) {
			TrackingItem patternTopicTracking = new TrackingItem(LabelType.PatternTopic, getPatternTopic());
			trackingItems.add(patternTopicTracking);
		}
		if (isEvolutionRequired()) {
			TrackingItem evolutionCodesTracking = new TrackingItem(LabelType.EvolutionCodes, getEvolutionInfo()
					.getEvolutionCodes()); // Evolution Codes
			trackingItems.add(evolutionCodesTracking);
			TrackingItem evolutionEngCommentsTracking = new TrackingItem(LabelType.EvolutionEngComments,
					getEvolutionInfo().getEvolutionTextEng()); // Evolution English Comments
			trackingItems.add(evolutionEngCommentsTracking);
			TrackingItem evolutionFraCommentsTracking = new TrackingItem(LabelType.EvolutionFraComments,
					getEvolutionInfo().getEvolutionTextFra()); // Evolution French Comments
			trackingItems.add(evolutionFraCommentsTracking);
		}
		// Rationale for Change
		TrackingItem rationaleForChangeTracking = new TrackingItem(LabelType.RationalForChange, getChangeRationalTxt());
		trackingItems.add(rationaleForChangeTracking);
		// codingQuestions
		if (codingQuestions != null && codingQuestions.size() > 0) {
			StringBuilder sbCodingQuestions = new StringBuilder();
			for (int i = 0; i < codingQuestions.size() - 1; i++) {
				sbCodingQuestions.append("Id: ").append(codingQuestions.get(i).geteQueryId()).append(",  ");
				sbCodingQuestions.append("URL: ").append(codingQuestions.get(i).getUrl()).append("<br/>");
			}
			sbCodingQuestions.append("Id: ").append(codingQuestions.get(codingQuestions.size() - 1).geteQueryId())
					.append(",  ");
			sbCodingQuestions.append("URL: ").append(codingQuestions.get(codingQuestions.size() - 1).getUrl());
			TrackingItem codingQuestionsTracking = new TrackingItem(LabelType.CodingQuestions,
					sbCodingQuestions.toString());
			trackingItems.add(codingQuestionsTracking);
		}
		// URC Attachments
		if (urcAttachments != null && urcAttachments.size() > 0) {
			StringBuilder sbUrcAttachments = new StringBuilder();
			for (int i = 0; i < urcAttachments.size() - 1; i++) {
				sbUrcAttachments.append(urcAttachments.get(i).getFileName()).append("</br>");
			}
			sbUrcAttachments.append(urcAttachments.get(urcAttachments.size() - 1).getFileName());
			TrackingItem urcAttachmentsTracking = new TrackingItem(LabelType.UrcAttachments,
					sbUrcAttachments.toString());
			trackingItems.add(urcAttachmentsTracking);
		}
		// URC Links
		if (urcLinks != null && urcLinks.size() > 0) {
			StringBuilder sbUrcLinks = new StringBuilder();
			for (int i = 0; i < urcLinks.size() - 1; i++) {
				sbUrcLinks.append(urcLinks.get(i).getUrl()).append("</br>");
			}
			sbUrcLinks.append(urcLinks.get(urcLinks.size() - 1).getUrl());
			TrackingItem urcLinksTracking = new TrackingItem(LabelType.UrcLinks, sbUrcLinks.toString());
			trackingItems.add(urcLinksTracking);
		}
		// Other Attachments
		if (otherAttachments != null && otherAttachments.size() > 0) {
			StringBuilder sbOtherAttachments = new StringBuilder();
			for (int i = 0; i < otherAttachments.size() - 1; i++) {
				sbOtherAttachments.append(otherAttachments.get(i).getFileName()).append("</br>");
			}
			sbOtherAttachments.append(otherAttachments.get(otherAttachments.size() - 1).getFileName());
			TrackingItem otherAttachmentsTracking = new TrackingItem(LabelType.OtherAttachments,
					sbOtherAttachments.toString());
			trackingItems.add(otherAttachmentsTracking);
		}

		// Other Links
		if (otherLinks != null && otherLinks.size() > 0) {
			StringBuilder sbOtherLinks = new StringBuilder();

			for (int i = 0; i < otherLinks.size() - 1; i++) {
				sbOtherLinks.append(otherLinks.get(i).getUrl()).append("</br>");
			}
			sbOtherLinks.append(otherLinks.get(otherLinks.size() - 1).getUrl());
			TrackingItem otherLinksTracking = new TrackingItem(LabelType.OtherLinks, sbOtherLinks.toString());
			trackingItems.add(otherLinksTracking);
		}

		// Owner
		TrackingItem ownerTracking = new TrackingItem(LabelType.Owner, owner.getUsername());
		trackingItems.add(ownerTracking);
		// Assignee
		TrackingItem assigneeTracking = new TrackingItem(LabelType.Assignee,
				userAssignee != null ? userAssignee.getUsername() : dlAssignee.getName());
		trackingItems.add(assigneeTracking);
		// Creation Time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		TrackingItem createdTimeTracking = new TrackingItem(LabelType.CreationTime, df.format(getLastUpdatedTime()));
		trackingItems.add(createdTimeTracking);

		return trackingItems;
	}

	public Long getTransferedTo() {
		return transferedTo;
	}

	public List<DocumentReference> getUrcAttachments() {
		return urcAttachments;
	}

	public List<CommonsMultipartFile> getUrcFiles() {
		return urcFiles;
	}

	public List<DocumentReference> getUrcLinks() {
		return urcLinks;
	}

	public User getUserAssignee() {
		return userAssignee;
	}

	@SuppressWarnings("unchecked")
	private boolean isReviewGroupsSame(List<Distribution> reviewGroups1, List<Distribution> reviewGroups2) {
		boolean isSame = true;
		if (reviewGroups1.size() == reviewGroups2.size()) {
			Collections.sort(reviewGroups1);
			Collections.sort(reviewGroups2);
			for (int i = 0; i < reviewGroups1.size(); i++) {
				if (reviewGroups1.get(i).getDistributionlistid().longValue() != reviewGroups2.get(i)
						.getDistributionlistid().longValue()) {
					isSame = false;
					break;
				}
			}
		} else {
			isSame = false;
		}
		return isSame;
	}

	public void setAdvice(Advice advice) {
		this.advice = advice;
	}

	public void setAdviceRecipient(String adviceRecipient) {
		this.adviceRecipient = adviceRecipient;
	}

	public void setAdvices(List<Advice> advices) {
		this.advices = advices;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public void setAssignor(User assignor) {
		this.assignor = assignor;
	}

	public void setChangeNature(AuxTableValue changeNature) {
		this.changeNature = changeNature;
	}

	public void setChangeType(AuxTableValue changeType) {
		this.changeType = changeType;
	}

	public void setCodingQuestions(List<DocumentReference> codingQuestions) {
		this.codingQuestions = codingQuestions;
	}

	public void setCommentDiscussions(List<UserComment> commentDiscussions) {
		this.commentDiscussions = commentDiscussions;
	}

	public void setDeferredTo(ChangeRequest deferredTo) {
		this.deferredTo = deferredTo;
	}

	public void setDlAssignee(Distribution dlAssignee) {
		this.dlAssignee = dlAssignee;
	}

	public void setEvolutionInfo(ChangeRequestEvolution evolutionInfo) {
		this.evolutionInfo = evolutionInfo;
	}

	public void setOtherAttachments(List<DocumentReference> otherAttachments) {
		this.otherAttachments = otherAttachments;
	}

	public void setOtherFiles(List<CommonsMultipartFile> otherFiles) {
		this.otherFiles = otherFiles;
	}

	public void setOtherLinks(List<DocumentReference> otherLinks) {
		this.otherLinks = otherLinks;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setQuestionForReviewers(List<QuestionForReviewer> questionForReviewers) {
		this.questionForReviewers = questionForReviewers;
	}

	public void setRequestor(AuxTableValue requestor) {
		this.requestor = requestor;
	}

	public void setReviewGroups(List<Distribution> reviewGroups) {
		this.reviewGroups = reviewGroups;
	}

	public void setTransferedTo(Long transferedTo) {
		this.transferedTo = transferedTo;
	}

	public void setUrcAttachments(List<DocumentReference> urcAttachments) {
		this.urcAttachments = urcAttachments;
	}

	public void setUrcFiles(List<CommonsMultipartFile> urcFiles) {
		this.urcFiles = urcFiles;
	}

	public void setUrcLinks(List<DocumentReference> urcLinks) {
		this.urcLinks = urcLinks;
	}

	public void setUserAssignee(User userAssignee) {
		this.userAssignee = userAssignee;
	}

	public List<TrackingItem> tellBasicInfoDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {

		trackingItems = tellLigthWeightBasicInfoDifferences(otherChangeRequest, trackingItems);
		// review groups
		if (!isReviewGroupsSame(otherChangeRequest.getReviewGroups(), getReviewGroups())) {
			StringBuilder sbReviewGroups = new StringBuilder();
			for (int i = 0; i < reviewGroups.size() - 1; i++) {
				sbReviewGroups.append(reviewGroups.get(i).getName()).append(",");
			}
			sbReviewGroups.append(reviewGroups.get(reviewGroups.size() - 1).getName());
			TrackingItem reviewGroupsTracking = new TrackingItem(LabelType.ReviewGroups, sbReviewGroups.toString());
			trackingItems.add(reviewGroupsTracking);
		}
		return trackingItems;
	}

	public List<TrackingItem> tellDifferences(ChangeRequestDTO otherChangeRequest, List<TrackingItem> trackingItems) {
		trackingItems = tellBasicInfoDifferences(otherChangeRequest, trackingItems);
		trackingItems = tellEvolutionInfoDifferences(otherChangeRequest, trackingItems);
		trackingItems = tellRationaleDifferences(otherChangeRequest, trackingItems);
		trackingItems = tellQuestionForReviewerDifferences(otherChangeRequest, trackingItems);
		trackingItems = tellReferencesDifferences(otherChangeRequest, trackingItems);
		trackingItems = tellOwnerAndAssigneeDifferences(otherChangeRequest, trackingItems);

		return trackingItems;
	}

	private List<TrackingItem> tellEvolutionInfoDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {
		// EvolutionInfo
		if (otherChangeRequest.isEvolutionRequired() && isEvolutionRequired()) {
			if (!areTwoStringSame(otherChangeRequest.getEvolutionInfo().getEvolutionCodes(), getEvolutionInfo()
					.getEvolutionCodes())) {
				TrackingItem evolutionCodesTracking = new TrackingItem(LabelType.EvolutionCodes, getEvolutionInfo()
						.getEvolutionCodes()); // Evolution Codes
				trackingItems.add(evolutionCodesTracking);
			}
			if (!areTwoStringSame(otherChangeRequest.getEvolutionInfo().getEvolutionTextEng(), getEvolutionInfo()
					.getEvolutionTextEng())) {
				TrackingItem evolutionEngCommentsTracking = new TrackingItem(LabelType.EvolutionEngComments,
						getEvolutionInfo().getEvolutionTextEng()); // Evolution English Comments
				trackingItems.add(evolutionEngCommentsTracking);
			}
			if (!areTwoStringSame(otherChangeRequest.getEvolutionInfo().getEvolutionTextFra(), getEvolutionInfo()
					.getEvolutionTextFra())) {
				TrackingItem evolutionFraCommentsTracking = new TrackingItem(LabelType.EvolutionFraComments,
						getEvolutionInfo().getEvolutionTextFra()); // Evolution French Comments
				trackingItems.add(evolutionFraCommentsTracking);
			}
		}
		if (!otherChangeRequest.isEvolutionRequired() && isEvolutionRequired()) {
			TrackingItem evolutionCodesTracking = new TrackingItem(LabelType.EvolutionCodes, getEvolutionInfo()
					.getEvolutionCodes()); // Evolution Codes
			trackingItems.add(evolutionCodesTracking);
			TrackingItem evolutionEngCommentsTracking = new TrackingItem(LabelType.EvolutionEngComments,
					getEvolutionInfo().getEvolutionTextEng()); // Evolution English Comments
			trackingItems.add(evolutionEngCommentsTracking);
			TrackingItem evolutionFraCommentsTracking = new TrackingItem(LabelType.EvolutionFraComments,
					getEvolutionInfo().getEvolutionTextFra()); // Evolution French Comments
			trackingItems.add(evolutionFraCommentsTracking);
		}
		return trackingItems;
	}

	private List<TrackingItem> tellOwnerAndAssigneeDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {

		if (getOwnerId() != null) {
			if (otherChangeRequest.getOwnerId().longValue() != getOwnerId().longValue()) { // owner
				TrackingItem ownerTracking = new TrackingItem(LabelType.Owner, owner.getUsername());
				trackingItems.add(ownerTracking);
			}
		} else {
			TrackingItem ownerTracking = new TrackingItem(LabelType.Owner, null);
			trackingItems.add(ownerTracking);
		}

		if (otherChangeRequest.getAssigneeUserId() != null && getAssigneeUserId() != null
				&& otherChangeRequest.getAssigneeUserId().longValue() != getAssigneeUserId().longValue()) {
			TrackingItem assigneeTracking = new TrackingItem(LabelType.Assignee, userAssignee.getUsername());
			trackingItems.add(assigneeTracking);
		}
		if (otherChangeRequest.getAssigneeDLId() != null && getAssigneeDLId() != null
				&& (otherChangeRequest.getAssigneeDLId().longValue() != getAssigneeDLId().longValue())) {
			TrackingItem assigneeTracking = new TrackingItem(LabelType.Assignee, dlAssignee.getName());
			trackingItems.add(assigneeTracking);
		}
		if (otherChangeRequest.getAssigneeDLId() != null && getAssigneeUserId() != null) {
			TrackingItem assigneeTracking = new TrackingItem(LabelType.Assignee, userAssignee.getUsername());
			trackingItems.add(assigneeTracking);
		}
		if (otherChangeRequest.getAssigneeUserId() != null && getAssigneeDLId() != null) {
			TrackingItem assigneeTracking = new TrackingItem(LabelType.Assignee, dlAssignee.getName());
			trackingItems.add(assigneeTracking);
		}
		return trackingItems;
	}

	private List<TrackingItem> tellQuestionForReviewerDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {

		int numOfQuestionsInOldCR = 0;
		int numOfQuestionsInNewCR = 0;
		List<QuestionForReviewer> oldQFRs = otherChangeRequest.getQuestionForReviewers();
		if (oldQFRs != null) {
			numOfQuestionsInOldCR = oldQFRs.size();
		}
		List<QuestionForReviewer> newQFRs = getQuestionForReviewers();
		if (newQFRs != null) {
			numOfQuestionsInNewCR = newQFRs.size();
		}
		int numOfNewQuestions = numOfQuestionsInNewCR - numOfQuestionsInOldCR;
		if (numOfNewQuestions > 0) { // 1. new one has more questions
			for (int i = numOfQuestionsInOldCR; i < numOfQuestionsInNewCR; i++) {
				QuestionForReviewer newQuestionForReviewer = newQFRs.get(i);
				String qfrNumb = String.valueOf(i + 1);
				String labelNumberDesc = "Question " + qfrNumb;
				String labelReviewerDesc = "Question " + qfrNumb + " Reviewer";
				String labelContentDesc = "Question " + qfrNumb + " Content";

				TrackingItem qfrReviewerTracking = new TrackingItem(LabelType.QFRReviewer, labelReviewerDesc,
						String.valueOf(newQuestionForReviewer.getReviewerId()), ReferenceTable.DISTRIBUTION_LIST);
				trackingItems.add(qfrReviewerTracking);
				TrackingItem qfrContentTracking = new TrackingItem(LabelType.QFRContent, labelContentDesc,
						newQuestionForReviewer.getQuestionForReviewerTxt());
				trackingItems.add(qfrContentTracking);
				if (newQuestionForReviewer.isBeenSentOut()) {
					TrackingItem qfrNumSentTracking = new TrackingItem(LabelType.QFRNum, labelNumberDesc,
							" has been sent out");
					trackingItems.add(qfrNumSentTracking);
				}
			}
		} else { // 2. old one and new one have same number of questions, but content changes /reviewer changes or sent
					// out
			for (int j = 0; j < numOfQuestionsInOldCR; j++) {
				QuestionForReviewer oldQFR = oldQFRs.get(j);
				QuestionForReviewer newQFR = newQFRs.get(j);
				String qfrNumb = String.valueOf(j + 1);
				String labelNumberDesc = "Question " + qfrNumb;
				if (!oldQFR.isBeenSentOut()) {
					String labelReviewerDesc = "Question " + qfrNumb + " Reviewer";
					String labelContentDesc = "Question " + qfrNumb + " Content";
					if (oldQFR.getReviewerId().longValue() != newQFR.getReviewerId().longValue()) { // reviewer changes
						TrackingItem qfrReviewerTracking = new TrackingItem(LabelType.QFRReviewer, labelReviewerDesc,
								String.valueOf(newQFR.getReviewerId()), ReferenceTable.DISTRIBUTION_LIST);
						trackingItems.add(qfrReviewerTracking);
					}
					if (!areTwoStringSame(oldQFR.getQuestionForReviewerTxt(), newQFR.getQuestionForReviewerTxt())) { // content
																														// changes
						TrackingItem qfrContentTracking = new TrackingItem(LabelType.QFRContent, labelContentDesc,
								newQFR.getQuestionForReviewerTxt());
						trackingItems.add(qfrContentTracking);
					}
					if (newQFR.isBeenSentOut()) {
						TrackingItem qfrNumSentTracking = new TrackingItem(LabelType.QFRNum, labelNumberDesc,
								" has been sent out");
						trackingItems.add(qfrNumSentTracking);
					}
				}
			}

		}
		return trackingItems;
	}

	private List<TrackingItem> tellRationaleDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {
		if (!areTwoStringSame(otherChangeRequest.getChangeRationalTxt(), getChangeRationalTxt())) {
			TrackingItem rationaleForChangeTracking = new TrackingItem(LabelType.RationalForChange,
					getChangeRationalTxt());
			trackingItems.add(rationaleForChangeTracking);
		}
		if (!areTwoStringSame(otherChangeRequest.getRationaleForValid(), getRationaleForValid())) {
			TrackingItem rationaleForValidTracking = new TrackingItem(LabelType.RationalForValid,
					getRationaleForValid());
			trackingItems.add(rationaleForValidTracking);
		}

		if (StringUtils.isNotBlank(getRationaleForIncomplete())) {
			if (!areTwoStringSame(otherChangeRequest.getRationaleForIncomplete(), getRationaleForIncomplete())) {
				TrackingItem rationaleForIncompleteTracking = new TrackingItem(LabelType.RationalForIncomp,
						getRationaleForIncomplete());
				trackingItems.add(rationaleForIncompleteTracking);
			}
		}

		if (!areTwoStringSame(otherChangeRequest.getRationaleForReject(), getRationaleForReject())) {
			TrackingItem rationaleForRejectTracking = new TrackingItem(LabelType.RationalForRejected,
					getRationaleForReject());
			trackingItems.add(rationaleForRejectTracking);
		}
		if (!areTwoStringSame(otherChangeRequest.getRationaleForClosedDeferred(), getRationaleForClosedDeferred())) {
			TrackingItem rationaleForDeferredTracking = new TrackingItem(LabelType.RationalForDeferred,
					getRationaleForClosedDeferred());
			trackingItems.add(rationaleForDeferredTracking);
		}
		return trackingItems;
	}

	private List<TrackingItem> tellReferencesDifferences(ChangeRequestDTO otherChangeRequest,
			List<TrackingItem> trackingItems) {
		// coding questions
		if (!CimsUtils.areTwoDocumentReferencesListSame(otherChangeRequest.getCodingQuestions(), getCodingQuestions())) { // coding
																															// questions
			if (codingQuestions != null && codingQuestions.size() > 0) {
				StringBuilder sbCodingQuestions = new StringBuilder();
				for (int i = 0; i < codingQuestions.size() - 1; i++) {
					sbCodingQuestions.append("Id: ").append(codingQuestions.get(i).geteQueryId()).append(",  ");
					if (codingQuestions.get(i).getUrl() != null) {
						sbCodingQuestions.append("URL: ").append(codingQuestions.get(i).getUrl()).append("<br/>");
					} else {
						sbCodingQuestions.append("URL: ").append(" ").append("<br/>");
					}
				}
				sbCodingQuestions.append("Id: ").append(codingQuestions.get(codingQuestions.size() - 1).geteQueryId())
						.append(",  ");
				if (codingQuestions.get(codingQuestions.size() - 1).getUrl() != null) {
					sbCodingQuestions.append("URL: ").append(codingQuestions.get(codingQuestions.size() - 1).getUrl());
				} else {
					sbCodingQuestions.append("URL: ").append(" ");
				}
				TrackingItem codingQuestionsTracking = new TrackingItem(LabelType.CodingQuestions,
						sbCodingQuestions.toString());
				trackingItems.add(codingQuestionsTracking);
			} else {
				TrackingItem codingQuestionsTracking = new TrackingItem(LabelType.CodingQuestions, null);
				trackingItems.add(codingQuestionsTracking);
			}
		}
		// urc attachments
		if (!CimsUtils.areTwoDocumentReferencesListSame(otherChangeRequest.getUrcAttachments(), getUrcAttachments())) {
			if (urcAttachments != null && urcAttachments.size() > 0) {
				StringBuilder sbUrcAttachments = new StringBuilder();
				for (int i = 0; i < urcAttachments.size() - 1; i++) {
					sbUrcAttachments.append(urcAttachments.get(i).getFileName()).append("</br>");
				}
				sbUrcAttachments.append(urcAttachments.get(urcAttachments.size() - 1).getFileName());
				TrackingItem urcAttachmentsTracking = new TrackingItem(LabelType.UrcAttachments,
						sbUrcAttachments.toString());
				trackingItems.add(urcAttachmentsTracking);
			} else {
				TrackingItem urcAttachmentsTracking = new TrackingItem(LabelType.UrcAttachments, null);
				trackingItems.add(urcAttachmentsTracking);
			}
		}
		// urc links
		if (!CimsUtils.areTwoDocumentReferencesListSame(otherChangeRequest.getUrcLinks(), getUrcLinks())) {
			if (urcLinks != null && urcLinks.size() > 0) {
				StringBuilder sbUrcLinks = new StringBuilder();
				for (int i = 0; i < urcLinks.size() - 1; i++) {
					sbUrcLinks.append(urcLinks.get(i).getUrl()).append("</br>");
				}
				sbUrcLinks.append(urcLinks.get(urcLinks.size() - 1).getUrl());
				TrackingItem urcLinksTracking = new TrackingItem(LabelType.UrcLinks, sbUrcLinks.toString());
				trackingItems.add(urcLinksTracking);
			} else {
				TrackingItem urcLinksTracking = new TrackingItem(LabelType.UrcLinks, null);
				trackingItems.add(urcLinksTracking);
			}
		}
		// other attachments
		if (!CimsUtils
				.areTwoDocumentReferencesListSame(otherChangeRequest.getOtherAttachments(), getOtherAttachments())) {
			if (otherAttachments != null && otherAttachments.size() > 0) {
				StringBuilder sbOtherAttachments = new StringBuilder();
				for (int i = 0; i < otherAttachments.size() - 1; i++) {
					sbOtherAttachments.append(otherAttachments.get(i).getFileName()).append("</br>");
				}
				sbOtherAttachments.append(otherAttachments.get(otherAttachments.size() - 1).getFileName());
				TrackingItem otherAttachmentsTracking = new TrackingItem(LabelType.OtherAttachments,
						sbOtherAttachments.toString());
				trackingItems.add(otherAttachmentsTracking);

			} else {
				TrackingItem otherAttachmentsTracking = new TrackingItem(LabelType.OtherAttachments, null);
				trackingItems.add(otherAttachmentsTracking);
			}
		}
		// other Links
		if (!CimsUtils.areTwoDocumentReferencesListSame(otherChangeRequest.getOtherLinks(), getOtherLinks())) {
			if (otherLinks != null && otherLinks.size() > 0) {
				StringBuilder sbOtherLinks = new StringBuilder();

				for (int i = 0; i < otherLinks.size() - 1; i++) {
					sbOtherLinks.append(otherLinks.get(i).getUrl()).append("</br>");
				}
				sbOtherLinks.append(otherLinks.get(otherLinks.size() - 1).getUrl());
				TrackingItem otherLinksTracking = new TrackingItem(LabelType.OtherLinks, sbOtherLinks.toString());
				trackingItems.add(otherLinksTracking);
			} else {
				TrackingItem otherLinksTracking = new TrackingItem(LabelType.OtherLinks, null);
				trackingItems.add(otherLinksTracking);
			}
		}

		return trackingItems;
	}

}
