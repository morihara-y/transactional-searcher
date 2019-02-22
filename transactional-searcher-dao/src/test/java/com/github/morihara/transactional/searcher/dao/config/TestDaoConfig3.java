package com.github.morihara.transactional.searcher.dao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.morihara.transactional.searcher.dao.rdb.RelatedDaoCodeDao;
import com.github.morihara.transactional.searcher.dao.rdb.RelatedDaoCodeDaoImpl;
import com.github.morihara.transactional.searcher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.searcher.dao.rdb.TransactionalMethodDaoImpl;

@Configuration
public class TestDaoConfig3 {
    @Bean
    @Autowired
    public RelatedDaoCodeDao relatedDaoCodeDao(JdbcTemplate jdbc) {
        return new RelatedDaoCodeDaoImpl(jdbc);
    }
}
