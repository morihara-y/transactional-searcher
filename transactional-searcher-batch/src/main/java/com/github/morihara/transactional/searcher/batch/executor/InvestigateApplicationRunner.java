package com.github.morihara.transactional.searcher.batch.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.searcher.batch.config.MainConfig;
import com.github.morihara.transactional.searcher.biz.service.InvestigationService;
import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Import({MainConfig.class})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InvestigateApplicationRunner implements CommandLineRunner {
    private final InvestigationService investigationService;

    @Override
    public void run(String... arg0) throws Exception {
        log.info("----start investigation----");
        String sourceFolderPath = arg0[0];
        String packagePrefixArgs = arg0[1];
        log.info("sourceFolderPath: {}", sourceFolderPath);
        log.info("packagePrefixArgs: {}", packagePrefixArgs);
        List<String> packagePrefixList = Arrays.asList(packagePrefixArgs);
        List<String> packageNames = investigationService.getPackageNames(sourceFolderPath);
        List<TransactionalMethodDto> transactionalMethodDtos =
                investigationService.getTopLayerWithoutRegistered(sourceFolderPath, packageNames);
        List<TransactionalMethodDto> results = new ArrayList<>();
        for (TransactionalMethodDto transactionalMethodDto : transactionalMethodDtos) {
            if (investigationService.isManagedTransactional(transactionalMethodDto) 
                    || !investigationService.isRDBUpdateService(sourceFolderPath,
                            transactionalMethodDto, packagePrefixList)) {
                continue;
            }
            results.add(transactionalMethodDto);
        }
        investigationService.updateResult(results);
        log.info("----end investigation----");
    }

}
