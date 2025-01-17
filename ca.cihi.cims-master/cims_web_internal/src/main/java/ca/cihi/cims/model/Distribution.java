package ca.cihi.cims.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * @author szhang
 */
public class Distribution implements Serializable, Comparable<Distribution> {
	private static final long serialVersionUID = -4808362978980768066L;

	public static final Long DL_ID_ENContentDeveloper = 1L;
	public static final Long DL_ID_FRContentDeveloper = 2L;
	public static final Long DL_ID_Reviewer = 3L;
	public static final Long DL_ID_Initiator = 4L;
	public static final Long DL_ID_ADMINISTRATOR = 5L;
	public static final Long DL_ID_ReleaseOperator = 6L;
	public static final Long DL_ID_PreliminaryRelease = 7L;
	public static final Long DL_ID_OfficialRelease = 8L;
	public static final Long DL_ID_Classification = 9L;
	public static final Long DL_ID_InternalRelease = 19L;
	public static final Long DL_ID_RefsetDeveloper = 21L;

	private Long distributionlistid;
	private String code;
	private String name;
	private String description;
	private String status;
	private String reviewgroup;
	private Date createdDate;

	@Override
	public int compareTo(Distribution other) {
		return new CompareToBuilder().append(getCode(), other.getCode())
				.append(getDistributionlistid(), other.getDistributionlistid()).toComparison();
	}

	public String getCode() {
		return code;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getDescription() {
		return description;
	}

	public Long getDistributionlistid() {
		return distributionlistid;
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

	public void setCode(String code) {
		this.code = code;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDistributionlistid(Long distributionlistid) {
		this.distributionlistid = distributionlistid;
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
}
