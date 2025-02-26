package ca.cihi.cims.service;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;

public interface ChangeRequestAccessService {

	ChangeRequestPermission getChangeRequestClassificationPermission(User user, long changeRequestId,
			ChangeRequestCategory category);

	ChangeRequestPermission getChangeRequestPermission(User user, long changeRequest);

}
