package com.github.morihara.transactional.searcher.dao.rdb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.morihara.transactional.searcher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.searcher.dto.vo.SourceCodeVo;
import com.github.morihara.transactional.searcher.enumerate.DevelopStatusEnum;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionalMethodDaoImpl implements TransactionalMethodDao {
    private final JdbcTemplate jdbc;

    private final RelatedDaoCodeDao relatedDaoCodeDao;

    private static final RowMapper<TransactionalMethodDto> ROW_MAPPER = (rs, i) -> {
        SourceCodeVo sourceCodeVo = SourceCodeVo.builder()
                .packageName(rs.getString("package_name"))
                .className(rs.getString("class_name"))
                .methodName(rs.getString("method_name"))
                .methodParam(rs.getString("method_param"))
                .methodType(rs.getString("method_type"))
                .build();
        return TransactionalMethodDto.builder()
                .transactionalMethodId(UUID.fromString(rs.getString("transactional_method_id")))
                .sourceCodeVo(sourceCodeVo)
                .developStatus(DevelopStatusEnum.getEnum(rs.getString("develop_status")))
                .build();
    };

    @Override
    public void batchInsert(List<TransactionalMethodDto> transactionalMethodDtos) {
        if (CollectionUtils.isEmpty(transactionalMethodDtos)) {
            return;
        }
        final String sql = "insert into transactional_method ("
                + "transactional_method_id, "
                + "package_name, "
                + "class_name, "
                + "method_name, "
                + "method_param, "
                + "method_type, "
                + "develop_status"
                + ") values (?, ?, ?, ?, ?, ?, ?)";
        BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TransactionalMethodDto transactionalMethodDto = transactionalMethodDtos.get(i);
                int index = 0;
                ps.setString(++index, transactionalMethodDto.getTransactionalMethodId().toString());
                ps.setString(++index, transactionalMethodDto.getSourceCodeVo().getPackageName());
                ps.setString(++index, transactionalMethodDto.getSourceCodeVo().getClassName());
                ps.setString(++index, transactionalMethodDto.getSourceCodeVo().getMethodName());
                ps.setString(++index, transactionalMethodDto.getSourceCodeVo().getMethodParam());
                ps.setString(++index, transactionalMethodDto.getSourceCodeVo().getMethodType());
                ps.setString(++index, transactionalMethodDto.getDevelopStatus().getText());
            }
            @Override
            public int getBatchSize() {
                return 100;
            }
        };
        jdbc.batchUpdate(sql, pss);
    }

    @Override
    public void updateDevelopStatus(TransactionalMethodDto transactionalMethodDto) {
        if (Objects.isNull(transactionalMethodDto)) {
            return;
        }
        jdbc.update(
                "update transactional_method set develop_status = ?"
                        + " where transactional_method_id = ?",
                transactionalMethodDto.getDevelopStatus().getText(),
                transactionalMethodDto.getTransactionalMethodId());
    }

    @Override
    public void delete(UUID transactionalMethodId) {
        if (Objects.isNull(transactionalMethodId)) {
            return;
        }
        jdbc.update("delete from transactional_method where transactional_method_id = ?",
                transactionalMethodId);
    }

    @Override
    public Optional<TransactionalMethodDto> fetchByMethod(String sourceFolderPath, SourceCodeVo sourceCodeVo) {
        String sql = "select * from transactional_method "
                + "where source_folder_path = ? "
                + "and package_name = ? "
                + "and class_name = ? "
                + "and method_name = ? "
                + "and method_param = ? "
                + "order by source_folder_path, package_name, class_name, method_name, method_param";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, sourceFolderPath);
                ps.setString(2, sourceCodeVo.getPackageName());
                ps.setString(3, sourceCodeVo.getClassName());
                ps.setString(4, sourceCodeVo.getMethodName());
                ps.setString(5, sourceCodeVo.getMethodParam());
            }
        };
        List<TransactionalMethodDto> dtos = jdbc.query(sql, pss, ROW_MAPPER);
        if (dtos.size() != 1) {
            return Optional.empty();
        }
        TransactionalMethodDto dto = dtos.get(0);
        UUID transactionalMethodId = dto.getTransactionalMethodId();
        dto.setRelatedDaoCodes(relatedDaoCodeDao.fetchByRelatedMethodId(transactionalMethodId));
        return Optional.of(dto);
    }
}
