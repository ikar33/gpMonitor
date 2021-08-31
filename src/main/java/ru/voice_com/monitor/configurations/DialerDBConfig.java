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
@PropertySource("classpath:db-dialer.properties")
public class DialerDBConfig {

    @Bean("dialerDataSource")
    @ConfigurationProperties(prefix = "dialer-datasource")
    static DriverManagerDataSource dialerDataSource() {
        return new DriverManagerDataSource();
    }

    @Bean("dialerMapperScannerConfigurer")
    static MapperScannerConfigurer dialerMapperScannerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("ru.voice_com.monitor.repositories.dialer");
        msc.setSqlSessionFactoryBeanName("dialerSqlSessionFactory");
        return msc;
    }

    @Bean("dialerSqlSessionFactory")
    static SqlSessionFactoryBean dialerSqlSessionFactory(
            @Qualifier("dialerDataSource") DriverManagerDataSource dialerDataSource) {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setDataSource(dialerDataSource);
        return ssfb;
    }

    @Bean("dialerTransactionManager")
    static DataSourceTransactionManager dialerTransactionManager(
            @Qualifier("dialerDataSource") DriverManagerDataSource dialerDataSource) {
        DataSourceTransactionManager dstm = new DataSourceTransactionManager();
        dstm.setDataSource(dialerDataSource);
        return dstm;
    }
}
