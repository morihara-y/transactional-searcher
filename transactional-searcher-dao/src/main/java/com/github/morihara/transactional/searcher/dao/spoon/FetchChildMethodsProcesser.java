package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import lombok.extern.slf4j.Slf4j;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;

@Slf4j
public class FetchChildMethodsProcesser {
    private final List<String> packageNamePrefixList;
    private final Map<String, MetadataResourceVo> metadataResourceMap;
    private Set<SourceCodeVo> result;

    public FetchChildMethodsProcesser(List<String> filterPackagePrefixList,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        super();
        if (CollectionUtils.isEmpty(filterPackagePrefixList)) {
            throw new IllegalArgumentException("filterPackagePrefixList is required");
        }
        this.packageNamePrefixList = filterPackagePrefixList;
        this.metadataResourceMap = metadataResourceMap;
    }

    private void process(CtMethod<?> method) {
        List<CtExecutableReference<?>> executableMethods =
                MethodsUtil.fetchChildExecutableMethods(method);
        for (CtExecutableReference<?> executableMethod : executableMethods) {
            if (canIgnoreMethod(executableMethod)) {
                continue;
            }
            if (isTargetPackage(executableMethod)) {
                result.add(MethodsUtil.makeSourceCodeVo(executableMethod));
            }
        }
    }

    List<SourceCodeVo> executeSpoon(SourceCodeVo sourceCodeVo,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        this.result = new HashSet<>();
        this.process(getImplementedMethod(sourceCodeVo, beanDefinitionMap));
        return new ArrayList<>(this.result);
    }

    private CtMethod<?> getImplementedMethod(SourceCodeVo sourceCodeVo,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        String classQualifierName = sourceCodeVo.getClassQualifierName();
        MetadataResourceVo metadata = this.metadataResourceMap.get(classQualifierName);
        if (!metadata.isInterface()) {
            return MethodsUtil.fetchTargetMethod(metadata.getElement(), sourceCodeVo); 
        }
        // get impled method from bean definition map  
        return null;
    }

    private boolean canIgnoreMethod(CtExecutableReference<?> executableMethod) {
        if (executableMethod == null) {
            log.warn("executableMethod is null.");
            return true;
        }
        if (executableMethod.getDeclaringType() == null) {
            log.warn("executableMethod.getDeclaringType is null. \nmethod: {}", executableMethod);
            return true;
        }
        if (executableMethod.getDeclaringType().getPackage() == null) {
            log.debug("It is an auto generated method. \nmethod: {}, class: {}", executableMethod,
                    executableMethod.getDeclaringType());
            return true;
        }
        if (executableMethod.getType() == null) {
            log.debug("It is an auto generated method. \nmethod: {}", executableMethod);
            return true;
        }
        return false;
    }

    private boolean isTargetPackage(CtExecutableReference<?> executableMethod) {
        String packageName = executableMethod.getDeclaringType().getPackage().getQualifiedName();
        for (String packageNamePrefix : packageNamePrefixList) {
            if (packageName.startsWith(packageNamePrefix)) {
                return true;
            }
        }
        return false;
    }
}
