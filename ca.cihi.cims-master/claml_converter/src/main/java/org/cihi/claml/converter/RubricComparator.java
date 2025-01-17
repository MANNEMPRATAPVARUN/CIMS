package org.cihi.claml.converter;

import org.cihi.claml.schema.Label;
import org.cihi.claml.schema.Rubric;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class RubricComparator implements Comparator<Rubric> {

  private Locale locale;

  public RubricComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(Rubric o1, Rubric o2) {
    Collator collator = Collator.getInstance(this.locale);
    collator.setStrength(Collator.PRIMARY);
    String content1 = getLabelContent(o1);
    String content2 = getLabelContent(o2);
    return collator.compare(content1, content2);
  }

  /**
   * Expecting only Content per Label and one Label per Rubric
   *
   * @param rubric
   * @return
   */
  private String getLabelContent(Rubric rubric) {
    return rubric.getLabel().stream().findFirst().orElse(new Label()).getContent().stream()
        .map(String.class::cast)
        .findFirst()
        .orElse("");
  }
}
