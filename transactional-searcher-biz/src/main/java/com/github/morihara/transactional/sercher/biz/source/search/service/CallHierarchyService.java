package com.github.morihara.transactional.sercher.biz.source.search.service;

import java.util.List;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;

public interface CallHierarchyService {
    List<RelatedDaoCodeDto> fetchRelatedDaoCodesByCallHierarchy(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList);
}
