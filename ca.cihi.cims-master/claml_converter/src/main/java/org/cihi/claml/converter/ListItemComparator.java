package org.cihi.claml.converter;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * List item comparator.
 */
public class ListItemComparator implements Comparator<SortedLiTag> {

  private final String notSpecifiedLiTag;
  private final Locale locale;

  public ListItemComparator(Locale locale) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("Icd_Message", locale);
    notSpecifiedLiTag = "<li>" + resourceBundle.getString("content.nos");
    this.locale = locale;
  }

  /**
   * Compare.
   *
   * @param o1 the o 1
   * @param o2 the o 2
   * @return the int
   */
  @Override
  public int compare(SortedLiTag o1, SortedLiTag o2) {
    Collator collator = Collator.getInstance(this.locale); //Your locale here
    collator.setStrength(Collator.PRIMARY);
    o1.sort();
    o2.sort();
    if (o1.toString().startsWith(notSpecifiedLiTag)) {
      return -1;
    }
    if (o2.toString().startsWith(notSpecifiedLiTag)) {
      return 1;
    }
    return collator.compare(o1.toString(),o2.toString());
  }
}
