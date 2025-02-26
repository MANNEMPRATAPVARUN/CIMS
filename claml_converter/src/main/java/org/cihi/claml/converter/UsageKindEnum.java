package org.cihi.claml.converter;

import java.util.Arrays;

/**
 * Enumeration for Usage kind.
 */
public enum UsageKindEnum {

  /** The dagger. */
  DAGGER("+"),

  /** The asterisk. */
  ASTERISK("*");

  /** The value. */
  private final String value;

  /**
   * Instantiates a {@link UsageKindEnum} from the specified parameters.
   *
   * @param value the value
   */
  UsageKindEnum(String value) {
    this.value = value;
  }

  /**
   * Returns the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * From value.
   *
   * @param value the value
   * @return the usage kind enum
   */
  public static UsageKindEnum fromValue(String value) {
    return Arrays.stream(UsageKindEnum.values())
        .filter(usageKindEnum -> usageKindEnum.value.equals(value)).findFirst().orElse(null);
  }
}
