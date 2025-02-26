package ca.cihi.cims.model.snomed;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCTRelationship implements SCTBase, Serializable {
		
	private static final long serialVersionUID = 1L;

	private static final Log LOGGER = LogFactory.getLog(SCTRelationship.class);

	private long id;
	private Date effectiveTime;
	private String activeIndCode;
	private long moduleId;
	private long sourceId;
	private long destinationId;
	private long relationshipGroup;
	private long typeId;
	private long characteristicTypeId;
	private long modifierId;
		
	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public long getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(long destinationId) {
		this.destinationId = destinationId;
	}

	public long getRelationshipGroup() {
		return relationshipGroup;
	}

	public void setRelationshipGroup(long relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public long getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(long characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	public long getModifierId() {
		return modifierId;
	}

	public void setModifierId(long modifierId) {
		this.modifierId = modifierId;
	}

	//Populate one record based on below format
	//id	effectiveTime	active	moduleId	sourceId	destinationId	relationshipGroup	typeId	characteristicTypeId	modifierId
	public void setValues(String[] values) throws Exception {
		/*
		LOGGER.debug("values[0]="+values[0]+" values[1]="+values[1]+" values[2]="+values[2]+" values[3]="+values[3]+
                " values[4]="+values[4] + "values[5]=" +values[5] + "values[6]=" +values[6] +
                " values[7]="+values[7] + "values[8]=" +values[8] + "values[9]=" +values[9] );
        */                
		this.setId(Long.parseLong(values[0]));
		String DATE_FORMAT = "yyyyMMdd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.setEffectiveTime(sdf.parse(values[1]));
		this.setActiveIndCode(values[2]);
		this.setModuleId(Long.parseLong(values[3]));
		this.setSourceId(Long.parseLong(values[4]));
		this.setDestinationId(Long.parseLong(values[5]));
		this.setRelationshipGroup(Long.parseLong(values[6]));
		this.setTypeId(Long.parseLong(values[7]));
		this.setCharacteristicTypeId(Long.parseLong(values[8]));
		this.setModifierId(Long.parseLong(values[9]));
	}

}
