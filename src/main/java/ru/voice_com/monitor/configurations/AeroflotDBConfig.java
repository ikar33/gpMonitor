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
@PropertySource("classpath:db-aeroflot.properties")
public class AeroflotDBConfig {

    @Bean("aeroflotDataSource")
    @ConfigurationProperties(prefix = "aeroflot-datasource")
    static DriverManagerDataSource aeroflotDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean("aeroflotMapperScannerConfigurer")
    static MapperScannerConfigurer aeroflotMapperScannerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("ru.voice_com.monitor.repositories.aeroflot");
        msc.setSqlSessionFactoryBeanName("aeroflotSqlSessionFactory");
        return msc;
    }

    @Bean("aeroflotSqlSessionFactory")
    static SqlSessionFactoryBean aeroflotSqlSessionFactory(
            @Qualifier("aeroflotDataSource") DriverManagerDataSource aeroflotDataSource) {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setDataSource(aeroflotDataSource);
        return ssfb;
    }

    @Bean("aeroflotTransactionManager")
    static DataSourceTransactionManager aeroflotTransactionManager(
            @Qualifier("aeroflotDataSource") DriverManagerDataSource aeroflotDataSource) {
        DataSourceTransactionManager dstm = new DataSourceTransactionManager();
        dstm.setDataSource(aeroflotDataSource);
        return dstm;
    }
}
