package com.github.morihara.transactional.sercher.dao.rdb;

import java.util.List;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;

public interface TransactionalMethodDao {
    void insert(TransactionalMethodDto transactionalMethodDto);

    void update(TransactionalMethodDto transactionalMethodDto);

    List<TransactionalMethodDto> getAll();

    List<TransactionalMethodDto> fetchByPackageName(String paclageName);
}
