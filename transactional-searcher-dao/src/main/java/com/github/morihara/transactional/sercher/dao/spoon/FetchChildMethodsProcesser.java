package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;

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
        List<CtElement> elements = method.getElements(new AbstractFilter<CtElement>(CtElement.class) {
            @Override
            public boolean matches(CtElement element) {
                return element instanceof CtAbstractInvocation;
            }
        });
        for (CtElement element : elements) {
            CtAbstractInvocation invocation = (CtAbstractInvocation)element;
            CtExecutableReference<?> executableMethod = invocation.getExecutable();
            if (executableMethod != null) {
                Method actualMethod = executableMethod.getActualMethod();
                if (actualMethod != null && isTargetPackage(actualMethod)) {
                    result.add(MethodsUtil.makeSourceCodeVo(actualMethod));
                }
            }
        }
    }
    
    private boolean isTargetPackage(Method method) {
        String packageName = method.getDeclaringClass().getPackage().getName();
        for (String packageNamePrefix : packageNamePrefixList) {
            if (packageName.startsWith(packageNamePrefix)) {
                return true;
            }
        }
        return false;
    }
}
