package com.github.morihara.transactional.searcher.dto.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BeanDefinitionVo {
    private String beanName;
    private String interfaceClassQualifiedName;
    private String implClassQualifiedName;
    private String qualifierName;
    // TODO: It should be supported for a qualifier of each param
}
