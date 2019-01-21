package com.github.morihara.transactional.sercher.dto.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SourceCodeVo {
    private String packageName;
    private String className;
    private String methodName;
    private String methodParam;
    private String methodType;
}
