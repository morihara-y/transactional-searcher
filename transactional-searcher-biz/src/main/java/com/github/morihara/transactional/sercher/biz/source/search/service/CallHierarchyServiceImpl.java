package com.github.morihara.transactional.sercher.biz.source.search.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.HierarchyVo;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallHierarchyServiceImpl implements CallHierarchyService {

    @Override
    public List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(
            TransactionalMethodDto transactionalMethodDto) {
        List<HierarchyVo> calledHierarchyList = callHierarchy(transactionalMethodDto.getSourceCodeVo());
        return makeResultList(transactionalMethodDto, calledHierarchyList);
    }

    private List<HierarchyVo> callHierarchy(SourceCodeVo sourceCodeVo) {
        return null;
    }

    private List<RelatedDaoCodeDto> makeResultList(TransactionalMethodDto transactionalMethodDto,
            List<HierarchyVo> calledHierarchyList) {
        List<RelatedDaoCodeDto> resultDaoCodes = new ArrayList<>();
        int seq = 0;
        for (HierarchyVo hierarchyVo : calledHierarchyList) {
            if (hierarchyVo.isDao() && hierarchyVo.isRequiredTransactional()) {
                SourceCodeVo fetchedMethodVo = hierarchyVo.getSourceCodeVo();
                seq++;
                resultDaoCodes.add(
                        RelatedDaoCodeDto.builder()
                            .transactionalMethodId(transactionalMethodDto.getTransactionalMethodId())
                            .seq(seq)
                            .relatedDaoCodeVo(fetchedMethodVo)
                            .build());
            }
        }
        return resultDaoCodes;
    }
}
