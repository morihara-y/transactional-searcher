package com.github.morihara.transactional.sercher.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.github.morihara.transactional.sercher.biz.source.search.service.CallHierarchyService;
import com.github.morihara.transactional.sercher.dao.rdb.RelatedDaoCodeDao;
import com.github.morihara.transactional.sercher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.sercher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.sercher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.sercher.enumerate.DevelopStatusEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private final TransactionalMethodDao transactionalMethodDao;
    private final RelatedDaoCodeDao relatedDaoCodeDao;
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
                    sourceCodeFetchDao.fetchMethodsByPackageName(sourceFolderPath, packageName);
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
        if (!relatedDaoCodes.isEmpty()) {
            transactionalMethodDto.setRelatedDaoCodes(relatedDaoCodes);
            return true;
        }
        return false;
    }

    @Override
    public boolean isManagedTransactional(TransactionalMethodDto transactionalMethodDto) {
        return transactionalMethodDto.getDevelopStatus() == DevelopStatusEnum.DEVELOPED
                || transactionalMethodDto.getDevelopStatus() == DevelopStatusEnum.IS_NOT_REQUIRED
                || sourceCodeFetchDao.hasAnnotation(transactionalMethodDto.getSourceCodeVo());
    }

    @Override
    public void updateResult(List<TransactionalMethodDto> transactionalMethodDtos) {
        transactionalMethodDao.batchInsert(transactionalMethodDtos);
        List<UUID> transactionalMethodIds = new ArrayList<>();
        List<RelatedDaoCodeDto> relatedDaoCodes = new ArrayList<>();
        for (TransactionalMethodDto transactionalMethodDto : transactionalMethodDtos) {
            transactionalMethodIds.add(transactionalMethodDto.getTransactionalMethodId());
            relatedDaoCodes.addAll(transactionalMethodDto.getRelatedDaoCodes());
        }
        relatedDaoCodeDao.batchUpsert(transactionalMethodIds, relatedDaoCodes);
    }

    private TransactionalMethodDto makeNewTransactionalMethodDto(SourceCodeVo sourceCodeVo) {
        return TransactionalMethodDto.builder()
                .transactionalMethodId(UUID.randomUUID())
                .sourceCodeVo(sourceCodeVo)
                .developStatus(DevelopStatusEnum.IS_REQUIRED)
                .build();
    }
}
