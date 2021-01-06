package me.bleaf.comix.admin.configuration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Data
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "comix")
@RequiredArgsConstructor
public class ComixConfig {
    final JdbcTemplate jdbcTemplate;
    Map<String, String> configMap;

    @PostConstruct
    public void init() {
        configMap = jdbcTemplate.query("SELECT * FROM comix.comix_config", rs -> {
            Map<String, String> map = new HashMap<>();
            while(rs.next()) {
                map.put(rs.getString("config_name"), rs.getString("config_value"));
            }

            return map;
        });
    }

    public String getString(ComixConfgPropertyName configName) {
        return configMap.get(configName.getPropertyName());
    }

    public int getInt(ComixConfgPropertyName configName) {
        return Integer.parseInt(configMap.get(configName.getPropertyName()));
    }
}
