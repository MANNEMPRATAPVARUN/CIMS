package ca.cihi.cims.model;

public class UserSearchCriteria extends SearchCriteria {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userId;
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
