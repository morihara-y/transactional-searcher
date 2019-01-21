package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtPackage;
import spoon.support.QueueProcessingManager;

public class FetchPackagesProcesser extends AbstractProcessor<CtPackage> {
    
    private final Set<String> packageNames = new HashSet<>();

    @Override
    public void process(CtPackage element) {
        if (element.getTypes().isEmpty()) {
            return;
        }
        packageNames.add(element.getQualifiedName());
    }
    
    List<String> executeSpoon(QueueProcessingManager queueProcessingManager) {
        queueProcessingManager.addProcessor(this);
        queueProcessingManager.process(queueProcessingManager.getFactory().Package().getAll());
        return new ArrayList<>(packageNames);
    }

}
