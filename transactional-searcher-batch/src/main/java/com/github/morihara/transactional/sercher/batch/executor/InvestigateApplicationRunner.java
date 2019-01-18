package com.github.morihara.transactional.sercher.batch.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InvestigateApplicationRunner implements CommandLineRunner {

    @Override
    public void run(String... arg0) throws Exception {
        log.info("----start----");
 
        log.info("----end----");
    }

}
