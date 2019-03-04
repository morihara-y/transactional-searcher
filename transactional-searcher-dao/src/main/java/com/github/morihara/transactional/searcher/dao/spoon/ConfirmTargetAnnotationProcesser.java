package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.RequiredArgsConstructor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;

@RequiredArgsConstructor
public class ConfirmTargetAnnotationProcesser {
    private final Class<?> annotationType;
    private final Map<String, MetadataResourceVo> metadataResourceMap;
    private boolean result;

    private void process(CtMethod<?> method) {
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

    boolean executeSpoon(SourceCodeVo sourceCodeVo) {
        this.result = false;
        String classQualifierName = sourceCodeVo.getClassQualifierName();
        MetadataResourceVo metadata = this.metadataResourceMap.get(classQualifierName);
        this.process(MethodsUtil.fetchTargetMethod(metadata.getElement(), sourceCodeVo));
        return this.result;
    }
}
