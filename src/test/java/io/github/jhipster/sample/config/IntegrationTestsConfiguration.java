package io.github.jhipster.sample.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;


import javax.annotation.PreDestroy;
import javax.sql.DataSource;

@Configuration
@Profile("testcontainers")
public class IntegrationTestsConfiguration {

    private Logger logger = LoggerFactory.getLogger(IntegrationTestsConfiguration.class);

    private JdbcDatabaseContainer dbContainer;

    @Bean
    public DataSource dataSource() {
        dbContainer = new MySQLContainer("mysql:5.7.20")
            .withDatabaseName("jhipstersampleapplication")
            .withUsername("root")
            .withPassword("");
        dbContainer.setCommand("mysqld --lower_case_table_names=1 --skip-ssl --character_set_server=utf8mb4 --explicit_defaults_for_timestamp");
        String jdbcUrlSuffix = "?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";

        dbContainer.start();

        String jdbcUrl = dbContainer.getJdbcUrl() + jdbcUrlSuffix;
        logger.info("Database started, creating datasource for url: '{}'", jdbcUrl);


        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(dbContainer.getUsername());
        dataSource.setPassword(dbContainer.getPassword());
        dataSource.setDriverClassName(dbContainer.getDriverClassName());
        dataSource.setAutoCommit(false);

        return dataSource;

    }

    @PreDestroy
    public void preDestroy(){
        if(this.dbContainer != null){
            this.dbContainer.stop();
        }
    }

}
