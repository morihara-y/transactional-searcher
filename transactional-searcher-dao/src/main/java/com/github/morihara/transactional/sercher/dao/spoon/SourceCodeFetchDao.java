package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

public interface SourceCodeFetchDao {
    List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath);

    List<SourceCodeVo> fetchMethodsByPackageName(String sourceFolderPath, String packageName);

    int hasMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo, Method[] methods);

    boolean hasAnnotation(String sourceFolderPath, SourceCodeVo sourceCodeVo, Class<?> annotationType);
}
