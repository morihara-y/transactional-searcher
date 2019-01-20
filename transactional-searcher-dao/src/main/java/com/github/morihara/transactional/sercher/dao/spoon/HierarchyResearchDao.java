package com.github.morihara.transactional.sercher.dao.spoon;

import java.util.List;
import com.github.morihara.transactional.sercher.dto.vo.HierarchyVo;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;

public interface HierarchyResearchDao {
    List<HierarchyVo> callHierarchy(SourceCodeVo sourceCodeVo);
}
