package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.List;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

public interface SourceCodeFetchDao {
    List<String> fetchPackagesBySourceFolderPath(String sourceFolderPath);

    List<SourceCodeVo> fetchMethodsByPackageName(String packageName);

    boolean hasUpdateSql(SourceCodeVo sourceCodeVo);

    boolean hasTransactionalAnnotation(SourceCodeVo sourceCodeVo);
}
