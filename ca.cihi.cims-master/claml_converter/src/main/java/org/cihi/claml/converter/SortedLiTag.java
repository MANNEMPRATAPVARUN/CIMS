package org.cihi.claml.converter;

import java.util.Objects;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Sorted "li" tag.
 */
public class SortedLiTag extends ContainerTag<SortedLiTag> {

  /** The id. */
  private final Integer id;

  /** The parent id. */
  private Integer parentId;

  /**
   * Instantiates a {@link SortedLiTag} from the specified parameters.
   *
   * @param id the id
   */
  public SortedLiTag(Integer id) {
    super("li");
    this.id = id;
  }

  /**
   * Sorted li tag.
   *
   * @param id the id
   * @return the sorted li tag
   */
  public static SortedLiTag sortedLiTag(Integer id) {
    return new SortedLiTag(id);
  }

  /**
   * Sorted li tag.
   *
   * @param id the id
   * @param text the text
   * @return the sorted li tag
   */
  public static SortedLiTag sortedLiTag(Integer id, String text) {
    return new SortedLiTag(id).withText(text);
  }

  /**
   * Equals.
   *
   * @param o the o
   * @return true, if successful
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    SortedLiTag liTag = (SortedLiTag) o;
    return Objects.equals(id, liTag.id);
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /**
   * Returns the id.
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Returns the parent id.
   *
   * @return the parent id
   */
  public Integer getParentId() {
    return parentId;
  }

  /**
   * Sets the parent id.
   *
   * @param parentId the parent id
   */
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * Sort.
   */
  public void sort() {
    for (DomContent domContent : children) {
      if (domContent instanceof SortedUlTag) {
        ((SortedUlTag) domContent).sort();
      }
    }
  }
}
