package ca.cihi.cims.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestHistory;

public interface ChangeRequestHistoryService {
	@Transactional
	void createChangeRequestHistoryForUpdating (ChangeRequestDTO oldChangeRequest , User currentUser);



	@Transactional
	void createChangeRequestHistoryForCreating (ChangeRequestDTO newChangeRequest,User currentUser);

	List<ChangeRequestHistory> findChangeRequestHistoryByChangeRequestId (Long changeRequestId);
}
