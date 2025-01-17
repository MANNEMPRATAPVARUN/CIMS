package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;

import ca.cihi.cims.data.mapper.ChangeRequestHistoryMapper;
import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.model.ReferenceTable;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ActionType;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestHistory;
import ca.cihi.cims.model.changerequest.ChangeRequestHistoryItem;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.TrackingItem;


public class ChangeRequestHistoryServiceImpl implements ChangeRequestHistoryService {

	private ChangeRequestMapper changeRequestMapper;

	private ChangeRequestHistoryMapper changeRequestHistoryMapper;

	private LookupMapper lookupMapper;

	@Override
	@Async
	public void createChangeRequestHistoryForUpdating (ChangeRequestDTO oldChangeRequest,User currentUser){

		ChangeRequestDTO newChangeRequestInTable =changeRequestMapper.findCourseGrainedChangeRequestById(oldChangeRequest.getChangeRequestId());
		List<QuestionForReviewer> questionForReviewers = changeRequestMapper.findReviewQuestionsForChangeRequest(newChangeRequestInTable.getChangeRequestId());
		for (QuestionForReviewer questionForReviewer : questionForReviewers) {
			if (questionForReviewer.getSentOutNotification() != null) {
				questionForReviewer.setBeenSentOut(true);
			}
		}
		newChangeRequestInTable.setQuestionForReviewers(questionForReviewers);

		List<TrackingItem> trackingItems = new ArrayList<TrackingItem>();
		trackingItems = newChangeRequestInTable.tellDifferences(oldChangeRequest, trackingItems);
		if (trackingItems.size()>0){
			ChangeRequestHistory changeRequestHistory = new ChangeRequestHistory();
			changeRequestHistory.setChangeRequestId(newChangeRequestInTable.getChangeRequestId());
			changeRequestHistory.setModifiedByUser(currentUser.getUsername());
			changeRequestHistory.setAction(ActionType.UPDATE);
			changeRequestHistoryMapper.insertChangeRequestHistory(changeRequestHistory);

			List<ChangeRequestHistoryItem> historyItems = new ArrayList<ChangeRequestHistoryItem>();
			for (TrackingItem trackingItem:trackingItems){
				ChangeRequestHistoryItem historyItem = new ChangeRequestHistoryItem();
				historyItem.setChangeRequestHistoryId(changeRequestHistory.getChangeRequestHistoryId());
				historyItem.setLabelCode(trackingItem.getLabel());
				historyItem.setLabelDescOverride(trackingItem.getLabelDescOverride());
				if (trackingItem.getReferenceTable()==null){
					historyItem.setItem(trackingItem.getValue());
				}else{  // need get value from reference table
					historyItem.setItem(getItemValueFromReferenceTable(trackingItem));
				}
				historyItems.add(historyItem);
			}
			changeRequestHistory.setHistoryItems(historyItems);
			for (ChangeRequestHistoryItem historyItem: historyItems){
				changeRequestHistoryMapper.insertChangeRequestHistoryItem(historyItem);
			}
		}
	}







	@Override
	@Async
	public void createChangeRequestHistoryForCreating  (ChangeRequestDTO newChangeRequest,User currentUser){


		ChangeRequestHistory changeRequestHistory = new ChangeRequestHistory();
		changeRequestHistory.setChangeRequestId(newChangeRequest.getChangeRequestId());
		changeRequestHistory.setModifiedByUser(currentUser.getUsername());
		changeRequestHistory.setAction(ActionType.CREATE);
		changeRequestHistoryMapper.insertChangeRequestHistory(changeRequestHistory);

		ChangeRequestDTO changeRequestInTable =changeRequestMapper.findCourseGrainedChangeRequestById(newChangeRequest.getChangeRequestId());
		List<TrackingItem> trackingItems = new ArrayList<TrackingItem>();
		trackingItems = changeRequestInTable.getTrackingItemsForCreating(trackingItems);
		List<ChangeRequestHistoryItem> historyItems = new ArrayList<ChangeRequestHistoryItem>();
		for (TrackingItem trackingItem:trackingItems){
			ChangeRequestHistoryItem historyItem = new ChangeRequestHistoryItem();
			historyItem.setChangeRequestHistoryId(changeRequestHistory.getChangeRequestHistoryId());
			historyItem.setLabelCode(trackingItem.getLabel());

			if (trackingItem.getReferenceTable()==null){
				historyItem.setItem(trackingItem.getValue());
			}else{  // need get value from reference table
				historyItem.setItem(getItemValueFromReferenceTable(trackingItem));
			}
			historyItems.add(historyItem);
		}
		changeRequestHistory.setHistoryItems(historyItems);

		for (ChangeRequestHistoryItem historyItem: historyItems){
			changeRequestHistoryMapper.insertChangeRequestHistoryItem(historyItem);
		}

	}


	@Override
	public List<ChangeRequestHistory> findChangeRequestHistoryByChangeRequestId (Long changeRequestId){
		return changeRequestHistoryMapper.findChangeRequestHistoryByChangeRequestId(changeRequestId);
	}





	private String getItemValueFromReferenceTable (TrackingItem trackingItem){
		String referenceId = trackingItem.getValue();   // code or Id
		String itemValue = null;
		if (ReferenceTable.CHANGE_REQUEST_LANGUAGE == trackingItem.getReferenceTable()){   // get value from table Change_Request_Language
			itemValue = lookupMapper.findChangeRequestLanguageDescByCode(referenceId);
		}

		if (ReferenceTable.AUX_TABLE_VALUE == trackingItem.getReferenceTable()){   // get value from table AUX_TABLE_VALUE
			itemValue = lookupMapper.findAuxTableValueById(Long.valueOf(referenceId));
		}
		if (ReferenceTable.USER_PROFILE ==trackingItem.getReferenceTable()){   // get value from table USER_PROFILE
			itemValue = lookupMapper.findUserNameByUserId(Long.valueOf(referenceId));
		}
		if (ReferenceTable.DISTRIBUTION_LIST ==trackingItem.getReferenceTable()){   // get value from table DISTRIBUTION_LIST
			itemValue = lookupMapper.findDistributionNameById(Long.valueOf(referenceId));
		}

		return itemValue;
	}




	public List<TrackingItem> tellDifferences(ChangeRequestDTO newChangeRequest, ChangeRequestDTO oldChangeRequest,List<TrackingItem> trackingItems){



		return null;
	}

	public ChangeRequestMapper getChangeRequestMapper() {
		return changeRequestMapper;
	}


	public void setChangeRequestMapper(ChangeRequestMapper changeRequestMapper) {
		this.changeRequestMapper = changeRequestMapper;
	}


	public LookupMapper getLookupMapper() {
		return lookupMapper;
	}


	public void setLookupMapper(LookupMapper lookupMapper) {
		this.lookupMapper = lookupMapper;
	}


	public ChangeRequestHistoryMapper getChangeRequestHistoryMapper() {
		return changeRequestHistoryMapper;
	}


	public void setChangeRequestHistoryMapper(ChangeRequestHistoryMapper changeRequestHistoryMapper) {
		this.changeRequestHistoryMapper = changeRequestHistoryMapper;
	}


}
