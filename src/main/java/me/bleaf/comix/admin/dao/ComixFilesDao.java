package me.bleaf.comix.admin.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bleaf.comix.admin.model.Comix;
import me.bleaf.comix.admin.model.ComixFile;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ComixFilesDao {
    final JdbcTemplate jdbcTemplate;

    public void saveAll(final List<Comix> comixList) {
        String sql = "INSERT INTO comix.comix_files VALUES(default, ?, ?, ?, ?, ?, ?)";

        final List<ComixFile> comixFileList = comixList.stream()
                .map(Comix::getComixFileList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ComixFile comixFile = comixFileList.get(i);

                    ps.setString(1, comixFile.getComixTitle());
                    ps.setString(2, comixFile.getComixPath().toString());
                    ps.setString(3, comixFile.getFileName());
                    ps.setString(4, comixFile.getFilePath().toString());
                    ps.setString(5, comixFile.getFileExt());
                    ps.setLong(6, comixFile.getFileSize());
                }

                @Override
                public int getBatchSize() {
                    return comixFileList.size();
                }
            });
        } catch (Exception e) {
            log.error("error save all comix file, size = {} -> {}, cause = \n{}", comixList.size(), comixFileList.size(), e.getCause());
            throw new RuntimeException(e);
        }
    }
}
