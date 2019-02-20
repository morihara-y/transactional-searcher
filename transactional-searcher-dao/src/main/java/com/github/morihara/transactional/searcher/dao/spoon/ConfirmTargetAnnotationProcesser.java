package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.List;
import java.util.Objects;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
public class ConfirmTargetAnnotationProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final SourceCodeVo sourceCodeVo;
    private final Class<?> annotationType;
    private boolean result;

    @Override
    public void process(CtClass<CtElement> element) {
        CtMethod<?> method = MethodsUtil.fetchTargetMethod(element, this.sourceCodeVo, getFactory());
        if (Objects.isNull(method)) {
            return;
        }
        List<CtAnnotation<?>> annotations = method.getAnnotations();
        for (CtAnnotation<?> annotation : annotations) {
            if (annotation.getType().getQualifiedName().equals(annotationType.getName())) {
                result = true;
                return;
            }
        }
    }

    boolean executeSpoon(QueueProcessingManager queueProcessingManager) {
        this.result = false;
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
        return this.result;
    }
}
