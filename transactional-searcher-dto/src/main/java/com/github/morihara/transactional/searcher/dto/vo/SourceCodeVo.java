package com.github.morihara.transactional.searcher.dto.vo;

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

    public String toUniqueMethodStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.packageName);
        sb.append(".");
        sb.append(this.className);
        sb.append(".");
        sb.append(this.methodName);
        sb.append("(");
        sb.append(this.methodParam);
        sb.append(")");
        return sb.toString();
    }
}
