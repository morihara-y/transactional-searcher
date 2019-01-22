package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.support.QueueProcessingManager;

public class FetchMethodsProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private String packageName;
    private final Set<SourceCodeVo> sourceCodeVoSet = new HashSet<>();

    @Override
    public void process(CtClass<CtElement> element) {
        if (element.isAnonymous()) {
            return;
        }
        Set<CtMethod<?>> methods = element.getMethods();
        String className = element.getQualifiedName();
        for (CtMethod<?> method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            SourceCodeVo sourceCodeVo = makeSourceCodeVo(className, method);
            sourceCodeVoSet.add(sourceCodeVo);
        }
    }

    List<SourceCodeVo> executeSpoon(QueueProcessingManager queueProcessingManager,
            String packageName) {
        this.packageName = packageName;
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return new ArrayList<>(sourceCodeVoSet);
    }

    private SourceCodeVo makeSourceCodeVo(String className, CtMethod<?> method) {
        return SourceCodeVo.builder()
                .packageName(packageName)
                .className(className)
                .methodName(method.getSimpleName())
                .methodParam(makeParamStr(method))
                .methodType(method.getType().getQualifiedName())
                .build();
    }

    private String makeParamStr(CtMethod<?> method) {
        List<CtParameter<?>> params = method.getParameters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getType().getQualifiedName());
            if (i < params.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
