package ca.cihi.cims.framework.domain;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.dto.ContextDTO;
import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.handler.ContextHandler;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 10:48:52 AM
 */
public class Context extends Element {

	/**
	 * elementId = ContextHandler.createContextElement(classs.classid, key) elementVersionId =
	 * ContextHandler.createContextVersion( classs.classId, elementId, versionCode, null) return new Context( new
	 * ElementIdentifier(elementId , elementVersionId ), classs, versionCode)
	 *
	 * @param classs
	 * @param key
	 * @param versionCode
	 * @throws WrongClasssException
	 */
	public static Context createInceptionVersion(Classs classs, String key, String versionCode) {
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		Long elementId = handler.createContextElement(classs.getClassId(), key);
		Long elementVersionId = handler.createContextVersion(classs.getClassId(), elementId, versionCode, null);
		return new Context(new ElementIdentifier(elementId, elementVersionId), classs, versionCode, ContextStatus.OPEN,
				null);
	}

	/**
	 * contextDTO = ContextHandler.getContext(contextId) return new Context(classsDTO )
	 *
	 * @param contextId
	 */
	public static Context findById(Long contextId) {
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		ContextDTO dto = handler.getContext(contextId);
		if (dto == null) {
			return null;
		} else {
			return new Context(dto);
		}
	}

	private ContextStatus contextStatus;

	private String versionCode;

	private Long baseContextId;

	public Context() {

	}

	public Context(ContextDTO dto) {
		super.setContext(this);
		setClasss(new Classs(dto.getClasss()));
		setElementIdentifier(dto.getElementIdentifier());
		setVersionCode(dto.getVersionCode());
		setContextStatus(dto.getContextStatus());
		setBaseContextId(dto.getBaseContextId());
		

	}

	/**
	 * Instantiates a Context object
	 *
	 * @param elementIdentifier
	 * @param classs
	 * @param vesrionCode
	 */
	public Context(ElementIdentifier elementIdentifier, Classs classs, String vesrionCode, ContextStatus contextStatus,
			Long baseContextId) {
		super(classs, elementIdentifier);
		super.setContext(this);
		this.setVersionCode(vesrionCode);
		this.setContextStatus(contextStatus);
		this.setBaseContextId(baseContextId);
	}

	/**
	 *
	 * @param versionCode
	 * @throws WrongClasssException
	 */
	public Context createSubsequentVersion(String versionCode) {
		// elementVersion = ContextHandler.
		// createContextVersion(this.classs.
		// classId, this.elementId, versionCode,
		// this.elementVersionId)
		// return new Context( new
		// ElementIdentifier(this.elementId,
		// elementVersion ), classs, versionCode)
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		Long elementVersionId = handler.createContextVersion(getClasss().getClassId(),
				getElementIdentifier().getElementId(), versionCode, getContextId());

		Context context = new Context(new ElementIdentifier(getElementIdentifier().getElementId(), elementVersionId),
				getClasss(), versionCode, ContextStatus.OPEN, getContextId());

		return context;
	}

	public Long getContextId() {
		return getElementIdentifier().getElementVersionId();
	}

	public ContextStatus getContextStatus() {
		return contextStatus;
	}

	public String getVersionCode() {
		return versionCode;
	}

	/**
	 * ContextHandler.remove(contextId)
	 */
	public void remove() {
		// TODO if allow remove closed context???
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		handler.remove(getContextId());
	}

	public void setContextStatus(ContextStatus contextStatus) {
		this.contextStatus = contextStatus;
	}

	public void closeContext() {
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		handler.closeContext(getContextId());
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public Long getBaseContextId() {
		return baseContextId;
	}

	public void setBaseContextId(Long baseContextId) {
		this.baseContextId = baseContextId;
	}

	public Long getLatestClosedVersion() {
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		return handler.getLatestClosedVersion(getElementIdentifier().getElementId());
	}

	public boolean isOpenVersionExists() {
		ContextHandler handler = ApplicationContextProvider.getApplicationContext().getBean(ContextHandler.class);
		return handler.getOpenVersionCount(getElementIdentifier().getElementId()) == 1;
	}

}