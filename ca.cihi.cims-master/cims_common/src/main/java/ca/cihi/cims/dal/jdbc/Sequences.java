package ca.cihi.cims.dal.jdbc;

public interface Sequences {
	
	static final String ELEMENT_ID_SEQUENCE = "ELEMENTID_SEQ";
	static final String ELEMENTVERSION_ID_SEQUENCE = "ELEMENTVERSIONID_SEQ";
	
	long nextValue(String sequenceName);
}
