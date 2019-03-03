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

@Repository
@Slf4j
public class SourceCodeFetchDaoImpl implements SourceCodeFetchDao {
    private static final String TMP_FOLDER_PATH = System.getProperty("java.io.tmpdir");
//    private static final String SOURCE_FOLDER_PATH = "src/main/java";

    @Override
    public void walkJarFile(String jarPath, String jarName,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        String resourceFolderPath = TMP_FOLDER_PATH 
                + System.getProperty("file.separator")
                + jarName;
        JarLauncher launcher = new JarLauncher(jarPath, resourceFolderPath);
        launcher.setArgs(new String[] {"--output-type", "nooutput"});
        launcher.addInputResource(resourceFolderPath);
        launcher.run();
        QueueProcessingManager queueProcessingManager = new QueueProcessingManager(launcher.getFactory());
        new WalkJarFileProcesser(metadataResourceMap).executeSpoon(queueProcessingManager);
    }

    @Override
    public void makeBeanDefinitionMap(String configQualifiedName,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        new FetchCreatedBeanProcesser(configQualifiedName, beanDefinitionMap, metadataResourceMap)
                .executeSpoon();
    }

    @Override
    public List<SourceCodeVo> fetchPublicMethodsByAnotation(Annotation annotation,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return new FetchImplementedMethodsProcesser(annotation.annotationType(),
                metadataResourceMap).executeSpoon();
    }

    @Override
    public List<SourceCodeVo> fetchCalledMethodsByMethod(SourceCodeVo sourceCodeVo,
            List<String> filterPackagePrefixList,
            Map<String, MetadataResourceVo> metadataResourceMap,
            Map<String, List<BeanDefinitionVo>> beanDefinitionMap) {
        return new FetchChildMethodsProcesser(filterPackagePrefixList, metadataResourceMap)
                .executeSpoon(sourceCodeVo, beanDefinitionMap);
    }

    @Override
    public int hasMethod(SourceCodeVo sourceCodeVo, Method[] methods,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return 0;
    }

    @Override
    public boolean hasAnnotation(SourceCodeVo sourceCodeVo, Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        // TODO Auto-generated method stub
        return false;
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
