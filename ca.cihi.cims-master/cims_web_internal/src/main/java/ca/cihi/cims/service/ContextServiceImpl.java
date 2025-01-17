package ca.cihi.cims.service;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.data.mapper.ContextMapper;
import ca.cihi.cims.model.ContextStatus;

public class ContextServiceImpl implements ContextService {

	private ContextMapper contextMapper;

	@Autowired
	ContextOperations operations;

	@Override
	public void blockUnfreeze(Long contextId) {
		contextMapper.blockUnfreeze(contextId);
	}

	@Override
	public void deleteContext(Long contextId) {

		operations.updateChangeContextStatus(contextId, ContextStatus.DELETED.getCode());
	}

	@Override
	public void freezeAllChanges(Long contextId) {
		contextMapper.freezeAllChanges(contextId);
	}

	@Override
	public void freezeTabularChanges(Long contextId) {
		contextMapper.freezeTabularChanges(contextId);
	}

	public ContextMapper getContextMapper() {
		return contextMapper;
	}

	public void setContextMapper(ContextMapper contextMapper) {
		this.contextMapper = contextMapper;
	}

	@Override
	public void unfreezeTabularChanges(Long contextId) {
		contextMapper.unfreezeStructure(contextId);
	}
}
