package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

@RequiredArgsConstructor
@Slf4j
public class FetchBeansFromConfigProcesser {
    private final Class<?>[] annotationTypes;
    private final Map<String, List<BeanDefinitionVo>> result;
    private final Map<String, MetadataResourceVo> metadataResourceMap;

    private static final Set<String> SCANED_BEAN_NAME = new HashSet<>();

    private void process(CtClass<CtElement> element) {
        if (!isTargetClass(element)) {
            return;
        }
        for (CtMethod<?> method : element.getMethods()) {
            Optional<BeanDefinitionVo> beanDefinitionVo = makeBeanDefinitionVo(method);
            if (!beanDefinitionVo.isPresent()) {
                continue;
            }
            String interfaceClassPath = method.getType().getQualifiedName();
            this.result.computeIfAbsent(interfaceClassPath, key -> new ArrayList<>());
            this.result.get(interfaceClassPath).add(beanDefinitionVo.get());
            SCANED_BEAN_NAME.add(method.getSimpleName());
        }
    }

    void executeSpoon() {
        for (Map.Entry<String, MetadataResourceVo> entry : this.metadataResourceMap.entrySet()) {
            this.process(entry.getValue().getElement());
        }
    }

    private boolean isTargetClass(CtClass<CtElement> element) {
        for (CtAnnotation<?> annotation : element.getAnnotations()) {
            for (Class<?> annotationType : this.annotationTypes) {
                if (annotation.getType().getQualifiedName().equals(annotationType.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Optional<BeanDefinitionVo> makeBeanDefinitionVo(CtMethod<?> method) {
        if (canIgnoreMethod(method)) {
            return Optional.empty();
        }

        CtTypeReference<?> interfaceClassType = method.getType();
        List<CtExecutableReference<?>> executableMethods = MethodsUtil.fetchChildExecutableMethods(method);
        Optional<String> implClassQualifiedName = getImplClassQualifiedName(interfaceClassType, executableMethods);
        if (!implClassQualifiedName.isPresent()) {
            return Optional.empty();
        }

        Qualifier qualifier = method.getAnnotation(Qualifier.class);
        return Optional.of(BeanDefinitionVo.builder()
                .beanName(method.getSimpleName())
                .interfaceClassQualifiedName(interfaceClassType.getQualifiedName())
                .implClassQualifiedName(implClassQualifiedName.get())
                .qualifierName(Objects.nonNull(qualifier) ? qualifier.value() : StringUtils.EMPTY)
                .build());
    }

    private boolean canIgnoreMethod(CtMethod<?> method) {
        if (SCANED_BEAN_NAME.contains(method.getSimpleName())) {
            log.debug("it has already scaned. beanName: {}", method.getSimpleName());
            return true;
        }
        if (!method.isPublic() || !method.hasAnnotation(Bean.class)) {
            log.warn("it is not a method for the bean creation. methodName: {}", method.getSimpleName());
            return true;
        }
        return false;
    }

    private Optional<String> getImplClassQualifiedName(CtTypeReference<?> interfaceClassType,
            List<CtExecutableReference<?>> executableMethods) {
        for (CtExecutableReference<?> executableMethod : executableMethods) {
            if (!executableMethod.isConstructor()) {
                continue;
            }
            CtTypeReference<?> constructorTypeRef = executableMethod.getDeclaringType();
            Set<CtTypeReference<?>> constructorsInterfaceClasses = constructorTypeRef.getSuperInterfaces();
            if (constructorsInterfaceClasses.contains(interfaceClassType)) {
                return Optional.of(constructorTypeRef.getQualifiedName());
            }
        }
        log.warn("it is not created the bean. interfaceClassPath: {}", interfaceClassType);
        return Optional.empty();
    }
}
