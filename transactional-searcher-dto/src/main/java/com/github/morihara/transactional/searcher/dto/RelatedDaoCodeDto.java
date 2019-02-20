package com.github.morihara.transactional.searcher.dto;

import java.util.UUID;

import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RelatedDaoCodeDto {
    private UUID transactionalMethodId;
    private int seq;
    private SourceCodeVo relatedDaoCodeVo;
    private int updateMethodCnt;
}
