package com.github.morihara.transactional.sercher.dao.rdb;

import java.util.List;
import java.util.UUID;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;

public interface RelatedDaoCodeDao {
    void batchUpsert(List<UUID> transactionalMethodIds, List<RelatedDaoCodeDto> relatedDaoCodes);

    List<RelatedDaoCodeDto> fetchByRelatedMethodId(UUID transactionalMethodId);
}
