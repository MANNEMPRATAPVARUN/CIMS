package ca.cihi.cims.data.mapper;

import java.util.List;

import ca.cihi.cims.model.changerequest.ChangeRequestHistory;
import ca.cihi.cims.model.changerequest.ChangeRequestHistoryItem;

public interface ChangeRequestHistoryMapper {
	void insertChangeRequestHistory(ChangeRequestHistory  changeRequestHistory);

	//void insertChangeRequestHistoryItems(ChangeRequestHistory  changeRequestHistory);
	void insertChangeRequestHistoryItem(ChangeRequestHistoryItem  changeRequestHistoryItem);


	List<ChangeRequestHistory> findChangeRequestHistoryByChangeRequestId (Long changeRequestId);
}
