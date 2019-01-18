package com.github.morihara.transactional.sercher.batch.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.sercher.batch.config.MainConfig;
import com.github.morihara.transactional.sercher.batch.executor.InvestigateApplicationRunner;

@SpringBootApplication
@Import({MainConfig.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(InvestigateApplicationRunner.class, args);
    }

}