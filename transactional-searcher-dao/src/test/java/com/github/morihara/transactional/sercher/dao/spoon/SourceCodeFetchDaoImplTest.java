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
        List<SourceCodeVo> result2 = dao.fetchMethodsByPackageName(sourceFolderPath, "com.github.morihara.transactional.sercher.dao.rdb");
        for (SourceCodeVo vo : result2) {
            System.out.println(vo.toUniqueMethodStr());
        }
        boolean result3 = dao.hasUpdateSql(sourceFolderPath, makeTransactionalMethodDaoSourceCode());
        System.out.println(result3);
    }

    private SourceCodeVo makeTransactionalMethodDaoSourceCode() {
        return SourceCodeVo.builder()
                .packageName("com.github.morihara.transactional.sercher.dao.rdb")
                .className("TransactionalMethodDaoImpl")
                .methodName("batchInsert")
                .methodParam("java.util.List")
                .methodType("void")
                .build();
    }
}
