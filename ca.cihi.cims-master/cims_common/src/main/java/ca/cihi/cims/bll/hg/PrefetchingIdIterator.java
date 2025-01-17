package ca.cihi.cims.bll.hg;

import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A specialised iterator that ensures that the upcoming elements and their properties have all been preloaded.
 */
class PrefetchingIdIterator extends BufferingIterator<Long> {

	private static final Logger LOGGER = LogManager.getLogger(PrefetchingIdIterator.class);

	private final ContextElementAccess operations;

	public PrefetchingIdIterator(Iterator<Long> idIterator, int bufferSize, ContextElementAccess operations) {
		super(idIterator, bufferSize);
		this.operations = operations;
	}

	@Override
	protected void onBufferFilled(Queue<Long> buffer) {
		LOGGER.debug("Prefetching elements: " + buffer);

		operations.loadElements(Collections.unmodifiableCollection(buffer));
	}

}
