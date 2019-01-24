package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
public class CountTargetMethodsProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final SourceCodeVo sourceCodeVo;
    private final Method[] fetchingMethods;
    private int resultCnt;

    @Override
    public void process(CtClass<CtElement> element) {
        CtMethod<?> method = MethodsUtil.fetchTargetMethod(element, this.sourceCodeVo, getFactory());
        if (Objects.isNull(method)) {
            return;
        }
        resultCnt = resultCnt + countFetchingMethods(method);
    }

    int executeSpoon(QueueProcessingManager queueProcessingManager) {
        this.resultCnt = 0;
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return this.resultCnt;
    }

    @SuppressWarnings("rawtypes")
    private int countFetchingMethods(CtMethod<?> method) {
        int resultCnt = 0;
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
                if (actualMethod != null && hasMethod(actualMethod)) {
                    resultCnt++;
                }
            }
        }
        return resultCnt;
    }

    private boolean hasMethod(Method target) {
        for (Method m : this.fetchingMethods) {
            if (target.equals(m)) {
                return true;
            }
        }
        return false;
    }
}
