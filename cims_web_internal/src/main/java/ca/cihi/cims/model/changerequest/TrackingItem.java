package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

import ca.cihi.cims.model.ReferenceTable;
/*
 * this class is designed for tracking change request changes
 * which label get changed, the new value, if referenceTable is not null, need get the
 * value from table
 * 
 */
public class TrackingItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LabelType label;
	private String labelDescOverride;
	private String value;
	private ReferenceTable referenceTable;


	public TrackingItem(){
	}
	public TrackingItem(LabelType label, String value, ReferenceTable referenceTable){
		this.label = label;
		this.value =value;
		this.referenceTable = referenceTable;
	}

	public TrackingItem(LabelType label,String labelDescOverride ,String value, ReferenceTable referenceTable){
		this.label = label;
		this.labelDescOverride = labelDescOverride;
		this.value =value;
		this.referenceTable = referenceTable;
	}

	public TrackingItem(LabelType label,String labelDescOverride, String value){
		this.label = label;
		this.labelDescOverride = labelDescOverride;
		this.value =value;
	}

	public TrackingItem(LabelType label, String value){
		this.label = label;
		this.value =value;
	}


	public LabelType getLabel() {
		return label;
	}
	public void setLabel(LabelType label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public ReferenceTable getReferenceTable() {
		return referenceTable;
	}
	public void setReferenceTable(ReferenceTable referenceTable) {
		this.referenceTable = referenceTable;
	}
	public String getLabelDescOverride() {
		return labelDescOverride;
	}
	public void setLabelDescOverride(String labelDescOverride) {
		this.labelDescOverride = labelDescOverride;
	}


}
