package ru.voice_com.monitor.configurations;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@PropertySource("classpath:db-sabre-ix-archive.properties")
public class SabreIxArchiveDBConfig {

    @Bean("sabreIxArchiveDataSource")
    @ConfigurationProperties(prefix = "sabre-ix-archive-datasource")
    static DriverManagerDataSource sabreIxArchiveDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean("sabreIxArchiveMapperScannerConfigurer")
    static MapperScannerConfigurer sabreIxArchiveMapperScannerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("ru.voice_com.monitor.repositories.sabreixarchive");
        msc.setSqlSessionFactoryBeanName("sabreIxArchiveSqlSessionFactory");
        return msc;
    }

    @Bean("sabreIxArchiveSqlSessionFactory")
    static SqlSessionFactoryBean sabreIxArchiveSqlSessionFactory(
            @Qualifier("sabreIxArchiveDataSource") DriverManagerDataSource sabreIxArchiveDataSource) {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setDataSource(sabreIxArchiveDataSource);
        return ssfb;
    }

    @Bean("sabreIxArchiveTransactionManager")
    static DataSourceTransactionManager sabreIxArchiveTransactionManager(
            @Qualifier("sabreIxArchiveDataSource") DriverManagerDataSource sabreIxArchiveDataSource) {
        DataSourceTransactionManager dstm = new DataSourceTransactionManager();
        dstm.setDataSource(sabreIxArchiveDataSource);
        return dstm;
    }
}
