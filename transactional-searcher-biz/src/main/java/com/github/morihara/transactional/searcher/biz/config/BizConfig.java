package com.github.morihara.transactional.searcher.biz.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.searcher.dao.config.TestDaoConfig1;

@Configuration
@ComponentScan({"com.github.morihara.transactional.searcher.biz"})
@Import(value = {
        TestDaoConfig1.class })
public class BizConfig {
}
