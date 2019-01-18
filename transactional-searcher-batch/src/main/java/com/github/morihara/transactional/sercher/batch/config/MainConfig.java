package com.github.morihara.transactional.sercher.batch.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.github.morihara.transactional.sercher.biz",
        "com.github.morihara.transactional.sercher.dao"})
public class MainConfig {
}
