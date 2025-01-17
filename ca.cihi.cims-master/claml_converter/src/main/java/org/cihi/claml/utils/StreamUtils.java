package org.cihi.claml.utils;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream utilities.
 */
public class StreamUtils {

  /**
   * As stream.
   *
   * @param <T> the
   * @param sourceIterator the source iterator
   * @return the stream
   */
  public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
    return asStream(sourceIterator, false);
  }

  /**
   * As stream.
   *
   * @param <T> the
   * @param sourceIterator the source iterator
   * @param parallel the parallel
   * @return the stream
   */
  public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
    Iterable<T> iterable = () -> sourceIterator;
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }
}
