package com.github.morihara.transactional.sercher.dao.rdb;

import java.util.Optional;
import java.util.UUID;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

public interface TransactionalMethodDao {
    void insert(TransactionalMethodDto transactionalMethodDto);

    void updateDevelopStatus(TransactionalMethodDto transactionalMethodDto);

    void delete(UUID transactionalMethodId);

    Optional<TransactionalMethodDto> fetchByMethod(SourceCodeVo sourceCodeVo);
}
