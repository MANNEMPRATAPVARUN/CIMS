package ca.cihi.cims.service.refset;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.exception.SupplementInUseException;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

@Service
public class SupplementServiceImpl implements SupplementService {

    @Override
    @Transactional
    public void deleteSupplement(SupplementViewBean viewBean) throws Exception {
        List<RefsetSupplementOutputDTO> refsetSupplementOutputList = RefsetFactory
                .getRefsetSupplementOutputBySupplementId(viewBean.getContextId(), viewBean.getSupplementElementId());

        if (refsetSupplementOutputList != null && !refsetSupplementOutputList.isEmpty()) {
            throw new SupplementInUseException("supplement in use. ");
        }

        Supplement supplement = RefsetFactory.getSupplement(viewBean.getContextId(),
                new ElementIdentifier(viewBean.getSupplementElementId(), viewBean.getSupplementElementVersionId()),
                ConceptLoadDegree.MINIMAL);
        supplement.remove();
    }

    @Override
    public Supplement getSupplement(SupplementViewBean viewBean) throws Exception {
        return RefsetFactory.getSupplement(viewBean.getContextId(),
                new ElementIdentifier(viewBean.getSupplementElementId(), viewBean.getSupplementElementVersionId()),
                ConceptLoadDegree.MINIMAL);
    }

}
