package com.github.morihara.transactional.searcher.dao.spoon;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import lombok.extern.slf4j.Slf4j;
import spoon.JarLauncher;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.support.QueueProcessingManager;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;

@Repository
@Slf4j
public class SourceCodeFetchDaoImpl implements SourceCodeFetchDao {

    @Override
    public void walkJarFile(String jarPath, Map<String, MetadataResourceVo> metadataResourceMap,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        JarLauncher launcher = new JarLauncher(jarPath);
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
    }

    @Override
    public List<SourceCodeVo> fetchPublicMethodsByAnotation(Annotation annotation,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<SourceCodeVo> fetchCalledMethodsByMethod(SourceCodeVo sourceCodeVo,
            List<String> daoPackageNames, Map<String, MetadataResourceVo> metadataResourceMap,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hasMethod(SourceCodeVo sourceCodeVo, Method[] methods,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasAnnotation(SourceCodeVo sourceCodeVo, Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        // TODO Auto-generated method stub
        return false;
    }

    public List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath) {
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFolder(new File(sourceFolderPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchPackagesProcesser().executeSpoon(queueProcessingManager);
    }

    public List<SourceCodeVo> fetchMethodsByPackageName(String sourceFolderPath, String packageName) {
        String packageNamePath = makePackagePath(sourceFolderPath, packageName);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFolder(new File(packageNamePath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchMethodsProcesser(packageName).executeSpoon(queueProcessingManager);
    }

    public List<SourceCodeVo> fetchCalledMethodsByMethod(String sourceFolderPath,
            SourceCodeVo sourceCodeVo, List<String> packagePrefixList) {
        String classPath = makeClassPath(sourceFolderPath, sourceCodeVo);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new FetchChildMethodsProcesser(sourceCodeVo, packagePrefixList).executeSpoon(queueProcessingManager);
    }

    public int hasMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo, Method[] methods) {
        String classPath = makeClassPath(sourceFolderPath, sourceCodeVo);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new CountTargetMethodsProcesser(sourceCodeVo, methods).executeSpoon(queueProcessingManager);
    }

    public boolean hasAnnotation(String sourceFolderPath, SourceCodeVo sourceCodeVo, Class<?> annotationType) {
        String classPath = makeClassPath(sourceFolderPath, sourceCodeVo);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        return new ConfirmTargetAnnotationProcesser(sourceCodeVo, annotationType).executeSpoon(queueProcessingManager);
    }

    public void updateBeanDefinitionMap(String sourceFolderPath, String springConfigPath,
            List<String> packagePrefixList, Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        String classPath = makeClassPath(sourceFolderPath, springConfigPath);
        Launcher launcher = makeLauncherWithCommonArgs();
        launcher.addInputResource(new FileSystemFile(new File(classPath)));
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        new FetchCreatedBeanProcesser(packagePrefixList, beanDefinitionMap).executeSpoon(queueProcessingManager);
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

    private String makeClassPath(String sourceFolderPath, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(sourceFolderPath);
        sb.append("/");
        sb.append(path.replaceAll("\\.", "/"));
        sb.append(".java");
        return sb.toString();
    }


    private Launcher makeLauncherWithCommonArgs() {
        try {
            Launcher launcher = new Launcher();
            File classpathFile = new File("classpath.txt");
            String classpathes = StringUtils.strip(FileUtils.readFileToString(classpathFile, "utf-8"), "\n\r\t ")
                    + ":"
                    + System.getProperty("user.dir")
                    + "/target/classes";
            launcher.setArgs(new String[] {
                    "--output-type",
                    "nooutput",
                    "--source-classpath",
                    classpathes
            });
            return launcher;
        } catch (IOException e) {
            log.error("classpath.txt is required. \n"
                    + "please run \"mvn dependency:build-classpath | grep -v \".*\\[.*\\].*\" > classpath.txt\".");
            throw new SpoonException(e);
        }
    }
}
