package com.github.morihara.transactional.sercher.dao.rdb;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.github.morihara.transactional.sercher.dto.TransactionalMethodDto;
import com.github.morihara.transactional.sercher.dto.vo.SourceCodeVo;
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
                .sourceFolderPath(rs.getString("source_folder_path"))
                .sourceCodeVo(sourceCodeVo)
                .isDeveloped(rs.getBoolean("is_developed"))
                .ticketNo(rs.getInt("ticket_no"))
                .build();
    };

    @Override
    public void batchInsert(List<TransactionalMethodDto> transactionalMethodDtos) {
        final String sql = "insert into transactional_method ("
                + "transactional_method_id, "
                + "source_folder_path, "
                + "package_name, "
                + "class_name, "
                + "method_name, "
                + "method_param, "
                + "method_type, "
                + "is_developed, "
                + "ticket_no"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TransactionalMethodDto transactionalMethodDto = transactionalMethodDtos.get(i);
                int index = 0;
                ps.setString(index++, transactionalMethodDto.getTransactionalMethodId().toString());
                ps.setString(index++, transactionalMethodDto.getSourceFolderPath());
                ps.setString(index++, transactionalMethodDto.getSourceCodeVo().getPackageName());
                ps.setString(index++, transactionalMethodDto.getSourceCodeVo().getClassName());
                ps.setString(index++, transactionalMethodDto.getSourceCodeVo().getMethodName());
                ps.setString(index++, transactionalMethodDto.getSourceCodeVo().getMethodParam());
                ps.setString(index++, transactionalMethodDto.getSourceCodeVo().getMethodType());
                ps.setBoolean(index++, transactionalMethodDto.isDeveloped());
                ps.setInt(index++, transactionalMethodDto.getTicketNo());
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
        jdbc.update(
                "update transactional_method set is_developed = ?, ticket_no = ?"
                        + " where transactional_method_id = ?",
                transactionalMethodDto.isDeveloped(),
                transactionalMethodDto.getTicketNo(),
                transactionalMethodDto.getTransactionalMethodId());
    }

    @Override
    public void delete(UUID transactionalMethodId) {
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
