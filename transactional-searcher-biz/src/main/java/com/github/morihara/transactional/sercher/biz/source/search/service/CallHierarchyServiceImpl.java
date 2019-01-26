package com.github.morihara.transactional.sercher.biz.source.search.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.HierarchyVo;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallHierarchyServiceImpl implements CallHierarchyService {
    private SourceCodeFetchDao sourceCodeFetchDao;

    @Override
    public List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(String sourceFolderPath,
            TransactionalMethodDto transactionalMethodDto, List<String> packagePrefixList) {
        List<HierarchyVo> calledHierarchyList = new ArrayList<>();
        callHierarchy(calledHierarchyList, sourceFolderPath,
                transactionalMethodDto.getSourceCodeVo(), packagePrefixList);
        return makeResultList(transactionalMethodDto, calledHierarchyList);
    }

    private void callHierarchy(List<HierarchyVo> calledHierarchyList, String sourceFolderPath,
            SourceCodeVo sourceCodeVo, List<String> packagePrefixList) {
        List<SourceCodeVo> childSourceCodes = sourceCodeFetchDao
                .fetchCalledMethodsByMethod(sourceFolderPath, sourceCodeVo, packagePrefixList);
        if (CollectionUtils.isEmpty(childSourceCodes)) {
            return;
        }
        for (SourceCodeVo childSourceCodeVo : childSourceCodes) {
            callHierarchy(calledHierarchyList, sourceFolderPath, childSourceCodeVo,
                    packagePrefixList);
        }
    }

    private List<RelatedDaoCodeDto> makeResultList(TransactionalMethodDto transactionalMethodDto,
            List<HierarchyVo> calledHierarchyList) {
        List<RelatedDaoCodeDto> resultDaoCodes = new ArrayList<>();
        int seq = 0;
        for (HierarchyVo hierarchyVo : calledHierarchyList) {
            if (hierarchyVo.isDao() && hierarchyVo.isRequiredTransactional()) {
                SourceCodeVo fetchedMethodVo = hierarchyVo.getSourceCodeVo();
                seq++;
                resultDaoCodes.add(RelatedDaoCodeDto.builder()
                        .transactionalMethodId(transactionalMethodDto.getTransactionalMethodId())
                        .seq(seq).relatedDaoCodeVo(fetchedMethodVo).build());
            }
        }
        return resultDaoCodes;
    }
}
