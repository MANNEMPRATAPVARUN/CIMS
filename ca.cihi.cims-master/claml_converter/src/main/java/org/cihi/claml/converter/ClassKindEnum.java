package org.cihi.claml.converter;

import java.util.Arrays;

/**
 * The Enum ClassKindEnum.
 */
public enum ClassKindEnum {

  /** The chapter. */
  CHAPTER("chapter"),

  /** The block. */
  BLOCK("block"),

  /** The category. */
  CATEGORY("category"),

  /** The attribute. */
  ATTRIBUTE("attribute"),

  /** The attribute code. */
  ATTRIBUTE_CODE("attribute-code"),

  /** The front matter. */
  FRONT_MATTER("front-matter"),

  /** The back matter. */
  BACK_MATTER("back-matter"),

  /** The book index. */
  BOOK_INDEX("book-index"),

  /** The letter index. */
  LETTER_INDEX("letter-index"),

  /** The index term. */
  INDEX_TERM("index-term");

  /** The value. */
  private final String value;

  /**
   * Instantiates a {@link ClassKindEnum} from the specified parameters.
   *
   * @param value the value
   */
  ClassKindEnum(String value) {
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
   * @return the class kind enum
   */
  public static ClassKindEnum fromValue(String value) {
    return Arrays.stream(ClassKindEnum.values())
        .filter(classKindEnum -> classKindEnum.value.equals(value)).findFirst().orElse(null);
  }
}
