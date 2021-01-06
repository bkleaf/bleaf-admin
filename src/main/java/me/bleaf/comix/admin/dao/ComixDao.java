package me.bleaf.comix.admin.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.model.Comix;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ComixDao {
    final JdbcTemplate jdbcTemplate;

    public void saveAll(final List<Comix> comixList) {
        String sql = "INSERT INTO comix.comix VALUES(default, ?, null, ?, default)";

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, comixList.get(i).getTitle());
                    ps.setInt(2, comixList.get(i).getVolume());
                }

                @Override
                public int getBatchSize() {
                    return comixList.size();
                }
            });
        } catch (Exception e) {
            log.error("error save all, comix list, size = {}, cause = \n{}", comixList.size(), e.getCause());
            throw new RuntimeException(e);
        }
    }
}
