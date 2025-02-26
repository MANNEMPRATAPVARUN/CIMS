package ca.cihi.cims.web.bean.tabular;

import java.util.Map;

import ca.cihi.cims.model.tabular.TabularConceptModel;

public class TabularAddBean extends TabularBasicInfoBean {

	private Map<Long, String> cciGroups;
	private Map<Long, String> cciTissues;
	private Map<Long, String> cciDevices;
	private Map<Long, String> cciInterventions;
	private Map<Long, String> cciTechniques;

	private Long cciGroup;
	private Long cciTissue;
	private Long cciDevice;
	private Long cciTechnique;
	private Long cciIntervention;

	// for CCI_RUBRIC only
	private String cciGroupName;

	// for CCI only
	private Long parentElementId;
	private boolean parentRoot;

	// -----------------------------------------------------------

	public TabularAddBean() {
	}

	public TabularAddBean(TabularConceptModel concept) {
		super(concept);
	}

	public Long getCciDevice() {
		return cciDevice;
	}

	public Map<Long, String> getCciDevices() {
		return cciDevices;
	}

	public Long getCciGroup() {
		return cciGroup;
	}

	public String getCciGroupName() {
		return cciGroupName;
	}

	public Map<Long, String> getCciGroups() {
		return cciGroups;
	}

	public Long getCciIntervention() {
		return cciIntervention;
	}

	public Map<Long, String> getCciInterventions() {
		return cciInterventions;
	}

	public Long getCciTechnique() {
		return cciTechnique;
	}

	public Map<Long, String> getCciTechniques() {
		return cciTechniques;
	}

	public Long getCciTissue() {
		return cciTissue;
	}

	public Map<Long, String> getCciTissues() {
		return cciTissues;
	}

	public Long getParentElementId() {
		return parentElementId;
	}

	public boolean isCciDevicesVisible() {
		return cciDevices != null;
	}

	public boolean isCciGroupsVisible() {
		return cciGroups != null;
	}

	public boolean isCciInterventionsVisible() {
		return cciInterventions != null;
	}

	public boolean isCciTechniquesVisible() {
		return cciTechniques != null;
	}

	public boolean isCciTissuesVisible() {
		return cciTissues != null;
	}

	public boolean isParentRoot() {
		return parentRoot;
	}

	public void setCciDevice(Long cciDevice) {
		this.cciDevice = cciDevice;
	}

	public void setCciDevices(Map<Long, String> cciDevices) {
		this.cciDevices = cciDevices;

	}

	public void setCciGroup(Long cciGroup) {
		this.cciGroup = cciGroup;
	}

	public void setCciGroupName(String cciGroupName) {
		this.cciGroupName = cciGroupName;
	}

	public void setCciGroups(Map<Long, String> cciGroups) {
		this.cciGroups = cciGroups;
	}

	public void setCciIntervention(Long cciIntervention) {
		this.cciIntervention = cciIntervention;
	}

	public void setCciInterventions(Map<Long, String> cciInterventions) {
		this.cciInterventions = cciInterventions;

	}

	public void setCciTechnique(Long cciTechnique) {
		this.cciTechnique = cciTechnique;
	}

	public void setCciTechniques(Map<Long, String> cciTechniques) {
		this.cciTechniques = cciTechniques;
	}

	public void setCciTissue(Long cciTissue) {
		this.cciTissue = cciTissue;
	}

	public void setCciTissues(Map<Long, String> cciTissues) {
		this.cciTissues = cciTissues;
	}

	public void setParentElementId(Long parentElementId) {
		this.parentElementId = parentElementId;
	}

	public void setParentRoot(boolean parentRoot) {
		this.parentRoot = parentRoot;
	}

}
