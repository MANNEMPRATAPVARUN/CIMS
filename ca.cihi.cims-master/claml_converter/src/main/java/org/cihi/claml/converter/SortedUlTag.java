package org.cihi.claml.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Sorted "ul" tag.
 */
public class SortedUlTag extends ContainerTag<SortedUlTag> {

  private Locale locale;
  /**
   * Instantiates an empty {@link SortedUlTag}.
   */
  public SortedUlTag(Locale locale) {
    super("ul");
    this.locale = locale;
  }

  /**
   * Sort.
   */
  public void sort() {
    Map<Boolean, List<SortedLiTag>> filteredChildren =
        children.stream().map(SortedLiTag.class::cast)
            .collect(Collectors.partitioningBy(sortedLiTag -> sortedLiTag.getParentId() == null));
    // Sort the list items that are not nested
    filteredChildren.get(true).sort(new ListItemComparator(locale));
    List<SortedLiTag> sortedLiTags = Lists.newArrayList(filteredChildren.get(true));
    for (SortedLiTag liTag : filteredChildren.get(false)) {
      liTag.sort();
      int indexOfParent = sortedLiTags.indexOf(new SortedLiTag(liTag.getParentId()));
      if (indexOfParent > -1) {
        sortedLiTags.add(indexOfParent + 1, liTag);
      }
    }
    children = sortedLiTags.stream().map(DomContent.class::cast).collect(Collectors.toList());
  }

  /**
   * Returns the parent li tag.
   *
   * @return the parent li tag
   */
  public Integer getParentLiTag() {
    DomContent domContent = Iterables.getLast(children, null);
    if (domContent != null) {
      return ((SortedLiTag) domContent).getId();
    }
    return null;
  }

  protected List<DomContent> getChildren(){
    return children;
  }

  protected void setChildren(List<DomContent> in){
    if(in == null){
      children = new ArrayList<>();
    }
    children.addAll(in);
  }
}
