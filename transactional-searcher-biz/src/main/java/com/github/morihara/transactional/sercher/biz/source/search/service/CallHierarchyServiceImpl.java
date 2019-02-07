package com.github.morihara.transactional.sercher.biz.source.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.sercher.service.enumrate.TargetMethodEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallHierarchyServiceImpl implements CallHierarchyService {
    private final SourceCodeFetchDao sourceCodeFetchDao;

    private static final Map<String, List<SourceCodeVo>> MEMO_MAP = new HashMap<>();

    @Override
    public List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList) {
        List<RelatedDaoCodeDto> resultList = new ArrayList<>();
        SourceCodeVo baseSourceCodeVo = transactionalMethodDto.getSourceCodeVo();
        MEMO_MAP.put(baseSourceCodeVo.toUniqueMethodStr(), new ArrayList<>(Arrays.asList(baseSourceCodeVo)));
        // It fetches the methods in dao in order to search the code called update/insert sql
        callHierarchy(resultList, transactionalMethodDto, baseSourceCodeVo, packagePrefixList, new AtomicInteger(0));
        return resultList;
    }

    private void callHierarchy(List<RelatedDaoCodeDto> resultList, TransactionalMethodDto transactionalMethodDto,
            SourceCodeVo sourceCodeVo, List<String> packagePrefixList, AtomicInteger seqAtomicInt) {
        List<SourceCodeVo> childSourceCodes = MEMO_MAP.computeIfAbsent(sourceCodeVo.toUniqueMethodStr(),
                f -> sourceCodeFetchDao.fetchCalledMethodsByMethod(transactionalMethodDto.getSourceFolderPath(),
                        sourceCodeVo, packagePrefixList));
        if (CollectionUtils.isEmpty(childSourceCodes)) {
            return;
        }
        for (SourceCodeVo childSourceCodeVo : childSourceCodes) {
            int updateMethodCnt = hasUpdateMethod(transactionalMethodDto.getSourceFolderPath(), childSourceCodeVo);
            if (updateMethodCnt > 0) {
                resultList.add(RelatedDaoCodeDto.builder()
                        .transactionalMethodId(transactionalMethodDto.getTransactionalMethodId())
                        .seq(seqAtomicInt.incrementAndGet())
                        .relatedDaoCodeVo(childSourceCodeVo)
                        .updateMethodCnt(updateMethodCnt)
                        .build());
            }
            callHierarchy(resultList, transactionalMethodDto, childSourceCodeVo, packagePrefixList, seqAtomicInt);
        }
    }

    private int hasUpdateMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo) {
        int cnt = 0;
        for (TargetMethodEnum methods : TargetMethodEnum.values()) {
            cnt = cnt + sourceCodeFetchDao.hasMethod(sourceFolderPath, sourceCodeVo, methods.getMethods());
        }
        return cnt;
    }
}
