package com.github.morihara.transactional.sercher.dao.spoon;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

import spoon.Launcher;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;

public class SourceCodeFetchDaoImpl implements SourceCodeFetchDao {

    @Override
    public List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath) {
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFolder(new File(sourceFolderPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchPackagesProcesser().executeSpoon(queueProcessingManager);
    }

    @Override
    public List<SourceCodeVo> fetchMethodsByPackageName(String sourceFolderPath, String packageName) {
        String packageNamePath = makePackagePath(sourceFolderPath, packageName);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFolder(new File(packageNamePath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchMethodsProcesser(packageName).executeSpoon(queueProcessingManager);
    }

    @Override
    public int hasMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo, Method[] methods) {
        String classPath = makeClassPath(sourceFolderPath, sourceCodeVo);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new CountTargetMethodsProcesser(sourceCodeVo, methods).executeSpoon(queueProcessingManager);
    }

    @Override
    public boolean hasAnnotation(String sourceFolderPath, SourceCodeVo sourceCodeVo, Class<?> annotationType) {
        String classPath = makeClassPath(sourceFolderPath, sourceCodeVo);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new ConfirmTargetAnnotationProcesser(sourceCodeVo, annotationType).executeSpoon(queueProcessingManager);
    }

    private String makePackagePath(String sourceFolderPath, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceFolderPath);
        sb.append("/");
        sb.append(packageName.replaceAll("\\.", "/"));
        return sb.toString();
    }

    private String makeClassPath(String sourceFolderPath, SourceCodeVo sourceCodeVo) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceFolderPath);
        sb.append("/");
        sb.append(sourceCodeVo.getPackageName().replaceAll("\\.", "/"));
        sb.append("/");
        sb.append(sourceCodeVo.getClassName());
        sb.append(".java");
        return sb.toString();
    }

    private Launcher makeLauncherWithCommonArgs() {
        Launcher launcher = new Launcher();
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        return launcher;
    }
}
