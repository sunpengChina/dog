package top.dogtcc.database.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

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
