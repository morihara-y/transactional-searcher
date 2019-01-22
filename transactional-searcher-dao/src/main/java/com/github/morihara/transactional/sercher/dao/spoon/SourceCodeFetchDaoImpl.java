package com.github.morihara.transactional.sercher.dao.spoon;

import java.io.File;
import java.util.List;

import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import spoon.Launcher;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFolder;

public class SourceCodeFetchDaoImpl implements SourceCodeFetchDao {

    @Override
    public List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath) {
        Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.addInputResource(new FileSystemFolder(new File(sourceFolderPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchPackagesProcesser().executeSpoon(queueProcessingManager);
    }

    @Override
    public List<SourceCodeVo> fetchMethodsByPackageName(String sourceFolderPath, String packageName) {
        String packageNamePath = makePackagePath(sourceFolderPath, packageName);
        Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.addInputResource(new FileSystemFolder(new File(packageNamePath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchMethodsProcesser().executeSpoon(queueProcessingManager, packageName);
    }

    @Override
    public boolean hasUpdateSql(SourceCodeVo sourceCodeVo) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasTransactionalAnnotation(SourceCodeVo sourceCodeVo) {
        // TODO Auto-generated method stub
        return false;
    }

    private String makePackagePath(String sourceFolderPath, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceFolderPath);
        sb.append("/");
        sb.append(packageName.replaceAll("\\.", "/"));
        return sb.toString();
    }
}
