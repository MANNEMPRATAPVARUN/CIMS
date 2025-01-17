package ca.cihi.cims.service.refset;

import java.util.List;

import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;

public abstract class RefsetPicklistOutputFilter {
    /**
     * Next filter in the chain.
     */
    protected RefsetPicklistOutputFilter successor;

    public void setSuccessor(RefsetPicklistOutputFilter successor) {
        this.successor = successor;
    }

    public RefsetPicklistOutputFilter getSuccessor() {
        return successor;
    }

    public abstract List<PicklistOutputDTO> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest,
            List<PicklistOutputDTO> origRefsetOutputList);
}
