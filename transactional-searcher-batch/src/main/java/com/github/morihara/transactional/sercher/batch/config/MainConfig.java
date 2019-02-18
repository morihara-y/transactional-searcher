package com.github.morihara.transactional.sercher.batch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.sercher.biz.config.BizConfig;
import com.github.morihara.transactional.sercher.dao.config.DaoConfig;

@Configuration
@Import(value = { BizConfig.class,
        DaoConfig.class })
public class MainConfig {
}
