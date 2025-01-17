package org.cihi.claml.converter;

import java.util.Arrays;

/**
 * Enum for Rubric kinds.
 */
public enum RubricKindEnum {

  /** The preferred. */
  PREFERRED("preferred"),

  /** The includes. */
  INCLUDES("includes"),

  /** The includes attribute. */
  INCLUDES_ATTRIBUTE("includes-attribute"),

  /** The excludes. */
  EXCLUDES("excludes"),

  /** The code also. */
  CODE_ALSO("code-also"),

  OMIT_CODE("omit-code"),

  /** The note. */
  NOTE("note"),

  /** The text. */
  TEXT("text"),

  /** The index level. */
  INDEX_LEVEL("index-level"),

  /** The see also. */
  SEE_ALSO("see-also"),

  /** The see. */
  SEE("see");

  /** The value. */
  private final String value;

  /**
   * Returns the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Instantiates a {@link RubricKindEnum} from the specified parameters.
   *
   * @param value the value
   */
  RubricKindEnum(String value) {
    this.value = value;
  }

  /**
   * From value.
   *
   * @param value the value
   * @return the rubric kind enum
   */
  public static RubricKindEnum fromValue(String value) {
    return Arrays.stream(RubricKindEnum.values())
        .filter(rubricKindEnum -> rubricKindEnum.value.equals(value)).findFirst().orElse(null);
  }
}
