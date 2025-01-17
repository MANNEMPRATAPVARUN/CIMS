package ca.cihi.cims.model.snomed;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCTRefsetLang implements SCTBase,Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(SCTRefsetLang.class);	
	
	private String id;
	private Date effectiveTime;
	private String activeIndCode;
	private long moduleId;
	private long refsetId;
	private long referencedComponentId;
	private long acceptabilityId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getActiveIndCode() {
		return activeIndCode;
	}

	public void setActiveIndCode(String activeIndCode) {
		this.activeIndCode = activeIndCode;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getRefsetId() {
		return refsetId;
	}

	public void setRefsetId(long refsetId) {
		this.refsetId = refsetId;
	}

	public long getReferencedComponentId() {
		return referencedComponentId;
	}

	public void setReferencedComponentId(long referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}

	public long getAcceptabilityId() {
		return acceptabilityId;
	}

	public void setAcceptabilityId(long acceptabilityId) {
		this.acceptabilityId = acceptabilityId;
	}
	
	//populate one record based on below format
	//id	effectiveTime	active	moduleId	refsetId	referencedComponentId	acceptabilityId
	public void setValues(String[] values) throws Exception {
		/*
		LOGGER.debug("values[0]="+values[0]+" values[1]="+values[1]+" values[2]="+values[2]+" values[3]="+values[3]+
                " values[4]="+values[4] + "values[5]=" +values[5] + "values[6]=" +values[6]);
        */
		this.setId(values[0]);
		String DATE_FORMAT = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.setEffectiveTime(sdf.parse(values[1]));
		this.setActiveIndCode(values[2]);
		this.setModuleId(Long.parseLong(values[3]));
		this.setRefsetId(Long.parseLong(values[4]));
		this.setReferencedComponentId(Long.parseLong(values[5]));
		this.setAcceptabilityId(Long.parseLong(values[6]));
	}
	
}
