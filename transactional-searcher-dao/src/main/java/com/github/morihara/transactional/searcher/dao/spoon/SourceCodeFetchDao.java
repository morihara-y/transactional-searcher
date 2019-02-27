package com.github.morihara.transactional.searcher.dao.spoon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

public interface SourceCodeFetchDao {
    void walkJarFile(String jarPath, Map<String, MetadataResourceVo> metadataResourceMap,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap);

    List<SourceCodeVo> fetchPublicMethodsByAnotation(Annotation annotation,
            Map<String, MetadataResourceVo> metadataResourceMap);

    List<SourceCodeVo> fetchCalledMethodsByMethod(SourceCodeVo sourceCodeVo,
            List<String> daoPackageNames, Map<String, MetadataResourceVo> metadataResourceMap,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap);

    int hasMethod(SourceCodeVo sourceCodeVo, Method[] methods,
            Map<String, MetadataResourceVo> metadataResourceMap);

    boolean hasAnnotation(SourceCodeVo sourceCodeVo, Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap);
}
