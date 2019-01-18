package com.github.morihara.transactional.sercher.batch.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.sercher.batch.config.MainConfig;
import com.github.morihara.transactional.sercher.batch.executor.InvestigateApplicationRunner;
import com.github.morihara.transactional.sercher.batch.executor.OutputApplicationRunner;

@SpringBootApplication
@Import({MainConfig.class})
public class TrnSrch {

    public static void main(String[] args) {
        SpringApplication.run(InvestigateApplicationRunner.class, args);
        SpringApplication.run(OutputApplicationRunner.class, args);
    }

}
