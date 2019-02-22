package com.github.morihara.transactional.searcher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

public interface SourceCodeFetchDao {
    List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath);

    List<SourceCodeVo> fetchMethodsByPackageName(String sourceFolderPath, String packageName);

    List<SourceCodeVo> fetchCalledMethodsByMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo,
            List<String> packagePrefixList);

    int hasMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo, Method[] methods);

    boolean hasAnnotation(String sourceFolderPath, SourceCodeVo sourceCodeVo, Class<?> annotationType);

    void updateBeanDefinitionMap(String sourceFolderPath, String springConfigPath, List<String> packagePrefixList,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap);
}
