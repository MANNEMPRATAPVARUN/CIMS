package org.cihi.claml.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Claml text-escaper tests.
 */
public class ClamlTextEscaperTest {

  /** The claml text escaper. */
  private ClamlTextEscaper clamlTextEscaper = new ClamlTextEscaper();

  /**
   * Test null.
   */
  @Test
  public void testNull() {
    assertThat(clamlTextEscaper.escape(null)).isNull();
  }

  /**
   * Test initial match no beta.
   */
  @Test
  public void testInitialMatchNoBeta() {
    assertThat(clamlTextEscaper.escape("[bgr")).isEqualTo("[bgr");
  }

  /**
   * Test end match no beta.
   */
  @Test
  public void testEndMatchNoBeta() {
    assertThat(clamlTextEscaper.escape("test]")).isEqualTo("test]");
  }

  /**
   * Test multiple space match beta.
   */
  @Test
  public void testMultipleSpaceMatchBeta() {
    assertThat(clamlTextEscaper.escape("[bgr   ]")).isEqualTo("&beta;");
  }

  /**
   * Test middle beta.
   */
  @Test
  public void testMiddleBeta() {
    assertThat(clamlTextEscaper.escape("test [bgr   ] string")).isEqualTo("test &beta; string");
  }
}
