package com.github.morihara.transactional.sercher.dao.rdb;

import java.util.List;
import java.util.UUID;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;

public interface TransactionalMethodDao {
    void insert(TransactionalMethodDto transactionalMethodDto);

    void updateDevelopStatus(TransactionalMethodDto transactionalMethodDto);

    void delete(UUID transactionalMethodId);

    List<TransactionalMethodDto> fetchByPackageName(String packageName);
}
