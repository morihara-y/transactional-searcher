package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
public class ConfirmUpdateMethodsProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final SourceCodeVo sourceCodeVo;
    private boolean result;

    private static final Method[] JDBC_UPDATE = MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "update");
    private static final Method[] JDBC_BATCH_UPDATE = MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "batchUpdate");

    @Override
    public void process(CtClass<CtElement> element) {
        if (result) {
            return;
        }
        CtMethod<?> method = fetchTargetMethod(element);
        if (Objects.isNull(method)) {
            return;
        }
        result = hasUpdateSql(method);
    }

    boolean executeSpoon(QueueProcessingManager queueProcessingManager) {
        this.result = false;
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return this.result;
    }

    private CtMethod<?> fetchTargetMethod(CtClass<CtElement> element) {
        String methodParamStr = sourceCodeVo.getMethodParam();
        if (StringUtils.isEmpty(methodParamStr)) {
            return element.getMethod(this.sourceCodeVo.getMethodName());
        }
        String[] methodTypeStrs = sourceCodeVo.getMethodParam().split(", ");
        int typeCnt = methodTypeStrs.length;
        CtTypeReference<?>[] methodTypes = new CtTypeReference<?>[typeCnt];
        for (int i = 0; i < typeCnt; i++) {
            methodTypes[i] = getFactory().Type().createReference(methodTypeStrs[i]);
        }
        return element.getMethod(this.sourceCodeVo.getMethodName(), methodTypes);
    }

    @SuppressWarnings("rawtypes")
    private boolean hasUpdateSql(CtMethod<?> method) {
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
                if (actualMethod != null && hasUpdateMethod(actualMethod)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasUpdateMethod(Method actualMethod) {
        return hasMethod(actualMethod, JDBC_UPDATE) || hasMethod(actualMethod, JDBC_BATCH_UPDATE);
    }

    private boolean hasMethod(Method target, Method[] methods) {
        for (Method m : methods) {
            if (target.equals(m)) {
                return true;
            }
        }
        return false;
    }
}
