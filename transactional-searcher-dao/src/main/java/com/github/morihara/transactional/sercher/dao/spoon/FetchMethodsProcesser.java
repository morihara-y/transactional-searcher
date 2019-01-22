package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.QueueProcessingManager;

public class FetchMethodsProcesser extends AbstractProcessor<CtInvocation<CtElement>> {
    private String packageName;
    private final Set<SourceCodeVo> sourceCodeVoSet = new HashSet<>();
    private final Set<String> methodStrs = new HashSet<>();

    @Override
    public void process(CtInvocation<CtElement> element) {
        CtExecutableReference<CtElement> executable = element.getExecutable();
        if (Objects.isNull(executable)) {
            return;
        }
        Method actualMethod = executable.getActualMethod();
        SourceCodeVo sourceCodeVo = makeSourceCodeVo(actualMethod);
        String sourceCodeStr = sourceCodeVo.toUniqueMethodStr();
        if (methodStrs.contains(sourceCodeStr)) {
            return;
        }
        sourceCodeVoSet.add(sourceCodeVo);
        methodStrs.add(sourceCodeStr);
    }

    List<SourceCodeVo> executeSpoon(QueueProcessingManager queueProcessingManager,
            String packageName) {
        this.packageName = packageName;
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return new ArrayList<>(sourceCodeVoSet);
    }

    private SourceCodeVo makeSourceCodeVo(Method method) {
        return SourceCodeVo.builder()
                .packageName(packageName)
                .className(method.getClass().getName())
                .methodName(method.getName())
                .methodParam(makeParamStr(method))
                .methodType(method.getReturnType().getName())
                .build();
    }

    private String makeParamStr(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.length; i++) {
            sb.append(classes[i].getName());
            if (i < classes.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
