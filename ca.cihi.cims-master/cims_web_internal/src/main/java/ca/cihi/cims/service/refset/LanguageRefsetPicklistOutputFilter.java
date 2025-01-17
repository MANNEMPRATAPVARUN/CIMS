package ca.cihi.cims.service.refset;

import java.util.List;
import java.util.stream.Collectors;

import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;

public class LanguageRefsetPicklistOutputFilter extends RefsetPicklistOutputFilter {
    @Override
    public List<PicklistOutputDTO> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest,
            List<PicklistOutputDTO> origRefsetOutputList) {
        if (refsetOutputRequest == null) {
            return origRefsetOutputList;
        }

        if (origRefsetOutputList == null) {
            return null;
        }

        return origRefsetOutputList.stream()
                .filter(x -> refsetOutputRequest.getLanguageCode().equals(x.getLanguageCode()))
                .collect(Collectors.toList());
    }
}
