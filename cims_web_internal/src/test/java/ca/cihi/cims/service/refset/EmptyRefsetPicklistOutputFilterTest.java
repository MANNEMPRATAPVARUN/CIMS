package ca.cihi.cims.service.refset;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;

public class EmptyRefsetPicklistOutputFilterTest {
    /**
     * Refset Output Configuration.
     */
    private static final List<PicklistOutputDTO> origRefsetOutputList = new ArrayList<PicklistOutputDTO>();

    private static final PicklistOutputDTO p1 = new PicklistOutputDTO();

    private static final PicklistOutputDTO p2 = new PicklistOutputDTO();

    static {
        p1.setLanguageCode("ENG");
        p1.setName("p1");
        p1.setOutputCode("p1");
        p1.setPicklistId(1L);
        p1.setPicklistOutputId(1);
        p1.setRefsetContextId(1L);
        p1.setTableName("p1");
        p1.setTabName("p1");

        p2.setLanguageCode("ENG");
        p2.setName("p2");
        p2.setOutputCode("p2");
        p2.setPicklistId(2L);
        p2.setPicklistOutputId(2);
        p2.setRefsetContextId(2L);

        origRefsetOutputList.add(p1);
        origRefsetOutputList.add(p2);
    }

    @Before
    public void setup() {
    }

    @Test
    public void testGetAvailableRefsetOutput() {
        RefsetOutputRequest refsetOutputRequest = new RefsetOutputRequest();

        refsetOutputRequest.setLanguageCode("ENG");

        List<PicklistOutputDTO> filteredRefsetOutputList = new EmptyRefsetPicklistOutputFilter()
                .getAvailableRefsetOutput(refsetOutputRequest, origRefsetOutputList);

        assertTrue(filteredRefsetOutputList.contains(p1));
        assertFalse(filteredRefsetOutputList.contains(p2));
    }
}
