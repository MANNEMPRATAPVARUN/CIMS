package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

@Component
public class SavedRefsetSupplementOutputFilter {
    /**
     * Reference to refset service.
     */
    @Autowired
    private RefsetService refsetService;
    
    public List<Supplement> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest) {
        if (refsetOutputRequest == null) {
            return null;
        } 
        
        Refset refset = refsetService.getRefset(refsetOutputRequest.getRefsetContextId(),
                refsetOutputRequest.getElementId(), refsetOutputRequest.getElementVersionId());

        List<Supplement> origSupplementList = refsetService.getSupplements(refset);
        
        if (origSupplementList == null) {
            return null;
        }      

        ArrayList<Supplement> filteredList = new ArrayList<Supplement>();

        for (Supplement s : origSupplementList) {
            if (isRefsetSupplementOutputSaved(refsetOutputRequest.getRefsetOutputId(),
                    s.getElementIdentifier().getElementId())) {
                continue;
            }

            filteredList.add(s);
        }

        return !filteredList.isEmpty() ? filteredList : null;
    }

    /**
     * Check the relationship of refset output & supplement output has been
     * saved or not.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @param supplementId
     *            the supplement id.
     * @return true - saved, false - not saved.
     */
    private boolean isRefsetSupplementOutputSaved(Integer refsetOutputId, Long supplementId) {
        return RefsetFactory.getRefsetSupplementOutputById(refsetOutputId, supplementId) != null;
    }
}
