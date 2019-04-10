package top.dogtcc.database.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import top.dogtcc.core.common.DefaultBytePackConvert;
import top.dogtcc.core.common.IBytePackConvert;
import top.dogtcc.core.log.DefaultErrorLog;
import top.dogtcc.core.log.DefaultHistoryLog;
import top.dogtcc.core.log.IErrorLog;
import top.dogtcc.core.log.IHistoryLog;

@Configuration
@EnableAspectJAutoProxy
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "dog.application")
public class DatabaseAutoConfig {



    @Bean
    @ConditionalOnMissingBean(DbTccHandler.class)
    public DbTccHandler dbTccHandler() {
        return new DbTccHandler();
    }




}
