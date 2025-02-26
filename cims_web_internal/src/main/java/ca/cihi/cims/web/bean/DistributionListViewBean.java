package ca.cihi.cims.web.bean;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.validator.Status;

/**
 * @author szhang
 */
public class DistributionListViewBean extends BaseSerializableCloneableObject {
	private static final long serialVersionUID = 2287702285708569368L;

	private List<Distribution> distributionList;
	private String actionType;

	private String distribution_id;

	@NotNull()
	@NumberFormat(style = Style.NUMBER)
	@Size(min = 3, max = 3, message = "Please enter missing distribution list code")
	@Pattern(regexp = "[0-9][0-9][0-9]", message = "Please enter missing distribution list code")
	private String code;

	@NotNull()
	@Size(min = 1, max = 20, message = "Please enter missing distribution list name")
	private String name;

	@Status
	private String status;

	private String reviewgroup;

	private String description;

	private List<User> users;

	public void fromDistribution(final Distribution distribution) {
		this.distribution_id = distribution.getDistributionlistid().toString();
		this.code = distribution.getCode();
		this.name = distribution.getName();
		this.description = distribution.getDescription();
		this.status = distribution.getStatus();
		this.reviewgroup = distribution.getReviewgroup();
	}

	public String getActionType() {
		return actionType;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getDistribution_id() {
		return distribution_id;
	}

	public List<Distribution> getDistributionList() {
		return distributionList;
	}

	public String getName() {
		return name;
	}

	public String getReviewgroup() {
		return reviewgroup;
	}

	public String getStatus() {
		return status;
	}

	public List<User> getUsers() {
		return users;
	}

	public void reset() {
		this.distribution_id = null;
		this.code = null;
		this.name = null;
		this.description = null;
		this.status = null;
		this.reviewgroup = null;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDistribution_id(String distribution_id) {
		this.distribution_id = distribution_id;
	}

	public void setDistributionList(List<Distribution> distributionList) {
		this.distributionList = distributionList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReviewgroup(String reviewgroup) {
		this.reviewgroup = reviewgroup;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Distribution toDistribution() {
		Distribution distribution = new Distribution();
		distribution.setDistributionlistid(Long.valueOf(this.distribution_id));
		distribution.setCode(code);
		distribution.setDescription(description);
		distribution.setName(name);
		distribution.setStatus(status);
		distribution.setReviewgroup(reviewgroup);
		distribution.setCreatedDate(new Date());
		return distribution;
	}

}
