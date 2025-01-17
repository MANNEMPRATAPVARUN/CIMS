package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.IndexBookType;

public class ConceptModification implements Serializable {

	private static final long serialVersionUID = -2680984671584638738L;

	private Long structureId;
	private Long elementId;
	private String code;
	private String indexTerm;
	private String indexPath;
	private Long validationId;
	private String conceptClassName;
	private String breadCrumbs;
	private List<ProposedChange> proposedTabularChanges;
	private List<RealizedChange> realizedTabularChanges;
	private List<ValidationChange> proposedValidationChanges;
	private List<ValidationChange> realizedValidationChanges;
	private HashMap<String, RealizedChange> rawRealizedValidationChanges;
	private List<ProposedChange> proposedAndConflictTabularChanges;
	private List<ProposedChange> proposedAndConflictValidationChanges;
	private List<ProposedChange> proposedIndexChanges;
	private List<ProposedChange> proposedAndConflictIndexChanges;
	private List<ProposedChange> proposedAndConflictSupplementChanges;
	private List<RealizedChange> realizedIndexChanges;
	private ProposedChange proposedIndexRefChange;
	private RealizedChange realizedIndexRefChange;
	private List<ProposedChange> proposedSupplementChanges;
	private List<RealizedChange> realizedSupplementChanges;
	private Long changeRequestId;
	private String versionCode;

	private void addRealizedValidationChanges(final HashMap<String, RealizedChange> rawRealizedValChanges,
			final List<RealizedChange> aRawRealizedValChanges) {
		for (RealizedChange rChange : aRawRealizedValChanges) {
			// filter out the validation status change on adding/activating
			if (!ConceptStatus.ACTIVE.toString().equals(rChange.getNewValue())) {
				String dataHolding = rChange.getFieldName();
				// Combine multiple records for each validation rule
				if (rawRealizedValChanges.containsKey(dataHolding)) {
					RealizedChange existingChange = rawRealizedValChanges.get(dataHolding);
					if (ConceptStatus.DISABLED.toString().equalsIgnoreCase(existingChange.getNewValue())
							|| existingChange.getNewValue().equals(rChange.getOldValue())) {
						existingChange.setNewValue(rChange.getNewValue());
						existingChange.setTableName(rChange.getTableName());
					}
				} else {
					rawRealizedValChanges.put(dataHolding, rChange);
				}
			}
		}
	}

