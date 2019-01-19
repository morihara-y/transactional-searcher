package com.github.morihara.transactional.sercher.dto;

import java.util.List;
import java.util.UUID;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransactionalMethodDto {
    private UUID transactionMethodId;
    private SourceCodeVo sourceCodeVo;
    private boolean isDeveloped;
    private List<RelatedDaoCodeDto> relatedDaoCodes;
}
