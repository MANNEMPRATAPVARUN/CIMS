package ca.cihi.cims.dal.jdbc;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.dal.query.ElementRef;

final class TableAliases {
	private int tableAliasCount = 0;

	private Map<ElementRef, String> tableAliases = new HashMap<ElementRef, String>();

	public String alias(String tablename) {
		return tablename + (tableAliasCount++);
	}

	public String alias(ElementRef elementRef) {
		if (tableAliases.containsKey(elementRef)) {
			return tableAliases.get(elementRef);
		}

		String newAlias = alias(elementRef.getElementClass()
				.getSimpleName());

		tableAliases.put(elementRef, newAlias);
		return newAlias;
	}
}