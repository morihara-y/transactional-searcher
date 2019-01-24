package com.github.morihara.transactional.sercher.enumerate;

import lombok.Getter;

public enum DevelopStatusEnum {
    DEVELOPED("DEVELOPED"),
    IS_REQUIRED("IS_REQUIRED"),
    IS_NOT_REQUIRED("IS_NOT_REQUIRED");

    @Getter
    private final String text;

    private DevelopStatusEnum(final String text) {
        this.text = text;
    }

    public static DevelopStatusEnum getEnum(String str) {
        for (DevelopStatusEnum value : values()) {
            if (value.getText().equals(str)) {
                return value;
            }
        }
        throw new IllegalArgumentException("undefined: " + str);
    }
}
