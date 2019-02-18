package com.github.morihara.transactional.sercher.batch.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.sercher.biz.config.BizConfig;

@Configuration
@Import(value = {
        BizConfig.class})
@ComponentScan({ "com.github.morihara.transactional.sercher.batch.executor" })
public class MainConfig {
}
