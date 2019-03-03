package com.github.morihara.transactional.searcher.dao.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.morihara.transactional.searcher.dao.rdb.RelatedDaoCodeDao;
import com.github.morihara.transactional.searcher.dao.rdb.TransactionalMethodDao;
import com.github.morihara.transactional.searcher.dao.rdb.TransactionalMethodDaoImpl;

@Configuration
@Import({ TestDaoConfig3.class })
public class TestDaoConfig2 {
    @Bean
    @Autowired
    public TransactionalMethodDao transactionalMethodDao(JdbcTemplate jdbc, RelatedDaoCodeDao relatedDaoCodeDao) {
        return new TransactionalMethodDaoImpl(jdbc, relatedDaoCodeDao);
    }
}
