package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
public class FetchMethodsProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final String packageName;
    private final Set<SourceCodeVo> sourceCodeVoSet = new HashSet<>();

    @Override
    public void process(CtClass<CtElement> element) {
        if (element.isAnonymous()) {
            return;
        }
        Set<CtMethod<?>> methods = element.getMethods();
        String className = element.getSimpleName();
        for (CtMethod<?> method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            SourceCodeVo sourceCodeVo = MethodsUtil.makeSourceCodeVo(this.packageName, className, method);
            sourceCodeVoSet.add(sourceCodeVo);
        }
    }

    List<SourceCodeVo> executeSpoon(QueueProcessingManager queueProcessingManager) {
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return new ArrayList<>(sourceCodeVoSet);
    }
}
