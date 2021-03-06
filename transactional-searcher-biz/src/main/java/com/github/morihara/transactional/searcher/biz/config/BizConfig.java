package com.github.morihara.transactional.searcher.biz.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.github.morihara.transactional.searcher.dao.config.DaoConfig;

@Configuration
@ComponentScan({"com.github.morihara.transactional.searcher.biz"})
@Import(value = {
        DaoConfig.class })
public class BizConfig {
}
