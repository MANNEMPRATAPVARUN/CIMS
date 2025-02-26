package org.cihi.claml.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Class CciAttribute.
 */
public class CciAttribute {

  /** The ref id. */
  private String refId;

  /** The codes. */
  private List<CciAttributeCode> codes = new ArrayList<>();

  private String note;

  /**
   * Instantiates an empty {@link CciAttribute}.
   */
  public CciAttribute() {
  }

  /**
   * Instantiates a {@link CciAttribute} from the specified parameters.
   *
   * @param refId the ref id
   */
  public CciAttribute(String refId) {
    this.refId = refId;
  }

  /**
   * Returns the ref id.
   *
   * @return the ref id
   */
  public String getRefId() {
    return refId;
  }

  /**
   * Sets the ref id.
   *
   * @param refId the ref id
   */
  public void setRefId(String refId) {
    this.refId = refId;
  }

  /**
   * Returns the codes.
   *
   * @return the codes
   */
  public List<CciAttributeCode> getCodes() {
    return codes;
  }

  /**
   * Sets the codes.
   *
   * @param codes the codes
   */
  public void setCodes(List<CciAttributeCode> codes) {
    this.codes = codes;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
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
    CciAttribute that = (CciAttribute) o;
    return Objects.equals(refId, that.refId);
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return Objects.hash(refId);
  }
}
