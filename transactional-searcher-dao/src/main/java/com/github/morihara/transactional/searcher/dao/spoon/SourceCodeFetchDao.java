package com.github.morihara.transactional.searcher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

public interface SourceCodeFetchDao {
    void walkJarFile(String jarPath, String jarName,
            Map<String, MetadataResourceVo> metadataResourceMap);

    void makeBeanDefinitionMap(Class<?>[] annotationTypes,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap,
            Map<String, MetadataResourceVo> metadataResourceMap);

    List<SourceCodeVo> fetchImplementedMethodsByClassAnotation(Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap);

    List<SourceCodeVo> fetchCalledMethodsByMethod(SourceCodeVo sourceCodeVo,
            List<String> filterPackagePrefixList,
            Map<String, MetadataResourceVo> metadataResourceMap);

    int hasMethod(SourceCodeVo sourceCodeVo, Method[] methods,
            Map<String, MetadataResourceVo> metadataResourceMap);

    boolean hasAnnotation(SourceCodeVo sourceCodeVo, Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap);
}
