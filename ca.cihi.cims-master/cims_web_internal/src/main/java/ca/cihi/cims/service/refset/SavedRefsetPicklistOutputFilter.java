package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

public class SavedRefsetPicklistOutputFilter extends RefsetPicklistOutputFilter {
    @Override
    public List<PicklistOutputDTO> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest,
            List<PicklistOutputDTO> origRefsetOutputList) {
        if (refsetOutputRequest == null) {
            return origRefsetOutputList;
        }

        if (origRefsetOutputList == null) {
            return null;
        }

        ArrayList<PicklistOutputDTO> filteredList = new ArrayList<PicklistOutputDTO>();
        
        for (PicklistOutputDTO p : origRefsetOutputList) {
            if (isRefsetPicklistOutputSaved(refsetOutputRequest.getRefsetOutputId(), p.getPicklistOutputId())) {
                continue;
            }
            
            filteredList.add(p);
        }
        
        return !filteredList.isEmpty() ? filteredList : null;
    }

    /**
     * Check the relationship of refset output & picklist output has been saved
     * or not.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @param picklistOutputId
     *            the picklist output id.
     * @return true - saved, false - not saved.
     */
    private boolean isRefsetPicklistOutputSaved(Integer refsetOutputId, Integer picklistOutputId) {
        return RefsetFactory.getRefsetPicklistOutputById(refsetOutputId, picklistOutputId) != null;
    }
}
