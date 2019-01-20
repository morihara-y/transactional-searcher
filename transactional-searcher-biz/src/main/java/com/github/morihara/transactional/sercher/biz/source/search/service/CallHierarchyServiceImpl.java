package com.github.morihara.transactional.sercher.biz.source.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.dao.spoon.HierarchyResearchDao;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.HierarchyVo;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallHierarchyServiceImpl implements CallHierarchyService {
    private final HierarchyResearchDao hierarchyResearchDao;

    @Override
    public List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(
            TransactionalMethodDto transactionalMethodDto) {
        UUID transactionalMethodId = transactionalMethodDto.getTransactionalMethodId();
        List<RelatedDaoCodeDto> resultDaoCodes = new ArrayList<>();

        List<HierarchyVo> hierarchyVoList =
                hierarchyResearchDao.callHierarchy(transactionalMethodDto.getSourceCodeVo());

        int seq = 0;
        for (HierarchyVo hierarchyVo : hierarchyVoList) {
            if (hierarchyVo.isDao() && hierarchyVo.isRequiredTransactional()) {
                SourceCodeVo fetchedMethodVo = hierarchyVo.getSourceCodeVo();
                seq++;
                resultDaoCodes.add(
                        RelatedDaoCodeDto.builder()
                            .transactionalMethodId(transactionalMethodId)
                            .seq(seq)
                            .relatedDaoCodeVo(fetchedMethodVo)
                            .build());
            }
        }
        return resultDaoCodes;
    }
}
