package com.github.morihara.transactional.searcher.dto.vo;

import lombok.Builder;
import lombok.Getter;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

@Builder
@Getter
public class MetadataResourceVo {
    private String classQualifierName;
    private CtClass<CtElement> element;
    private boolean isInterface;
}
