package ca.cihi.cims.service;

import static ca.cihi.cims.model.SecurityRole.ROLE_ADMINISTRATOR;
import static ca.cihi.cims.model.SecurityRole.ROLE_ENG_CONTENT_DEVELOPER;
import static ca.cihi.cims.model.SecurityRole.ROLE_FRA_CONTENT_DEVELOPER;
import static ca.cihi.cims.util.CollectionUtils.asSet;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.access.StandardChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;


public class ChangeRequestAccessServiceImpl implements ChangeRequestAccessService {

	private final static Set<SecurityRole> CHANGE_REQUEST_EDITOR_ROLES = asSet(ROLE_ENG_CONTENT_DEVELOPER,
			ROLE_FRA_CONTENT_DEVELOPER, ROLE_ADMINISTRATOR);

	private ChangeRequestService changeRequestService;

	// -------------------------------------------------------------

	@Override
	public ChangeRequestPermission getChangeRequestClassificationPermission(User user, long changeRequestId,
			ChangeRequestCategory category) {
		ChangeRequest request = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		if (request == null) {
			throw new CIMSException("Change request not found: " + changeRequestId);
		} else {
			if (hasOneOfRoles(user, CHANGE_REQUEST_EDITOR_ROLES)) {
				if (!ObjectUtils.equals(request.getAssigneeUserId(), user.getUserId())) {
					return StandardChangeRequestPermission.READ;
				} else {
					if (!ChangeRequestStatus.VALID_STATUSES.contains(request.getStatus())) {
						return StandardChangeRequestPermission.READ;
					} else {
						if (category != null && request.getCategory() != category) {
							return StandardChangeRequestPermission.READ;
						} else {
							if (user.getRoles().contains(ROLE_ADMINISTRATOR)
									|| request.getLanguages().containsAll(Language.ALL)) {
								return StandardChangeRequestPermission.WRITE_ALL;
							} else {
								if (user.getRoles().contains(ROLE_ENG_CONTENT_DEVELOPER)) {
									return StandardChangeRequestPermission.WRITE_ENGLISH;
								} else {
									return StandardChangeRequestPermission.WRITE_ALL;
								}
							}
						}
					}
				}
			} else {
				return StandardChangeRequestPermission.READ;
			}
		}
	}

	@Override
	public ChangeRequestPermission getChangeRequestPermission(User user, long changeRequestId) {
		return getChangeRequestClassificationPermission(user, changeRequestId, null);
	}

	private boolean hasOneOfRoles(User user, Set<SecurityRole> roles) {
		return !Collections.disjoint(user.getRoles(), roles);
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

}
