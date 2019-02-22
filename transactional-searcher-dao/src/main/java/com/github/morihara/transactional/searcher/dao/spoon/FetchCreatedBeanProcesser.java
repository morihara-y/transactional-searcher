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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
@Slf4j
public class FetchCreatedBeanProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final List<String> packageNamePrefixList;
    private final Map<String, List<BeanDefinitionVo>> result;

    private static final Set<String> SCANED_CONFIG_PATHS = new HashSet<>();
    private static final Set<String> SCANED_BEAN_NAME = new HashSet<>();

    @Override
    public void process(CtClass<CtElement> element) {
        scanConfig(element);
    }

    void executeSpoon(QueueProcessingManager queueProcessingManager) {
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
    }

    private void scanConfig(CtClass<CtElement> configElement) {
        scanBeansFromConfig(configElement);
        if (configElement.hasAnnotation(ComponentScan.class)) {
            log.warn("@ComponentScan is not supported");
        }
        if (!configElement.hasAnnotation(Import.class)) {
            return;
        }
        for (CtClass<CtElement> childConfigElement : getImportedClasses(configElement)) {
            scanConfig(childConfigElement);
        }
    }

    private void scanBeansFromConfig(CtClass<?> configElement) {
        log.info("scan ConfigClass. conficClassPath: {}", configElement.getQualifiedName());
        Set<CtMethod<?>> methods = configElement.getMethods();
        for (CtMethod<?> method : methods) {
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

        if (!isTargetPackage(implClassQualifiedName.get())) {
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

    private List<CtClass<CtElement>> getImportedClasses(CtClass<?> configElement) {
        Import importAnnotation = configElement.getAnnotation(Import.class);
        Class<?>[] importedClasses = importAnnotation.value();
        List<CtClass<CtElement>> results = new ArrayList<>();
        for (Class<?> importedClass : importedClasses) {
            if (canIgnoreConfigClass(importedClass)) {
                continue;
            }
            results.add(getFactory().Class().get(importedClass));
            SCANED_CONFIG_PATHS.add(importedClass.getName());
        }
        return results;
    }

    private boolean canIgnoreConfigClass(Class<?> importedClass) {
        if (SCANED_CONFIG_PATHS.contains(importedClass.getName())) {
            log.debug("it has already scaned. importedClass: {}", importedClass.getName());
            return true;
        }
        if (!isTargetPackage(importedClass.getPackage().getName())) {
            log.debug("it is not target class. importedClass: {}", importedClass.getName());
            return true;
        }
        return false;
    }

    private boolean isTargetPackage(String packageName) {
        for (String packageNamePrefix : this.packageNamePrefixList) {
            if (packageName.startsWith(packageNamePrefix)) {
                return true;
            }
        }
        return false;
    }
}
