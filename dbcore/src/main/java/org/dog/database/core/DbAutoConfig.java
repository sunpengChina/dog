package org.dog.database.core;

import org.dog.database.core.buffer.IDataBuffer;
import org.dog.database.core.buffer.LocalDataBuffer;
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
public class DbAutoConfig {

    @Bean
    @ConditionalOnMissingBean(IDataBuffer.class)
    public IDataBuffer dataBuffer() {
        return new LocalDataBuffer();
    }





}
