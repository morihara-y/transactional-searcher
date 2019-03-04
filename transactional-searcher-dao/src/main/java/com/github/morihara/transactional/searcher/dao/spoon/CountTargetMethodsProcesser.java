package com.github.morihara.transactional.searcher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AbstractFilter;

@RequiredArgsConstructor
public class CountTargetMethodsProcesser {
    private final Method[] fetchingMethods;
    private final Map<String, MetadataResourceVo> metadataResourceMap;
    private int resultCnt;

    private void process(CtMethod<?> method) {
        if (Objects.isNull(method)) {
            return;
        }
        countFetchingMethods(method);
    }

    int executeSpoon(SourceCodeVo sourceCodeVo) {
        this.resultCnt = 0;
        String classQualifierName = sourceCodeVo.getClassQualifierName();
        MetadataResourceVo metadata = this.metadataResourceMap.get(classQualifierName);
        this.process(MethodsUtil.fetchTargetMethod(metadata.getElement(), sourceCodeVo));
        return this.resultCnt;
    }

    @SuppressWarnings("rawtypes")
    private void countFetchingMethods(CtMethod<?> method) {
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
                    this.resultCnt++;
                }
            }
        }
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
