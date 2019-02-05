package com.github.morihara.transactional.sercher.batch.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.github.morihara.transactional.sercher.batch.config.MainConfig;
import com.github.morihara.transactional.sercher.batch.executor.InvestigateApplicationRunner;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Import({MainConfig.class})
@Slf4j
public class TrnSrch {

    public static void main(String[] args) {
        Options opt = prepareOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmdLine = parser.parse(opt, args);
            String sourceFolderPath = cmdLine.getOptionValue("s");
            String packagePrefixArgs = cmdLine.getOptionValue("p");
            String isRebuildMode = cmdLine.getOptionValue("r");
            SpringApplication.run(InvestigateApplicationRunner.class, sourceFolderPath, packagePrefixArgs,
                    isRebuildMode);
        } catch (ParseException e) {
            log.error("error: ", e);
        }
    }

    private static Options prepareOptions() {
        Options opt = new Options();
        opt.addOption(Option.builder("s")
                .required()
                .hasArg()
                .longOpt("source-folder-path")
                .desc("set source folder path e.g. src/main/java")
                .build());
        opt.addOption(Option.builder("p")
                .required()
                .hasArg()
                .longOpt("package-prefix-args")
                .desc("set target packages")
                .build());
        opt.addOption(Option.builder("r")
                .longOpt("rebuild-mode")
                .desc("research all method and update all data")
                .build());
        return opt;
    }
}
