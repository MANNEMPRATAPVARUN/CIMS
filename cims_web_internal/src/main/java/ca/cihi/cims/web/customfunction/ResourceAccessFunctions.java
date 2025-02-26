package ca.cihi.cims.web.customfunction;

import java.util.List;

import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.resourceaccess.AccessCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceCode;

public final class ResourceAccessFunctions {
	private ResourceAccessFunctions() {}

	public static boolean hasReadAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasReadAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
	}

	public static boolean hasReadAccess(User currentUser, ResourceCode resourceCode) {
		return hasReadAccessToResource(currentUser.getResourceAccesses(), resourceCode);
	}

	public static boolean hasWriteAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasWriteAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
		//return true;
	}

	public static boolean hasWriteAccess(User currentUser, ResourceCode resourceCode) {
		return hasWriteAccessToResource(currentUser.getResourceAccesses(), resourceCode);
	}

	public static boolean hasExecuteAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasExecuteAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
	}

	public static boolean hasExecuteAccess(User currentUser, ResourceCode resourceCode) {
		return hasExecuteAccessToResource(currentUser.getResourceAccesses(), resourceCode);
	}

	/*
	 * 
	 */
	public static boolean isUserAdviceRecipient(User currentUser, Advice advice) {
		boolean isRecipient = false;
		if (advice.getUserProfileId()!=null){  // the recipient is user
			if (currentUser.getUserId().longValue() == advice.getUserProfileId().longValue()){
				isRecipient = true;
			}
		}else{  // the recipient is group, and current user is not sender
			if (currentUser.getUserId().longValue() != advice.getSenderId().longValue()){
				List<Distribution> currentUserInGroups  = currentUser.getInGroups();
				for (Distribution group:currentUserInGroups){
					if (group.getDistributionlistid().longValue() ==advice.getDistributionListId().longValue()){
						isRecipient = true;
						break;
					}
				}
			}
		}
		return isRecipient;
	}

	public static boolean isUserQuestionRecipient(User currentUser, QuestionForReviewer question) {
		boolean isRecipient = false;
		if (question.getReviewerId()!=null){
			List<Distribution> currentUserInGroups  = currentUser.getInGroups();
			if (currentUserInGroups != null && currentUserInGroups.size()>0){
				for (Distribution group:currentUserInGroups){
					if (group.getDistributionlistid().longValue() ==question.getReviewerId().longValue()){
						isRecipient = true;
						break;
					}
				}
			}
		}

		return isRecipient;
	}

	public static boolean isUserInGroup(User currentUser, Long groupdId) {
		boolean isInGroup = false;
		List<Distribution> currentUserInGroups  = currentUser.getInGroups();
		if (currentUserInGroups != null && currentUserInGroups.size()>0){
			for (Distribution group:currentUserInGroups){
				if (group.getDistributionlistid().longValue() ==groupdId.longValue()){
					isInGroup = true;
					break;
				}
			}
		}
		return  isInGroup;
	}





	private static boolean hasReadAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasReadAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode && (resourceAccess.getAccessCode() == AccessCode.READ ||resourceAccess.getAccessCode()==AccessCode.WRITE) ) {
				hasReadAccess = true;
				break;
			}
		}
		return hasReadAccess;
	}


	private static boolean hasWriteAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasWriteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode && resourceAccess.getAccessCode() == AccessCode.WRITE) {
				hasWriteAccess = true;
				break;
			}
		}

		return hasWriteAccess;
	}

	private static boolean hasExecuteAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasExecuteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode
					&& resourceAccess.getAccessCode() == AccessCode.EXECUTE) {
				hasExecuteAccess = true;
				break;
			}
		}

		return hasExecuteAccess;
	}


}
