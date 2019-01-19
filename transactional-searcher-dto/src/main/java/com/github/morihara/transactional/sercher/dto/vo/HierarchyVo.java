package com.github.morihara.transactional.sercher.dto.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HierarchyVo {
    private int hierarchy;
    private int seq;
    private SourceCodeVo sourceCodeVo;
    private boolean isDao;
}
