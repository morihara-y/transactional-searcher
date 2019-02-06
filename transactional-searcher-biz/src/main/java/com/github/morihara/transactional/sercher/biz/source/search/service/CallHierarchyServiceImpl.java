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
    private final SourceCodeFetchDao sourceCodeFetchDao;

    @Override
    public List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(String sourceFolderPath,
            TransactionalMethodDto transactionalMethodDto, List<String> packagePrefixList) {
        List<HierarchyVo> calledHierarchyList = new ArrayList<>();
        List<String> memoList = new ArrayList<>();
        SourceCodeVo baseSourceCodeVo = transactionalMethodDto.getSourceCodeVo();
        memoList.add(baseSourceCodeVo.toUniqueMethodStr());
        callHierarchy(calledHierarchyList, memoList, sourceFolderPath, baseSourceCodeVo, packagePrefixList);
        return makeResultList(transactionalMethodDto, calledHierarchyList);
    }

    private void callHierarchy(List<HierarchyVo> calledHierarchyList, List<String> memoList, String sourceFolderPath,
            SourceCodeVo sourceCodeVo, List<String> packagePrefixList) {
        List<SourceCodeVo> childSourceCodes = sourceCodeFetchDao
                .fetchCalledMethodsByMethod(sourceFolderPath, sourceCodeVo, packagePrefixList);
        if (CollectionUtils.isEmpty(childSourceCodes)) {
            return;
        }
        for (SourceCodeVo childSourceCodeVo : childSourceCodes) {
            if (memoList.contains(childSourceCodeVo.toUniqueMethodStr())) {
                continue;
            }
            memoList.add(childSourceCodeVo.toUniqueMethodStr());
            callHierarchy(calledHierarchyList, memoList, sourceFolderPath, childSourceCodeVo, packagePrefixList);
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
