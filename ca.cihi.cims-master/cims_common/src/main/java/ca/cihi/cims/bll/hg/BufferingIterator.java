package ca.cihi.cims.bll.hg;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class BufferingIterator<T> implements Iterator<T> {

	private Iterator<T> wrappedIterator;

	private int bufferSize;

	private Queue<T> buffer = new LinkedList<T>();

	public BufferingIterator(Iterator<T> wrappedIterator, int bufferSize) {
		this.wrappedIterator = wrappedIterator;
		this.bufferSize = bufferSize;
	}

	@Override
	public boolean hasNext() {
		fillBufferIfNecessary();
		return !buffer.isEmpty();
	}

	@Override
	public T next() {
		fillBufferIfNecessary();
		return buffer.poll();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void fillBufferIfNecessary() {
		if (buffer.isEmpty()) {
			while (buffer.size() < bufferSize && wrappedIterator.hasNext()) {
				buffer.add(wrappedIterator.next());
			}
			onBufferFilled(buffer);
		}

	}

	protected void onBufferFilled(Queue<T> buffer) {
		// Template method, does nothing
	}

}
