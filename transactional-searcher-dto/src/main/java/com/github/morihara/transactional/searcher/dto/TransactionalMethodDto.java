package com.github.morihara.transactional.searcher.dto;

import java.util.List;
import java.util.UUID;

import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.searcher.enumerate.DevelopStatusEnum;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransactionalMethodDto {
    private UUID transactionalMethodId;
    private String sourceFolderPath;
    private SourceCodeVo sourceCodeVo;
    private DevelopStatusEnum developStatus;
    private int ticketNo;
    private List<RelatedDaoCodeDto> relatedDaoCodes;
}