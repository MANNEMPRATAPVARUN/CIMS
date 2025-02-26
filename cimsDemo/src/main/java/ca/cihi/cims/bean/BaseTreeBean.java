package ca.cihi.cims.bean;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public abstract class BaseTreeBean extends BaseSerializableCloneableObject {
	
	private static final long serialVersionUID = -1014360342707842426L;
	private String key;
	private String title;	
	private boolean expand;
	private boolean isFolder;
	private boolean isLazy;

	private String desc;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public BaseTreeBean() {
	}

	public BaseTreeBean(String title) {
		this();
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public boolean isExpand() {
		return expand;
	}

	public boolean getIsFolder() {
		return isFolder;
	}

	public boolean getIsLazy() {
		return isLazy;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
		// this.title += "(lazy)";
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
