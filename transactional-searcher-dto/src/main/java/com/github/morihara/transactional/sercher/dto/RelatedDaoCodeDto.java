package com.github.morihara.transactional.sercher.dto;

import java.util.UUID;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RelatedDaoCodeDto {
    private UUID transactionMethodId;
    private int seq;
    private SourceCodeVo sourceCodeVo;
}
