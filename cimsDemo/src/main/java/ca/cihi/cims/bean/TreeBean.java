package ca.cihi.cims.bean;

import java.util.ArrayList;
import java.util.List;


public class TreeBean {
	private String title;
	private boolean isFolder;
	private String key;
	private boolean expand;
	private boolean isLazy;
	private List<TreeBean> children=new ArrayList<TreeBean>();	
	
	private String desc;
	private int level=0;
	

	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}


	
	public TreeBean(){
		this.key = "node_"+Utils.getNextId();
	}
	public TreeBean(String title){
		this();
		this.title = title;
		this.desc = "This is description of " + this.title;
	}
	public void addChild(TreeBean child){
		this.children.add(child);
	}

	public List<TreeBean> getChildren() {
		return children;
	}
	public String getDesc() {
		return desc;
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
	public void setChildren(List<TreeBean> children) {
		this.children = children;
	}


	public void setDesc(String desc) {
		this.desc = desc;
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
		//this.title += "(lazy)";
	}


	public void setTitle(String title) {
		this.title = title;
	}
}
