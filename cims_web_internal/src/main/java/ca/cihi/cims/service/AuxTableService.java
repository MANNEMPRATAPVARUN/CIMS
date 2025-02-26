package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciAgentGroup;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.content.shared.DomainEnum;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;

@Component
public class AuxTableService {

	private enum ClassificationTable {
		AgentGroup, InvasivenessLevel;

		@SuppressWarnings("unchecked")
		public <T extends DomainEnum> Class<T> getDomainClass() {
			switch (this) {
			case AgentGroup:
				return (Class<T>) CciAgentGroup.class;
			case InvasivenessLevel:
				return (Class<T>) CciInvasivenessLevel.class;
			default:
				throw new RuntimeException("Unrecognized class: " + this);
			}
		}
	}

	// ----------------------------------------------------------------

	private static final String CCI = "CCI";

	@Autowired
	private AdminService adminService;
	@Autowired
	private ContextProvider contextProvider;
	@Autowired
	private NonContextOperations nonContextOperations;
	@Autowired
	private ContextOperations contextOperations;

	// ----------------------------------------------------------------

	private void commitContextAccess(ContextAccess context) {
		context.persist();
		context.realizeChangeContext(true);
	}

	@Transactional
	public void deleteAux(AuxTableValue value) throws AlreadyInUseException {
		Long elementId = Long.valueOf(value.getAuxTableValueId());
		if (value.isClassification()) {
			if (isReadonlyYear(value.getYear())) {
				throw new CIMSException("Year is closed");
			}
			if (contextOperations.hasConceptBeenPublished(elementId)) {
				throw new CIMSException("The item cannot be removed when it has been published");
			}
			ContextAccess context = getContextAccess(value.getYear());
			nonContextOperations.remove(context.getContextId(), elementId);
			commitContextAccess(context);
		} else {
			adminService.deleteAux(elementId);
		}
	}

	public List<Long> getCciVersionCodes() {
		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findBaseClassificationVersionYearVersionCodes(CCI);
		List<Long> versionCodes = new ArrayList<Long>();
		for (ContextIdentifier context : contextIdentifiers) {
			versionCodes.add(Long.parseLong(context.getVersionCode()));
		}
		Collections.sort(versionCodes);
		return versionCodes;
	}

	public List<String> getChangeRequestTableCodes() {
		return adminService.getAuxTableCodes();
	}

	public long getChangeRequestTableIdByCode(String auxCode) {
		return Long.parseLong(adminService.getAuxTableIdByCode(auxCode));
	}	

	public List<AuxTableValue> getChangeRequestTableValues(String auxCode) {
		return adminService.getAuxTableValues(auxCode);
	}

	public Map<String, String> getClassificationTableCodes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("AgentGroup", "Agent Group");
		map.put("InvasivenessLevel", "Invasiveness Level");
		return map;
	}

	public long getClassificationTableIdByCode(String auxCode) {
		return ClassificationTable.valueOf(auxCode).ordinal();
	}

	public List<AuxTableValue> getClassificationTableValues(String auxCode, long year) {
		ClassificationTable table = ClassificationTable.valueOf(auxCode);
		ContextDefinition cd = ContextDefinition.forVersion(CCI, year + "");
		ContextAccess context = contextProvider.findContext(cd);
		return getValues(table, context, table.getDomainClass());
	}

	private ContextAccess getContextAccess(int year) {
		ContextDefinition cd = ContextDefinition.forVersion(CCI, year + "");
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);
		return context;
	}

	private <T extends DomainEnum> List<AuxTableValue> getValues(ClassificationTable table, ContextAccess context,
			Class<T> clazz) {
		List<AuxTableValue> list = new ArrayList<AuxTableValue>();
		Iterator<T> agentGroups = context.findAll(clazz);
		while (agentGroups.hasNext()) {
			list.add(toAuxTableValue(table, agentGroups.next()));
		}
		return list;
	}

	@Transactional
	public long insertAuxTableValue(AuxTableValue value) {
		if (value.isClassification()) {
			if (isReadonlyYear(value.getYear())) {
				throw new CIMSException("Year is closed");
			}
			ClassificationTable table = ClassificationTable.values()[(int) value.getAuxTableId().longValue()];
			ContextAccess context = getContextAccess(value.getYear());
			DomainEnum d = DomainEnum.create(context, table.getDomainClass(), value.getAuxValueCode());
			setAuxValue(value, context, d);
			return d.getElementId();
		} else {
			// String auxTableId = adminService.getAuxTableIdByCode(value.get);
			// viewBean.setAuxTableId(Long.valueOf(auxTableId));
			return adminService.insertAuxTableValue(value);
		}
	}

	public boolean isReadonlyYear(long year) {
		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findBaseClassificationVersionYearVersionCodes(CCI);
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			if (Long.parseLong(contextIdentifier.getVersionCode()) == year) {
				return !contextIdentifier.isContextOpen();
			}
		}
		return false;
	}

	private void setAuxValue(AuxTableValue value, ContextAccess context, DomainEnum d) {
		d.meaning(Language.ENGLISH, value.getAuxEngDesc());
		d.meaning(Language.FRENCH, value.getAuxFraDesc());
		d.description(Language.ENGLISH, value.getAuxEngLable());
		d.description(Language.FRENCH, value.getAuxFraLable());
		d.setStatus(StringUtils.equalsIgnoreCase("A", value.getStatus()) ? "ACTIVE" : "DISABLED");
		commitContextAccess(context);
	}

	private AuxTableValue toAuxTableValue(ClassificationTable table, DomainEnum group) {
		AuxTableValue v = new AuxTableValue();
		v.setAuxTableId((long) table.ordinal());
		v.setAuxTableValueId(group.getElementId());
		v.setAuxValueCode(group.getCode());
		v.setAuxEngDesc(group.meaning(Language.ENGLISH));
		v.setAuxFraDesc(group.meaning(Language.FRENCH));
		v.setAuxEngLable(group.description(Language.ENGLISH));
		v.setAuxFraLable(group.description(Language.FRENCH));
		v.setStatus(StringUtils.equalsIgnoreCase("ACTIVE", group.getStatus()) ? "A" : "D");
		return v;
	}

	@Transactional
	public void updateAuxTableValue(AuxTableValue value) {
		if (value.isClassification()) {
			if (isReadonlyYear(value.getYear())) {
				throw new CIMSException("Year is closed");
			}
			// ClassificationTable table = ClassificationTable.values()[(int) value.getAuxTableId().longValue()];
			ContextAccess context = getContextAccess(value.getYear());
			DomainEnum g = context.load(value.getAuxTableValueId());
			setAuxValue(value, context, g);
		} else {
			adminService.updateAuxTableValue(value);
		}
	}
	
	public boolean isRefsetCodeNotUnique(String auxValueCode){
		return adminService.isRefsetCodeNotUnique(auxValueCode);
	}
	
	public boolean isRefsetNameNotUnique(String auxEngLable){
		return adminService.isRefsetNameNotUnique(auxEngLable);
	}
}
