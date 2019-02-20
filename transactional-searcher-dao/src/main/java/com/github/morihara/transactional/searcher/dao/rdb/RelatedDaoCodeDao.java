package com.github.morihara.transactional.searcher.dao.rdb;

import java.util.List;
import java.util.UUID;

import com.github.morihara.transactional.searcher.dto.RelatedDaoCodeDto;

public interface RelatedDaoCodeDao {
    void batchUpsert(List<UUID> transactionalMethodIds, List<RelatedDaoCodeDto> relatedDaoCodes);

    List<RelatedDaoCodeDto> fetchByRelatedMethodId(UUID transactionalMethodId);
}
