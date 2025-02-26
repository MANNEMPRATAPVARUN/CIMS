package ca.cihi.cims.bll;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.dal.ContextIdentifier;

/**
 * Simple static methods. Seems required, as multiple layers in the framework need information about the Context Access
 * to make decisions
 * 
 */
public class ContextUtils {

	private static final Logger LOGGER = LogManager.getLogger(ContextUtils.class);

	public static void ensureChangeContext(ContextIdentifier ctxId) {
		if (!ctxId.isChangeContext()) {
			String message = "[" + ctxId.getVersionCode() + "] is not a change context for ["
					+ ctxId.getBaseClassification() + "]";

			LOGGER.debug(message);
			throw new IllegalArgumentException(message);
		}
	}

	public static void ensureContextDoesNotRequireReload(ContextIdentifier context, boolean needsReload) {
		if (needsReload) {
			// Element got discarded. Caller should reload context before realizing to avoid errors and conflicts
			String message = "[" + context.getVersionCode() + "] requires reload for ["
					+ context.getBaseClassification() + "]";

			LOGGER.debug(message);
			throw new IllegalArgumentException(message);
		}

	}

	public static void ensureContextIsOldest(ContextIdentifier baseContext,
			Collection<ContextIdentifier> openBaseContexts) {

		int baseContextVersionCode = Integer.parseInt(baseContext.getVersionCode());

		for (ContextIdentifier contextId : openBaseContexts) {
			int openBaseContextVersionCode = Integer.parseInt(contextId.getVersionCode());

			if (baseContextVersionCode > openBaseContextVersionCode) {
				String message = "[" + baseContextVersionCode + "] is not the oldest context for ["
						+ baseContext.getBaseClassification() + "].  Found [" + openBaseContextVersionCode + "]";

				LOGGER.debug(message);
				throw new IllegalArgumentException(message);
			}
		}
	}

	public static void ensureContextIsOpen(ContextIdentifier baseContext) {
		if (!baseContext.isContextOpen()) {

			String message = "[" + baseContext.getVersionCode() + "] is closed for ["
					+ baseContext.getBaseClassification() + "]";

			LOGGER.debug(message);
			throw new IllegalArgumentException(message);

		}
	}

	public static void ensureNotAChangeContext(ContextIdentifier ctxId) {
		if (ctxId.isChangeContext()) {
			String message = "[" + ctxId.getVersionCode() + "] is a change context for ["
					+ ctxId.getBaseClassification() + "]";

			LOGGER.debug(message);
			throw new IllegalArgumentException(message);
		}
	}

	public static Collection<ContextIdentifier> returnNewerContexts(ContextIdentifier baseContext,
			Collection<ContextIdentifier> openBaseContexts) {

		int baseContextVersionCode = Integer.parseInt(baseContext.getVersionCode());
		Collection<ContextIdentifier> newerContexts = new ArrayList<ContextIdentifier>();

		for (ContextIdentifier contextId : openBaseContexts) {
			int openBaseContextVersionCode = Integer.parseInt(contextId.getVersionCode());

			if (baseContextVersionCode < openBaseContextVersionCode) {
				newerContexts.add(contextId);
			}
		}

		return newerContexts;
	}
}
