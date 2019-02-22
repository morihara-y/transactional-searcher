package com.github.morihara.transactional.searcher.dao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDao;
import com.github.morihara.transactional.searcher.dao.spoon.SourceCodeFetchDaoImpl;

@Configuration
@Import({ TestDaoConfig2.class, TestDaoConfig3.class })
public class TestDaoConfig1 {
    @Bean
    public SourceCodeFetchDao sourceCodeFetchDao() {
        return new SourceCodeFetchDaoImpl();
    }
}
