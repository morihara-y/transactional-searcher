package com.github.morihara.transactional.sercher.batch.executor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.github.morihara.transactional.sercher.biz.service.InvestigationService;
import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InvestigateApplicationRunner implements CommandLineRunner {
    private final InvestigationService investigationService;

    @Override
    public void run(String... arg0) throws Exception {
        log.info("----start investigation----");
        String sourceFolderPath = arg0[0];
        log.info("sourceFolderPath: {}", sourceFolderPath);
        List<String> packageNames = investigationService.getPackageNames(sourceFolderPath);
        List<TransactionalMethodDto> transactionalMethodDtos =
                investigationService.getTopLayerWithoutRegistered(sourceFolderPath, packageNames);
        List<TransactionalMethodDto> results = new ArrayList<>();
        for (TransactionalMethodDto transactionalMethodDto : transactionalMethodDtos) {
            if (investigationService.isManagedTransactional(transactionalMethodDto) 
                    || !investigationService.isRDBUpdateService(transactionalMethodDto)) {
                continue;
            }
            results.add(transactionalMethodDto);
        }
        investigationService.updateResult(results);
        log.info("----end investigation----");
    }

}
