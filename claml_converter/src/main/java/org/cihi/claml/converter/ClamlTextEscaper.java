package org.cihi.claml.converter;

import j2html.utils.TextEscaper;

/**
 * The Class ClamlTextEscaper.
 */
public class ClamlTextEscaper implements TextEscaper {

  /** The Constant ALPHA_TEMPLATE. */
  private static final String ALPHA_TEMPLATE = "\\[agr   \\]";

  /** The Constant BETA_TEMPLATE. */
  private static final String BETA_TEMPLATE = "\\[bgr   \\]";

  /** The Constant BETA_TEMPLATE_2. */
  private static final String BETA_TEMPLATE_2 = "\\[bgr \\]";

  /**
   * Escape.
   *
   * @param text the text
   * @return the string
   */
  @Override
  public String escape(String text) {
    if (text != null) {
      return text.replaceAll(ALPHA_TEMPLATE, "&alpha;").replaceAll(BETA_TEMPLATE, "&beta;")
          .replaceAll(BETA_TEMPLATE_2, "&beta;");
    }
    return null;
  }
}
