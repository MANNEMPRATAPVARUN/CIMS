package ca.cihi.cims.model.refset;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RefsetExportTest {
	
	private RefsetExport refsetExport1;
	private RefsetExport refsetExport2;
	private RefsetExport refsetExport3;
	private RefsetExport refsetExport4;
	private RefsetExport refsetExport5;	
	private RefsetExport refsetExport6;	
	
	@Before
	public void setUp() {
		refsetExport1 = new RefsetExport();
		refsetExport1.setColumnId(0l);
		refsetExport1.setOrderNumber(1);
		refsetExport1.setCellValue("abc");
		
		refsetExport2 = new RefsetExport();
		refsetExport2.setColumnId(0l);
		refsetExport2.setOrderNumber(1);
		refsetExport2.setCellValue("abc");
		
		refsetExport3 = new RefsetExport();
		refsetExport3.setColumnId(0l);
		refsetExport3.setOrderNumber(1);
		refsetExport3.setCellValue(null);		
		
		refsetExport4 = new RefsetExport();
		refsetExport4.setColumnId(0l);
		refsetExport4.setOrderNumber(1);
		refsetExport4.setCellValue(null);
				
		refsetExport5 = new RefsetExport();
		refsetExport5.setColumnId(0l);
		refsetExport5.setOrderNumber(2);
		refsetExport5.setCellValue(null);
		
		refsetExport6 = new RefsetExport();
		refsetExport6.setColumnId(0l);
		refsetExport6.setOrderNumber(1);
		refsetExport6.setCellValue("");
	}
	
	@Test
	public void test(){
		assertTrue(refsetExport1.equals(refsetExport2));
		assertTrue(refsetExport1.equals(refsetExport3));
		assertTrue(refsetExport1.equals(refsetExport5));		
		
		assertTrue(refsetExport1.compareTo(refsetExport2)==0);
		assertTrue(refsetExport1.compareTo(refsetExport3)<0);
		assertTrue(refsetExport3.compareTo(refsetExport4)==0);
		
		assertTrue(refsetExport1.compareTo(refsetExport5)<0);
		
		assertTrue(refsetExport1.compareTo(refsetExport6)<0);
	}
	
	

}
