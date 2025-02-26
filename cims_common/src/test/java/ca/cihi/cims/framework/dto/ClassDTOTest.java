package ca.cihi.cims.framework.dto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ClassDTOTest {

	@Test
	public void test() {
		ClasssDTO dto = new ClasssDTO();
		ClasssDTO dtoFull = new ClasssDTO("ConceptVersion", "RefSet1", "Refset", "Refset");
		dtoFull.setClasssId(1l);
		assertTrue(dtoFull.equals(dtoFull));

		assertFalse(dto.equals(dtoFull));
		assertFalse(dto.equals(new Object()));
	}

}
