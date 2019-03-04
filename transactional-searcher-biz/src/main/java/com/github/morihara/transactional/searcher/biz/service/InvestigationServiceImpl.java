package com.github.morihara.transactional.searcher.biz.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.morihara.transactional.searcher.biz.source.search.service.CallHierarchyService;
import com.github.morihara.transactional.searcher.dao.rdb.RelatedDaoCodeDao;
import com.github.morihara.transactional.searcher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.searcher.dto.RelatedDaoCodeDto;
import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.searcher.enumerate.DevelopStatusEnum;
import com.github.morihara.transactional.searcher.service.enumrate.TargetConfigAnnotationEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestigationServiceImpl implements InvestigationService {
    private final TransactionalMethodDao transactionalMethodDao;
    private final RelatedDaoCodeDao relatedDaoCodeDao;
    private final SourceCodeFetchDao sourceCodeFetchDao;
    private final CallHierarchyService callHierarchyService;

    @Override
    public void updateMetadetaResourceMap(String jarPath, String jarName,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        sourceCodeFetchDao.walkJarFile(jarPath, jarName, metadataResourceMap);
    }

    @Override
    public void makeBeanDefinitionMap(Map<String, List<BeanDefinitionVo>> beanDefinitionMap,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        sourceCodeFetchDao.makeBeanDefinitionMap(TargetConfigAnnotationEnum.getAllAnnotationTypes(), beanDefinitionMap,
                metadataResourceMap);
    }

    @Override
    public List<TransactionalMethodDto> getTopLayerServiceMethods(Map<String, MetadataResourceVo> metadataResourceMap) {
        List<SourceCodeVo> topLayerServiceMethods = sourceCodeFetchDao
                .fetchImplementedMethodsByClassAnotation(Service.class, metadataResourceMap);
        return topLayerServiceMethods.stream()
                .map(this::makeNewTransactionalMethodDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionalMethodDto updateRelatedDaoCodes(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList, Map<String, MetadataResourceVo> metadataResourceMap) {
        // TODO metadataResourceMap
        return callHierarchyService.fetchRelatedDaoCodesByCallHierarchy(transactionalMethodDto,
                packagePrefixList);
    }

    @Override
    public TransactionalMethodDto updateDevelopStatus(TransactionalMethodDto transactionalMethodDto,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        int hasUpdateMethod = transactionalMethodDto.getRelatedDaoCodes().size();
        boolean hasTransactional = sourceCodeFetchDao.hasAnnotation(transactionalMethodDto.getSourceCodeVo(),
                Transactional.class, metadataResourceMap);
        if (hasUpdateMethod < 2 && hasTransactional) {
            // it is bad implementation
            transactionalMethodDto.setDevelopStatus(DevelopStatusEnum.IS_NOT_REQUIRED);
            transactionalMethodDto.setErrorMessage("@Transactional is not necessary when updating only one table");
            return transactionalMethodDto;
        }
        if (hasUpdateMethod >= 2 && !hasTransactional) {
            // it is necessary to implement Transactional
            transactionalMethodDto.setDevelopStatus(DevelopStatusEnum.IS_REQUIRED);
            transactionalMethodDto.setErrorMessage("@Transactional is necessary");
            return transactionalMethodDto;
        }
        transactionalMethodDto.setDevelopStatus(DevelopStatusEnum.COLLECT_DEVELOPMENT);
        transactionalMethodDto.setErrorMessage(StringUtils.EMPTY);
        return transactionalMethodDto;
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
                .build();
    }
}
