package com.github.morihara.transactional.sercher.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.biz.source.search.service.CallHierarchyService;
import com.github.morihara.transactional.sercher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.sercher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private final TransactionalMethodDao transactionalMethodDao;
    private final SourceCodeFetchDao sourceCodeFetchDao;
    private final CallHierarchyService callHierarchyService;
    
    @Override
    public List<String> getPackageNames(String sourceFolderPath) {
        return sourceCodeFetchDao.fetchPackagesBySourceFolderPath(sourceFolderPath);
    }

    @Override
    public List<TransactionalMethodDto> getTopLayerWithoutRegistered(String sourceFolderPath,
            List<String> packageNames) {
        List<TransactionalMethodDto> results = new ArrayList<>();
        for (String packageName : packageNames) {
            List<SourceCodeVo> topLayerMethods =
                    sourceCodeFetchDao.fetchMethodsByPackageName(packageName);
            results.addAll(topLayerMethods.stream()
                    .filter(topLayerMethod -> !transactionalMethodDao.fetchByMethod(sourceFolderPath, topLayerMethod)
                            .isPresent())
                    .map(this::makeNewTransactionalMethodDto)
                    .collect(Collectors.toList()));
        }
        return results;
    }

    @Override
    public boolean isRDBUpdateService(TransactionalMethodDto transactionalMethodDto) {
        List<RelatedDaoCodeDto> relatedDaoCodes =
                callHierarchyService.fetchRelatedDaoCodesByCallHierarchy(transactionalMethodDto);
        if (relatedDaoCodes.size() > 0) {
            transactionalMethodDto.setRelatedDaoCodes(relatedDaoCodes);
            return true;
        }
        return false;
    }

    @Override
    public boolean isManagedTransactional(TransactionalMethodDto transactionalMethodDto) {
        return sourceCodeFetchDao
                .hasTransactionalAnnotation(transactionalMethodDto.getSourceCodeVo());
    }

    @Override
    public void updateResult(TransactionalMethodDto transactionalMethodDto) {
        transactionalMethodDao.insert(transactionalMethodDto);
    }

    private TransactionalMethodDto makeNewTransactionalMethodDto(SourceCodeVo sourceCodeVo) {
        return TransactionalMethodDto.builder()
                .transactionalMethodId(UUID.randomUUID())
                .sourceCodeVo(sourceCodeVo)
                .isDeveloped(false)
                .build();
    }
}
