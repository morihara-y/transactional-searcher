package com.github.morihara.transactional.sercher.biz.service;

import java.util.List;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;

public interface InvestigationService {
    List<TransactionalMethodDto> getTopLayerWithoutTransactional(String sourceFolderPath);

    boolean isRDBUpdateService(TransactionalMethodDto transactionalMethodDto);

    boolean isManagedTransactional(TransactionalMethodDto transactionalMethodDto);

    void updateResult(TransactionalMethodDto transactionalMethodDto);

    void exportCSV(String sourceFolderPath);
}
