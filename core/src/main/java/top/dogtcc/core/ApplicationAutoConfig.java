package top.dogtcc.core;

import top.dogtcc.core.log.DefaultErrorLog;
import top.dogtcc.core.log.DefaultHistoryLog;
import top.dogtcc.core.log.IErrorLog;
import top.dogtcc.core.log.IHistoryLog;
import top.dogtcc.core.common.DefaultBytePackConvert;
import top.dogtcc.core.common.IBytePackConvert;
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
