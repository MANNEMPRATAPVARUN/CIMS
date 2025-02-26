package ca.cihi.cims.model.changerequest;

import static ca.cihi.cims.Language.ALL;
import static ca.cihi.cims.util.CollectionUtils.asSet;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import ca.cihi.cims.Language;
import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.ReferenceTable;
import ca.cihi.cims.util.CimsUtils;

public class ChangeRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long changeRequestId;
	@NotNull(message = "Year must not be blank")
	private Long baseContextId;

	@NotBlank(message = "Classification must not be blank")
	private String baseClassification;

	private String baseVersionCode; // version year

	// ENG, FRA or ALL
	@Size(min = 3, max = 3, message = "Language Code must not be empty")
	private String languageCode;
	
	// ENG, FRA 
	private String evolutionLanguge;

	// removed the annotated validation to custom validation.
	private String name;
	@NotNull(message = "Request Category must not be blank")
	private ChangeRequestCategory category; // enum , "T("TabularList"),I("Index"), S("Supplements");

	@NotNull(message = "Requestor must not be blank")
	private Long requestorId; // foreign key to AuxTableValue

	@NotNull(message = "Nature of Change must not be blank")
	private Long changeNatureId; // foreign key to AuxTableValue
	@NotNull(message = "Type of Change must not be blank")
	private Long changeTypeId; // foreign key to AuxTableValue

	private Long assigneeUserId; // ASSIGNEE_ID, foreign key to user_prifile table
	private Long assigneeDLId; // assignee can also be a DL

	private Long ownerId; // foreign key to user_prifile table
	private Long assignorId; // ASIGNOR_ID, foreign key to user_prifile table

	// @Size(max = 4000, message = "Rationale for Change can not be over 4000 characters")
	@NotEmpty(message = "Rationale for Change can not be empty ")
	private String changeRationalTxt; // CLOB

	private Date lastUpdatedTime;
	private boolean indexRequired;
	private boolean evolutionRequired;
	private boolean conversionRequired;
	private boolean patternChange;
	@Size(max = 150, message = "Pattern Topic should be less than 150 characters")
	private String patternTopic;

	// @Size(max = 4000, message = "Send back message can not be over 200 characters")
	// @NotEmpty(message = "Send back message can not be empty ")
	private String rationaleForIncomplete;
	// @Size(max = 4000, message = "Rationale for Valid can not be over 4000 characters")
	private String rationaleForValid;
	// @Size(max = 4000, message = "Rationale for Close Defer can not be over 4000 characters")
	private String rationaleForClosedDeferred; // RATIONALE_FOR_CLOSED_DEFERRED
	// @Size(max = 4000, message = "Rationale for Reject can not be over 4000 characters")
	private String rationaleForReject;

	private Long deferredChangeRequestId; // will keep the deferred change request Id;

	private Long deferredToBaseContextId; // when user click the defer button, it is required

	private Date creationDate;

	private Long createdByUserId;
	private Long lastUpdatedByUserId;

	private ChangeRequestStatus status;

	private ChangeSummary changeSummary;

	protected boolean areTwoStringSame(String s1, String s2) {
		boolean isSame = false;
		if (s1 != null && s2 != null && s1.equals(s2)) {
			isSame = true;
		}
		if (StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2)) {
			isSame = true;
		}
		return isSame;
	}

	public void copyFrom(ChangeRequest other) {
		changeRequestId = other.getChangeRequestId();
		baseContextId = other.getBaseContextId();
		baseClassification = other.getBaseClassification();
		baseVersionCode = other.getBaseVersionCode();
		languageCode = other.getLanguageCode();
		evolutionLanguge = other.getEvolutionLanguage();
		name = other.getName();
		category = other.getCategory();
		requestorId = other.getRequestorId();
		changeNatureId = other.getChangeNatureId();
		changeTypeId = other.getChangeTypeId();
		assigneeUserId = other.getAssigneeUserId();
		assigneeDLId = other.getAssigneeDLId();
		ownerId = other.getOwnerId();
		assignorId = other.getAssignorId();
		changeRationalTxt = other.getChangeRationalTxt();
		lastUpdatedTime = other.getLastUpdatedTime();
		indexRequired = other.isIndexRequired();
		evolutionRequired = other.isEvolutionRequired();
		conversionRequired = other.isConversionRequired();
		patternChange = other.isPatternChange();
		patternTopic = other.getPatternTopic();
		rationaleForIncomplete = other.getRationaleForIncomplete();
		rationaleForValid = other.getRationaleForValid();
		rationaleForClosedDeferred = other.getRationaleForClosedDeferred();
		rationaleForReject = other.getRationaleForReject();
		deferredChangeRequestId = other.getDeferredChangeRequestId();
		deferredToBaseContextId = other.getDeferredToBaseContextId();
		status = other.getStatus();
	}

	public Long getAssigneeDLId() {
		return assigneeDLId;
	}

	public Long getAssigneeUserId() {
		return assigneeUserId;
	}

	public Long getAssignorId() {
		return assignorId;
	}

	public String getBaseClassification() {
		return baseClassification;
	}

	public Long getBaseContextId() {
		return baseContextId;
	}

	public String getBaseVersionCode() {
		return baseVersionCode;
	}

	public ChangeRequestCategory getCategory() {
		return category;
	}

	public Long getChangeNatureId() {
		return changeNatureId;
	}

	public String getChangeRationalTxt() {
		return changeRationalTxt;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public ChangeSummary getChangeSummary() {
		return changeSummary;
	}

	public Long getChangeTypeId() {
		return changeTypeId;
	}

	public Classification getClassification() {
		return Classification.fromBaseClassification(baseClassification);
	}

	public Long getCreatedByUserId() {
		return createdByUserId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Long getDeferredChangeRequestId() {
		return deferredChangeRequestId;
	}

	public Long getDeferredToBaseContextId() {
		return deferredToBaseContextId;
	}

	public String getLanguageCode() {
		return languageCode;
	}
	
	public String getEvolutionLanguage() {
		return evolutionLanguge;
	}

	public Set<Language> getLanguages() {
		return languageCode == null || languageCode.equals("ALL") ? ALL : Collections.unmodifiableSet(asSet(Language
				.fromString(languageCode)));
	}

	public Long getLastUpdatedByUserId() {
		return lastUpdatedByUserId;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public Long getLockTimestamp() {
		return getLastUpdatedTime().getTime();
	}

	public String getName() {
		return name;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public String getPatternTopic() {
		return patternTopic;
	}

	public String getRationaleForClosedDeferred() {
		return rationaleForClosedDeferred;
	}

	public String getRationaleForIncomplete() {
		return rationaleForIncomplete;
	}

	public String getRationaleForReject() {
		return rationaleForReject;
	}

	public String getRationaleForValid() {
		return rationaleForValid;
	}

	public Long getRequestorId() {
		return requestorId;
	}

	public ChangeRequestStatus getStatus() {
		return status;
	}

	public boolean isConversionRequired() {
		return conversionRequired;
	}

	public boolean isEvolutionRequired() {
		return evolutionRequired;
	}

	public boolean isIndexRequired() {
		return indexRequired;
	}

	public boolean isPatternChange() {
		return patternChange;
	}

	public void setAssigneeDLId(Long assigneeDLId) {
		this.assigneeDLId = assigneeDLId;
	}

	public void setAssigneeUserId(Long assigneeUserId) {
		this.assigneeUserId = assigneeUserId;
	}

	public void setAssignorId(Long assignorId) {
		this.assignorId = assignorId;
	}

	public void setBaseClassification(String baseClassification) {
		this.baseClassification = baseClassification;
	}

	public void setBaseContextId(Long baseContextId) {
		this.baseContextId = baseContextId;
	}

	public void setBaseVersionCode(String baseVersionCode) {
		this.baseVersionCode = baseVersionCode;
	}

	public void setCategory(ChangeRequestCategory category) {
		this.category = category;
	}

	public void setChangeNatureId(Long changeNatureId) {
		this.changeNatureId = changeNatureId;
	}

	public void setChangeRationalTxt(String changeRationalTxt) {
		this.changeRationalTxt = changeRationalTxt;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setChangeSummary(ChangeSummary changeSummary) {
		this.changeSummary = changeSummary;
	}

	public void setChangeTypeId(Long changeTypeId) {
		this.changeTypeId = changeTypeId;
	}

	public void setConversionRequired(boolean conversionRequired) {
		this.conversionRequired = conversionRequired;
	}

	public void setCreatedByUserId(Long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDeferredChangeRequestId(Long deferredChangeRequestId) {
		this.deferredChangeRequestId = deferredChangeRequestId;
	}

	public void setDeferredToBaseContextId(Long deferredToBaseContextId) {
		this.deferredToBaseContextId = deferredToBaseContextId;
	}

	public void setEvolutionRequired(boolean evolutionRequired) {
		this.evolutionRequired = evolutionRequired;
	}

	public void setIndexRequired(boolean indexRequired) {
		this.indexRequired = indexRequired;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	public void setEvolutionLanguage(String evolutionLanguage) {
		this.evolutionLanguge = evolutionLanguage;
	}

	public void setLastUpdatedByUserId(Long lastUpdatedByUserId) {
		this.lastUpdatedByUserId = lastUpdatedByUserId;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public void setPatternChange(boolean patternChange) {
		this.patternChange = patternChange;
	}

	public void setPatternTopic(String patternTopic) {
		this.patternTopic = patternTopic;
	}

	public void setRationaleForClosedDeferred(String rationaleForClosedDeferred) {
		this.rationaleForClosedDeferred = rationaleForClosedDeferred;
	}

	public void setRationaleForIncomplete(String rationaleForIncomplete) {
		this.rationaleForIncomplete = rationaleForIncomplete;
	}

	public void setRationaleForReject(String rationaleForReject) {
		this.rationaleForReject = rationaleForReject;
	}

	public void setRationaleForValid(String rationaleForValid) {
		this.rationaleForValid = rationaleForValid;
	}

	public void setRequestorId(Long requestorId) {
		this.requestorId = requestorId;
	}

	public void setStatus(ChangeRequestStatus status) {
		this.status = status;
	}

	public List<TrackingItem> tellLigthWeightBasicInfoDifferences(ChangeRequest otherChangeRequest,
			List<TrackingItem> trackingItems) {
		if (!otherChangeRequest.getBaseClassification().equals(getBaseClassification())) { // classification
			TrackingItem baseClassificationTracking = new TrackingItem(LabelType.Classification,
					getBaseClassification());
			trackingItems.add(baseClassificationTracking);
		}
		// version code
		if (!otherChangeRequest.getBaseVersionCode().equals(getBaseVersionCode())) {
			TrackingItem yearTracking = new TrackingItem(LabelType.Year, getBaseVersionCode());
			trackingItems.add(yearTracking);
		}
		if (otherChangeRequest.getCategory() != getCategory()) {
			TrackingItem requestCategoryTracking = new TrackingItem(LabelType.RequestCategory, getCategory().getCode());
			trackingItems.add(requestCategoryTracking);
		}
		if (!otherChangeRequest.getLanguageCode().equals(getLanguageCode())) { // language
			TrackingItem languageTracking = new TrackingItem(LabelType.Language, getLanguageCode(),
					ReferenceTable.CHANGE_REQUEST_LANGUAGE);
			trackingItems.add(languageTracking);
		}
		if (!getName().equals(otherChangeRequest.getName())) { // Name
			TrackingItem nameTracking = new TrackingItem(LabelType.RequestName, getName());
			trackingItems.add(nameTracking);
		}
		if (otherChangeRequest.getChangeNatureId().longValue() != getChangeNatureId().longValue()) {// Nature of Change
			TrackingItem changeNatureTracking = new TrackingItem(LabelType.NatureOfChange,
					String.valueOf(getChangeNatureId()), ReferenceTable.AUX_TABLE_VALUE);
			trackingItems.add(changeNatureTracking);
		}
		if (otherChangeRequest.getChangeTypeId().longValue() != getChangeTypeId().longValue()) {// Type of Change
			TrackingItem changeTypeTracking = new TrackingItem(LabelType.TypeOfChange,
					String.valueOf(getChangeTypeId()), ReferenceTable.AUX_TABLE_VALUE);
			trackingItems.add(changeTypeTracking);
		}
		if (otherChangeRequest.getStatus() != getStatus()) {// status
			TrackingItem statusTracking = new TrackingItem(LabelType.RequestStatus, getStatus().getStatusCode());
			trackingItems.add(statusTracking);
		}
		if (otherChangeRequest.getRequestorId().longValue() != getRequestorId().longValue()) {// Requestor
			TrackingItem requestorTracking = new TrackingItem(LabelType.Requestor, String.valueOf(getRequestorId()),
					ReferenceTable.AUX_TABLE_VALUE);
			trackingItems.add(requestorTracking);
		}

		// Index Required
		if (otherChangeRequest.isIndexRequired() != isIndexRequired()) {
			TrackingItem indexRequiredTracking = new TrackingItem(LabelType.IndexRequired,
					CimsUtils.getStringValue(isIndexRequired()));
			trackingItems.add(indexRequiredTracking);
		}
		// Evolution Required
		if (otherChangeRequest.isEvolutionRequired() != isEvolutionRequired()) {
			TrackingItem evolutionRequiredTracking = new TrackingItem(LabelType.EvolutionRequired,
					CimsUtils.getStringValue(isEvolutionRequired()));
			trackingItems.add(evolutionRequiredTracking);
		}

		// Conversion Required
		if (otherChangeRequest.isConversionRequired() != isConversionRequired()) {
			TrackingItem conversionRequiredTracking = new TrackingItem(LabelType.ConversionRequired,
					CimsUtils.getStringValue(isConversionRequired()));
			trackingItems.add(conversionRequiredTracking);
		}
		// Pattern Change
		if (otherChangeRequest.isPatternChange() != isPatternChange()) {
			TrackingItem patternChangeTracking = new TrackingItem(LabelType.PatternChange,
					CimsUtils.getStringValue(isPatternChange()));
			trackingItems.add(patternChangeTracking);
		}
		// Pattern Topic
		if (isPatternChange() && !areTwoStringSame(otherChangeRequest.getPatternTopic(), getPatternTopic())) {
			TrackingItem patternTopicTracking = new TrackingItem(LabelType.PatternTopic, getPatternTopic());
			trackingItems.add(patternTopicTracking);
		}
		return trackingItems;
	}
}
