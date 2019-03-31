package org.dog.core;

import org.dog.core.log.DefaultErrorLog;
import org.dog.core.log.DefaultHistoryLog;
import org.dog.core.log.IErrorLog;
import org.dog.core.log.IHistoryLog;
import org.dog.core.util.DefaultBytePackConvert;
import org.dog.core.util.IBytePackConvert;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "dog.application")
public class ApplicationAutoConfig {

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    private String applicationname;

    @Bean
    @ConditionalOnMissingBean(IHistoryLog.class)
    public IHistoryLog historyLog() {
        return new DefaultHistoryLog();
    }

    @Bean
    @ConditionalOnMissingBean(IBytePackConvert.class)
    public IBytePackConvert objectBytesConvert(){
        return  new DefaultBytePackConvert();
    }

    @Bean
    @ConditionalOnMissingBean(IErrorLog.class)
    public IErrorLog errorLog(){
        return  new DefaultErrorLog();
    }




}
