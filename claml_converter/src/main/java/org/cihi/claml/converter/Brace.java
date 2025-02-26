package org.cihi.claml.converter;

import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.tr;

import java.util.ArrayList;
import java.util.List;

import j2html.tags.specialized.TableTag;
import j2html.tags.specialized.TbodyTag;
import j2html.tags.specialized.TrTag;

/**
 * The Class Brace.
 */
public class Brace {

  /** The tbody tag. */
  private TbodyTag tbodyTag = tbody();

  /** The table tag. */
  private TableTag tableTag = table().with(tbodyTag);

  /** The brace row. */
  private TrTag braceRow = tr();

  /** The rows. */
  private List<TrTag> rows = new ArrayList<>();

  /** The div id. */
  private String divId;

  /** The number of columns. */
  private int numberOfColumns;

  /**
   * Returns the table tag.
   *
   * @return the table tag
   */
  public TableTag getTableTag() {
    return tableTag;
  }

  /**
   * Sets the table tag.
   *
   * @param tableTag the table tag
   */
  public void setTableTag(TableTag tableTag) {
    this.tableTag = tableTag;
  }

  /**
   * Returns the tbody tag.
   *
   * @return the tbody tag
   */
  public TbodyTag getTbodyTag() {
    return tbodyTag;
  }

  /**
   * Sets the tbody tag.
   *
   * @param tbodyTag the tbody tag
   */
  public void setTbodyTag(TbodyTag tbodyTag) {
    this.tbodyTag = tbodyTag;
  }

  /**
   * Returns the rows.
   *
   * @return the rows
   */
  public List<TrTag> getRows() {
    return rows;
  }

  /**
   * Sets the rows.
   *
   * @param rows the rows
   */
  public void setRows(List<TrTag> rows) {
    this.rows = rows;
  }

  /**
   * Returns the div id.
   *
   * @return the div id
   */
  public String getDivId() {
    return divId;
  }

  /**
   * Sets the div id.
   *
   * @param divId the div id
   */
  public void setDivId(String divId) {
    this.divId = divId;
  }

  /**
   * Returns the number of columns.
   *
   * @return the number of columns
   */
  public int getNumberOfColumns() {
    return numberOfColumns;
  }

  /**
   * Sets the number of columns.
   *
   * @param numberOfColumns the number of columns
   */
  public void setNumberOfColumns(int numberOfColumns) {
    this.numberOfColumns = numberOfColumns;
  }

  /**
   * Returns the brace row.
   *
   * @return the brace row
   */
  public TrTag getBraceRow() {
    return braceRow;
  }

  /**
   * Sets the brace row.
   *
   * @param braceRow the brace row
   */
  public void setBraceRow(TrTag braceRow) {
    this.braceRow = braceRow;
  }
}
