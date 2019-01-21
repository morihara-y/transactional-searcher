package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SourceCodeFetchDaoImplTest {
    @Test
    public void test() {
        SourceCodeFetchDao dao = new SourceCodeFetchDaoImpl();
        List<String> result = dao.fetchPackagesBySourceFolderPath("/home/morihara_y/HUE/WorkSpace/Eclipse47/transactional-searcher/transactional-searcher-dao/src/main/java");
        System.out.println(result);
    }
}
