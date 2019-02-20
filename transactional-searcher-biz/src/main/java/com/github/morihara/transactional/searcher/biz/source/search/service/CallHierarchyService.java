package com.github.morihara.transactional.searcher.biz.source.search.service;

import java.util.List;

import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;

public interface CallHierarchyService {
    TransactionalMethodDto fetchRelatedDaoCodesByCallHierarchy(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList);
}
