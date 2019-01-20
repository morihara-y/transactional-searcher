package com.github.morihara.transactional.sercher.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.sercher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private final TransactionalMethodDao transactionalMethodDao;
    private final SourceCodeFetchDao sourceCodeFetchDao;
    
    @Override
    public List<String> getPackageNames(String sourceFolderPath) {
        return sourceCodeFetchDao.fetchPackagesBySourceFolderPath(sourceFolderPath);
    }

    @Override
    public List<TransactionalMethodDto> getTopLayerWithoutRegistered(List<String> packageNames) {
        List<TransactionalMethodDto> results = new ArrayList<>();
        for (String packageName : packageNames) {
            List<SourceCodeVo> topLayerMethods =
                    sourceCodeFetchDao.fetchMethodsByPackageName(packageName);
            results.addAll(topLayerMethods.stream()
                    .filter(topLayerMethod -> !transactionalMethodDao.fetchByMethod(topLayerMethod)
                            .isPresent())
                    .map(this::makeNewTransactionalMethodDto)
                    .collect(Collectors.toList()));
        }
        return results;
    }

    @Override
    public boolean isRDBUpdateService(TransactionalMethodDto transactionalMethodDto) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isManagedTransactional(TransactionalMethodDto transactionalMethodDto) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void updateResult(TransactionalMethodDto transactionalMethodDto) {
        transactionalMethodDao.insert(transactionalMethodDto);
    }

    @Override
    public void exportCSV(List<String> packageNames) {
        // TODO Auto-generated method stub

    }

    private TransactionalMethodDto makeNewTransactionalMethodDto(SourceCodeVo sourceCodeVo) {
        return TransactionalMethodDto.builder()
                .transactionMethodId(UUID.randomUUID())
                .sourceCodeVo(sourceCodeVo)
                .isDeveloped(false)
                .build();
    }

}
