package ca.cihi.cims.service.prodpub;

import org.apache.poi.ss.util.CellRangeAddress;

public class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {

	public CellRangeAddress range;

	/**
	 * @param theRange
	 *            the CellRangeAddress object to wrap.
	 */
	public CellRangeAddressWrapper(CellRangeAddress theRange) {
		this.range = theRange;
	}

	@Override
	public int compareTo(CellRangeAddressWrapper o) {
		if (range.getFirstColumn() < o.range.getFirstColumn() && range.getFirstRow() < o.range.getFirstRow()) {
			return -1;
		} else if (range.getFirstColumn() == o.range.getFirstColumn() && range.getFirstRow() == o.range.getFirstRow()) {
			return 0;
		} else {
			return 1;
		}
	}

}
