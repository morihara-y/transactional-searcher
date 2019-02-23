package com.github.morihara.transactional.searcher.biz.source.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.searcher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.searcher.service.enumrate.TargetMethodEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallHierarchyServiceImpl implements CallHierarchyService {
    private final SourceCodeFetchDao sourceCodeFetchDao;

    private static final Map<String, List<SourceCodeVo>> MEMO_MAP = new HashMap<>();

    @Override
    public TransactionalMethodDto fetchRelatedDaoCodesByCallHierarchy(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList) {
        List<RelatedDaoCodeDto> relatedDaoCodes = new ArrayList<>();
        // It fetches the methods in dao in order to search the code called update/insert sql
        callHierarchy(relatedDaoCodes, transactionalMethodDto, transactionalMethodDto.getSourceCodeVo(), packagePrefixList,
                new AtomicInteger(0));
        transactionalMethodDto.setRelatedDaoCodes(relatedDaoCodes);
        return transactionalMethodDto;
    }

    private void callHierarchy(List<RelatedDaoCodeDto> resultList, TransactionalMethodDto transactionalMethodDto,
            SourceCodeVo sourceCodeVo, List<String> packagePrefixList, AtomicInteger seqAtomicInt) {
        log.debug("start callHierarchy. sourceCodeVo: {}", sourceCodeVo.toUniqueMethodStr());
        int updateMethodCnt = hasUpdateMethod(transactionalMethodDto.getSourceFolderPath(), sourceCodeVo);
        if (updateMethodCnt > 0) {
            resultList.add(RelatedDaoCodeDto.builder()
                    .transactionalMethodId(transactionalMethodDto.getTransactionalMethodId())
                    .seq(seqAtomicInt.incrementAndGet())
                    .relatedDaoCodeVo(sourceCodeVo)
                    .updateMethodCnt(updateMethodCnt)
                    .build());
        }

        List<SourceCodeVo> childSourceCodes = MEMO_MAP.computeIfAbsent(sourceCodeVo.toUniqueMethodStr(),
                f -> sourceCodeFetchDao.fetchCalledMethodsByMethod(transactionalMethodDto.getSourceFolderPath(),
                        sourceCodeVo, packagePrefixList));
        if (CollectionUtils.isEmpty(childSourceCodes)) {
            log.debug("finish callHierarchy. last sourceCodeVo: {}", sourceCodeVo.toUniqueMethodStr());
            return;
        }
        for (SourceCodeVo childSourceCodeVo : childSourceCodes) {
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
