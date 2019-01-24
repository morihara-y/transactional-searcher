package com.github.morihara.transactional.sercher.dao.spoon;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.github.morihara.transactional.sercher.dao.util.MethodsUtil;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

@RunWith(MockitoJUnitRunner.class)
public class SourceCodeFetchDaoImplTest {
    private static final Method[] JDBC_BATCH_UPDATE = MethodsUtil.getDeclaredMethods(JdbcTemplate.class, "batchUpdate");

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
        int result3 = dao.hasMethod(sourceFolderPath, makeTransactionalMethodDaoSourceCode(), JDBC_BATCH_UPDATE);
        System.out.println(result3);
        boolean result4 = dao.hasAnnotation(sourceFolderPath, makeTransactionalMethodDaoSourceCode(), Override.class);
        System.out.println(result4);
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
