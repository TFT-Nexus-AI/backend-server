package org.project.storage.db.core.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CoreDataSourceConfig {

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.core")
//    public HikariConfig coreHikariConfig() {
//        return new HikariConfig();
//    }
//
//    @Bean
//    public HikariDataSource coreDataSource(@Qualifier("coreHikariConfig") HikariConfig config) {
//        return new HikariDataSource(config);
//    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource coreDataSource() {
        return new HikariDataSource();
    }

}
