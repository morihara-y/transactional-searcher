package com.github.morihara.transactional.searcher.biz.service;

import java.util.List;

import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;

public interface InvestigationService {
    List<String> getPackageNames(String sourceFolderPath);

    List<TransactionalMethodDto> getTopLayerWithoutRegistered(String sourceFolderPath,
            List<String> packageNames);

    boolean isRDBUpdateService(String sourceFolderPath,
            TransactionalMethodDto transactionalMethodDto, List<String> packagePrefixList);

    boolean isManagedTransactional(TransactionalMethodDto transactionalMethodDto);

    void updateResult(List<TransactionalMethodDto> transactionalMethodDtos);
}