	public String getBreadCrumbs() {
		return breadCrumbs;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public String getCode() {
		return code;
	}

	public String getConceptClassName() {
		return conceptClassName;
	}

	public Long getElementId() {
		return elementId;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public String getIndexTerm() {
		return indexTerm;
	}

	public List<ProposedChange> getProposedAndConflictIndexChanges() {
		return proposedAndConflictIndexChanges;
	}

	public List<ProposedChange> getProposedAndConflictSupplementChanges() {
		return proposedAndConflictSupplementChanges;
	}

	public List<ProposedChange> getProposedAndConflictTabularChanges() {
		return proposedAndConflictTabularChanges;
	}

	public List<ProposedChange> getProposedAndConflictValidationChanges() {
		return proposedAndConflictValidationChanges;
	}

	public List<ProposedChange> getProposedIndexChanges() {
		return proposedIndexChanges;
	}

	public ProposedChange getProposedIndexRefChange() {
		return proposedIndexRefChange;
	}

	public List<ProposedChange> getProposedSupplementChanges() {
		return proposedSupplementChanges;
	}

	public List<ProposedChange> getProposedTabularChanges() {
		return proposedTabularChanges;
	}

	public List<ValidationChange> getProposedValidationChanges() {
		return proposedValidationChanges;
	}

	public HashMap<String, RealizedChange> getRawRealizedValidationChanges() {
		return rawRealizedValidationChanges;
	}

	public List<RealizedChange> getRealizedIndexChanges() {
		return realizedIndexChanges;
	}

	public RealizedChange getRealizedIndexRefChange() {
		return realizedIndexRefChange;
	}

	public List<RealizedChange> getRealizedSupplementChanges() {
		return realizedSupplementChanges;
	}

	public List<RealizedChange> getRealizedTabularChanges() {
		return realizedTabularChanges;
	}

	public List<ValidationChange> getRealizedValidationChanges() {
		return realizedValidationChanges;
	}

	public Long getStructureId() {
		return structureId;
	}

	public Long getValidationId() {
		return validationId;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setBreadCrumbs(String breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setCode(String code) {
		// //Replace single quota with &rsquo;
		String aCode = code;
		if (aCode != null) {
			aCode = aCode.replaceAll("'", "&rsquo;");
		}
		this.code = aCode;
	}

	public void setConceptClassName(String conceptClassName) {
		this.conceptClassName = conceptClassName;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public void setIndexTerm(String indexTerm) {
		this.indexTerm = indexTerm;
	}

	public void setProposedAndConflictIndexChanges(final List<ProposedChange> rawProposedIndexChanges) {

		if (IndexBookType.A.getCode().equals(conceptClassName) || IndexBookType.E.getCode().equals(conceptClassName)
				|| IndexBookType.CCI_A.getCode().equals(conceptClassName)) {
			this.proposedAndConflictIndexChanges = rawProposedIndexChanges;
		} else {
			for (ProposedChange proposedChange : rawProposedIndexChanges) {
				if ("HTMLPropertyVersion".equalsIgnoreCase(proposedChange.getTableName())) {
					if (proposedIndexRefChange == null) {
						proposedIndexRefChange = proposedChange;
					} else {
						proposedChange.setOldValue(proposedIndexRefChange.getOldValue());
						proposedIndexRefChange = proposedChange;
					}
				} else {
					proposedAndConflictIndexChanges = proposedAndConflictIndexChanges == null ? new ArrayList<ProposedChange>()
							: proposedAndConflictIndexChanges;
					proposedAndConflictIndexChanges.add(proposedChange);
				}
			}
		}
	}

	public void setProposedAndConflictSupplementChanges(List<ProposedChange> proposedAndConflictSupplementChanges) {
		this.proposedAndConflictSupplementChanges = proposedAndConflictSupplementChanges;
	}

	public void setProposedAndConflictTabularChanges(List<ProposedChange> proposedAndConflictTabularChanges) {
		this.proposedAndConflictTabularChanges = proposedAndConflictTabularChanges;
	}

	public void setProposedAndConflictValidationChanges(List<ProposedChange> proposedAndConflictValidationChanges) {
		if (this.proposedAndConflictValidationChanges == null) {
			this.proposedAndConflictValidationChanges = proposedAndConflictValidationChanges;
		} else {
			this.proposedAndConflictValidationChanges.addAll(proposedAndConflictValidationChanges);
		}
	}

	public void setProposedIndexChanges(final List<ProposedChange> rawProposedIndexChanges) {

		if (IndexBookType.A.getCode().equals(conceptClassName) || IndexBookType.E.getCode().equals(conceptClassName)
				|| IndexBookType.CCI_A.getCode().equals(conceptClassName)) {
			this.proposedIndexChanges = rawProposedIndexChanges;
		} else {
			for (ProposedChange proposedChange : rawProposedIndexChanges) {
				if ("HTMLPropertyVersion".equalsIgnoreCase(proposedChange.getTableName())) {
					if (proposedIndexRefChange == null) {
						proposedIndexRefChange = proposedChange;
					} else {
						proposedChange.setOldValue(proposedIndexRefChange.getOldValue());
						proposedIndexRefChange = proposedChange;
					}
				} else {
					proposedIndexChanges = proposedIndexChanges == null ? new ArrayList<ProposedChange>()
							: proposedIndexChanges;
					proposedIndexChanges.add(proposedChange);
				}
			}
		}
	}

	public void setProposedIndexRefChange(final ProposedChange proposedIndexRefChange) {
		this.proposedIndexRefChange = proposedIndexRefChange;
	}

	public void setProposedSupplementChanges(List<ProposedChange> proposedSupplementChanges) {
		this.proposedSupplementChanges = proposedSupplementChanges;
	}

	public void setProposedTabularChanges(final List<ProposedChange> proposedTabularChanges) {
		this.proposedTabularChanges = proposedTabularChanges;
	}

	public void setProposedValidationChanges(final List<ValidationChange> proposedValidationChanges) {
		if (this.proposedValidationChanges == null) {
			this.proposedValidationChanges = proposedValidationChanges;
		} else {
			this.proposedValidationChanges.addAll(proposedValidationChanges);
		}
	}

	public void setRawRealizedValidationChanges(final List<RealizedChange> aRawRealizedValChanges) {
		if (this.rawRealizedValidationChanges == null) {
			rawRealizedValidationChanges = new LinkedHashMap<String, RealizedChange>();
			addRealizedValidationChanges(rawRealizedValidationChanges, aRawRealizedValChanges);
		} else {
			addRealizedValidationChanges(rawRealizedValidationChanges, aRawRealizedValChanges);
		}
	}

	public void setRealizedIndexChanges(List<RealizedChange> rawRealizedIndexChanges) {
		if (IndexBookType.A.getCode().equals(conceptClassName) || IndexBookType.E.getCode().equals(conceptClassName)
				|| IndexBookType.CCI_A.getCode().equals(conceptClassName)) {
			this.realizedIndexChanges = rawRealizedIndexChanges;
		} else {
			for (RealizedChange realizedChange : rawRealizedIndexChanges) {
				if ("HTMLPropertyVersion".equalsIgnoreCase(realizedChange.getTableName())) {
					if (realizedIndexRefChange == null) {
						realizedIndexRefChange = realizedChange;
					} else {
						realizedChange.setOldValue(realizedIndexRefChange.getOldValue());
						realizedIndexRefChange = realizedChange;
					}
				} else {
					realizedIndexChanges = realizedIndexChanges == null ? new ArrayList<RealizedChange>()
							: realizedIndexChanges;
					realizedIndexChanges.add(realizedChange);
				}
			}
		}
	}

	public void setRealizedIndexRefChange(RealizedChange realizedIndexRefChange) {
		this.realizedIndexRefChange = realizedIndexRefChange;
	}

	public void setRealizedSupplementChanges(List<RealizedChange> realizedSupplementChanges) {
		this.realizedSupplementChanges = realizedSupplementChanges;
	}

	public void setRealizedTabularChanges(final List<RealizedChange> realizedTabularChanges) {
		this.realizedTabularChanges = realizedTabularChanges;
	}

	public void setRealizedValidationChanges(final List<ValidationChange> aRealizedValidationChanges) {
		this.realizedValidationChanges = aRealizedValidationChanges;
	}

	public void setStructureId(final Long structureId) {
		this.structureId = structureId;
	}

	public void setValidationId(final Long validationId) {
		this.validationId = validationId;
	}

	public void setVersionCode(final String versionCode) {
		this.versionCode = versionCode;
	}

}
