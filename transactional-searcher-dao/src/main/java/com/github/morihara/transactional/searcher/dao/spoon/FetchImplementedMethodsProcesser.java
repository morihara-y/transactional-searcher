package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.github.morihara.transactional.searcher.dao.util.MethodsUtil;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import lombok.RequiredArgsConstructor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

@RequiredArgsConstructor
public class FetchImplementedMethodsProcesser {
    private final Class<?> annotationType;
    private final Map<String, MetadataResourceVo> metadataResourceMap;

    private void process(CtClass<CtElement> element, List<SourceCodeVo> result) {
        if (!isTargetClass(element)) {
            return;
        }
        for (CtMethod<?> method : element.getMethods()) {
            if (!method.isPublic() || !method.hasAnnotation(Override.class)) {
                continue;
            }
            result.add(MethodsUtil.makeSourceCodeVo(element.getPackage().getQualifiedName(),
                    element.getSimpleName(), method));
        }
    }

    List<SourceCodeVo> executeSpoon() {
        List<SourceCodeVo> result = new ArrayList<>();
        for (Map.Entry<String, MetadataResourceVo> entry : this.metadataResourceMap.entrySet()) {
            this.process(entry.getValue().getElement(), result);
        }
        return result;
    }

    private boolean isTargetClass(CtClass<CtElement> element) {
        for (CtAnnotation<?> annotation : element.getAnnotations()) {
            if (annotation.getType().getQualifiedName().equals(annotationType.getName())) {
                return true;
            }
        }
        return false;
    }
}
