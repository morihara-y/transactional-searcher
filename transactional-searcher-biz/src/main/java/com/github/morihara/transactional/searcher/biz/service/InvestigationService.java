package com.github.morihara.transactional.searcher.biz.service;

import java.util.List;
import java.util.Map;

import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;

public interface InvestigationService {
    void updateMetadetaResourceMap(String jarPath, String jarName, Map<String, MetadataResourceVo> metadataResourceMap);

    void makeBeanDefinitionMap(Map<String, List<BeanDefinitionVo>> beanDefinitionMap,
            Map<String, MetadataResourceVo> metadataResourceMap);

    List<TransactionalMethodDto> getTopLayerServiceMethods(Map<String, MetadataResourceVo> metadataResourceMap);

    TransactionalMethodDto updateRelatedDaoCodes(TransactionalMethodDto transactionalMethodDto,
            List<String> packagePrefixList, Map<String, MetadataResourceVo> metadataResourceMap);

    TransactionalMethodDto updateDevelopStatus(TransactionalMethodDto transactionalMethodDto,
            Map<String, MetadataResourceVo> metadataResourceMap);

    void updateResult(List<TransactionalMethodDto> transactionalMethodDtos);
}
