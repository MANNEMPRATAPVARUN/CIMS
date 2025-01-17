package org.cihi.claml.converter;

import java.util.Arrays;
import java.util.Locale;

/**
 * The Enum CciAttributeEnum.
 */
public enum CciAttributeEnum {

  /** The s attribute. */
  S_ATTRIBUTE("S"),

  /** The l attribute. */
  L_ATTRIBUTE("L"),

  /** The e attribute. */
  E_ATTRIBUTE("E"),

  /** The m attribute. */
  M_ATTRIBUTE("M"),

  /** The v attribute. */
  V_ATTRIBUTE("V");

  /** The attribute name. */
  private String attributeName;

  /**
   * Instantiates a {@link CciAttributeEnum} from the specified parameters.
   *
   * @param attributeName the attribute name
   */
  CciAttributeEnum(String attributeName) {
    this.attributeName = attributeName;
  }

  /**
   * Returns the attribute name.
   *
   * @return the attribute name
   */
  public String getAttributeName() {
    return attributeName;
  }

  /**
   * Returns the grey image name.
   *
   * @return the grey image name
   */
  public String getGreyImageName() {
    return attributeName.toLowerCase(Locale.ROOT) + " " + "grey.png";
  }

  /**
   * Returns the pink image name.
   *
   * @return the pink image name
   */
  public String getPinkImageName() {
    return attributeName.toLowerCase(Locale.ROOT) + " " + "pink.png";
  }

  /**
   * Returns the yellow image name.
   *
   * @return the yellow image name
   */
  public String getYellowImageName() {
    return attributeName.toLowerCase(Locale.ROOT) + " " + "yellow.png";
  }

  /**
   * From name.
   *
   * @param name the name
   * @return the cci attribute enum
   */
  public static CciAttributeEnum fromName(String name) {
    return Arrays.stream(CciAttributeEnum.values())
        .filter(cciAttributeEnum -> cciAttributeEnum.attributeName.equals(name)).findFirst()
        .orElse(null);
  }
}
