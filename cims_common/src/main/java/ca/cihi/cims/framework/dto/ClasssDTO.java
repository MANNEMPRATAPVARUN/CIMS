package ca.cihi.cims.framework.dto;

import java.io.Serializable;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:18:42 AM
 */
public class ClasssDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5204612817798709819L;

	private String baseClassificationName;

	private Long classsId;
	private String classsName;
	private String friendlyName;
	private String tableName;

	public ClasssDTO() {

	}

	public ClasssDTO(String tableName, String baseClassificationName, String classsName, String friendlyName) {
		setTableName(tableName);
		setBaseClassificationName(baseClassificationName);
		setClasssName(classsName);
		setFriendlyName(friendlyName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClasssDTO other = (ClasssDTO) obj;
		if (classsId == null) {
			if (other.classsId != null) {
				return false;
			}
		} else if (!classsId.equals(other.classsId)) {
			return false;
		}
		return true;
	}

	public String getBaseClassificationName() {
		return baseClassificationName;
	}

	public Long getClasssId() {
		return classsId;
	}

	public String getClasssName() {
		return classsName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((classsId == null) ? 0 : classsId.hashCode());
		return result;
	}

	public void setBaseClassificationName(String baseClassificationName) {
		this.baseClassificationName = baseClassificationName;
	}

	public void setClasssId(Long classsId) {
		this.classsId = classsId;
	}

	public void setClasssName(String classsName) {
		this.classsName = classsName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}