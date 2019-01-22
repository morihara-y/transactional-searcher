package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

@RunWith(MockitoJUnitRunner.class)
public class SourceCodeFetchDaoImplTest {
    @Test
    public void test() {
        String sourceFolderPath = "src/main/java";
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<String> result1 = dao.fetchPackagesBySourceFolderPath(sourceFolderPath);
        System.out.println(result1);
        List<SourceCodeVo> result2 = dao.fetchMethodsByPackageName(sourceFolderPath, result1.get(1));
        for (SourceCodeVo vo : result2) {
            System.out.println(vo.toUniqueMethodStr());
        }
    }
}
