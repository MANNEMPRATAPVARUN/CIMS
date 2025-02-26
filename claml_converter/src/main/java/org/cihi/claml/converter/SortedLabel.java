package org.cihi.claml.converter;

import java.util.Objects;

/**
 * Sorted label.
 */
public class SortedLabel {

  /** The content. */
  private final String content;

  /** The content id. */
  private final Integer contentId;

  /** The parent content id. */
  private Integer parentContentId;

  /**
   * Instantiates a {@link SortedLabel} from the specified parameters.
   *
   * @param contentId the content id
   * @param content the content
   */
  public SortedLabel(Integer contentId, String content) {
    this.content = content;
    this.contentId = contentId;
  }

  /**
   * Returns the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Returns the parent content id.
   *
   * @return the parent content id
   */
  public Integer getParentContentId() {
    return parentContentId;
  }

  /**
   * Sets the parent content id.
   *
   * @param parentContentId the parent content id
   */
  public void setParentContentId(Integer parentContentId) {
    this.parentContentId = parentContentId;
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
    SortedLabel that = (SortedLabel) o;
    return Objects.equals(contentId, that.contentId);
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return Objects.hash(contentId);
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return content;
  }
}
