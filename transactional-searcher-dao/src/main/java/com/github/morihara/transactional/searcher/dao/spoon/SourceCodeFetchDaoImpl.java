package com.github.morihara.transactional.searcher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.github.morihara.transactional.searcher.dto.vo.BeanDefinitionVo;
import com.github.morihara.transactional.searcher.dto.vo.MetadataResourceVo;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;

import spoon.JarLauncher;
import spoon.support.QueueProcessingManager;

@Repository
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
    public List<SourceCodeVo> fetchPublicMethodsByClassAnotation(Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return new FetchImplementedMethodsProcesser(annotationType, metadataResourceMap).executeSpoon();
    }

    @Override
    public List<SourceCodeVo> fetchCalledMethodsByMethod(SourceCodeVo sourceCodeVo,
            List<String> filterPackagePrefixList,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return new FetchChildMethodsProcesser(filterPackagePrefixList, metadataResourceMap)
                .executeSpoon(sourceCodeVo);
    }

    @Override
    public int hasMethod(SourceCodeVo sourceCodeVo, Method[] methods,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return new CountTargetMethodsProcesser(methods, metadataResourceMap).executeSpoon(sourceCodeVo);
    }

    @Override
    public boolean hasAnnotation(SourceCodeVo sourceCodeVo, Class<?> annotationType,
            Map<String, MetadataResourceVo> metadataResourceMap) {
        return new ConfirmTargetAnnotationProcesser(annotationType, metadataResourceMap).executeSpoon(sourceCodeVo);
    }
}
