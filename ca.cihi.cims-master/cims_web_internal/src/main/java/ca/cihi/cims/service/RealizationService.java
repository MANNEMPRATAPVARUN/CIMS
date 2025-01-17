package ca.cihi.cims.service;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;

public interface RealizationService {

	/*
	 * create change_request_realization with status begins
	 * do Step 1: synchronize view
	 * Step 2: check incompletes,
	 * Step 3: check conflicts,
	 * Step 4: realizing,  step 4 is realizeChangeRequest, need to be synchronized
	 * 
	 */
	ChangeRequestRealization processRealizingChangeRequest(ChangeRequest changeRequest, User currentUser);

}
