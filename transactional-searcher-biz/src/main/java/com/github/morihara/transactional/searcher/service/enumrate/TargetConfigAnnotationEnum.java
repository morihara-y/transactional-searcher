package com.github.morihara.transactional.searcher.service.enumrate;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;

public enum TargetConfigAnnotationEnum {
    CONFIGURATION(Configuration.class);
    
    @Getter
    private final Class<?> annotationType;

    private TargetConfigAnnotationEnum(final Class<?> annotationType) {
        this.annotationType = annotationType;
    }
    
    public static Class<?>[] getAllAnnotationTypes() {
        Class<?>[] result = new Class<?>[values().length];
        for (int i = 0; i < values().length; i++) {
            result[i] = values()[i].getAnnotationType();
        }
        return result;
    }
}
