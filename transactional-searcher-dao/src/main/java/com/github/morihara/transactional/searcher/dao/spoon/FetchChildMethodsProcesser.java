package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.extern.slf4j.Slf4j;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.QueueProcessingManager;

@Slf4j
public class FetchChildMethodsProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final SourceCodeVo sourceCodeVo;
    private final List<String> packageNamePrefixList;
    private Set<SourceCodeVo> result;

    public FetchChildMethodsProcesser(SourceCodeVo sourceCodeVo, List<String> filterPackagePrefixList) {
        super();
        if (CollectionUtils.isEmpty(filterPackagePrefixList)) {
            throw new IllegalArgumentException("filterPackagePrefixList is required");
        }
        this.sourceCodeVo = sourceCodeVo;
        this.packageNamePrefixList = filterPackagePrefixList;
    }

    @Override
    public void process(CtClass<CtElement> element) {
        CtMethod<?> method = MethodsUtil.fetchTargetMethod(element, this.sourceCodeVo, getFactory());
        if (Objects.isNull(method)) {
            return;
        }
        updateFetchingChildMethods(method);
    }

    List<SourceCodeVo> executeSpoon(QueueProcessingManager queueProcessingManager) {
        this.result = new HashSet<>();
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return new ArrayList<>(this.result);
    }

    @SuppressWarnings("rawtypes")
    private void updateFetchingChildMethods(CtMethod<?> method) {
        List<CtExecutableReference<?>> executableMethods = MethodsUtil.fetchChildExecutableMethods(method);
        for (CtExecutableReference executableMethod : executableMethods) {
            if (canIgnoreMethod(executableMethod)) {
                continue;
            }
            if (isTargetPackage(executableMethod)) {
                result.add(MethodsUtil.makeSourceCodeVo(executableMethod));
            }
        }
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
