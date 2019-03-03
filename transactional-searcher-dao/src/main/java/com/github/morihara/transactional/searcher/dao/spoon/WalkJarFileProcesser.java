package com.github.morihara.transactional.searcher.dao.spoon;

import java.util.Map;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import lombok.RequiredArgsConstructor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.support.QueueProcessingManager;

@RequiredArgsConstructor
public class WalkJarFileProcesser extends AbstractProcessor<CtClass<CtElement>> {
    private final Map<String, MetadataResourceVo> metadataResourceMap;

    @Override
    public void process(CtClass<CtElement> element) {
        if (element.isAnonymous()) {
            return;
        }
        metadataResourceMap.put(element.getQualifiedName(), MetadataResourceVo.builder()
                .classQualifierName(element.getQualifiedName())
                .element(element)
                .isInterface(element.isInterface())
                .build());
    }

    void executeSpoon(QueueProcessingManager queueProcessingManager) {
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Class().getAll());
    }
}
