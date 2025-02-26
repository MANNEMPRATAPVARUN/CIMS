package ca.cihi.cims.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ContextService {

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void blockUnfreeze(Long contextId);

	@Transactional
	void deleteContext(Long contextId);

	@Transactional
	void freezeAllChanges(Long contextId);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void freezeTabularChanges(Long contextId);

	@Transactional
	void unfreezeTabularChanges(Long contextId);
}
