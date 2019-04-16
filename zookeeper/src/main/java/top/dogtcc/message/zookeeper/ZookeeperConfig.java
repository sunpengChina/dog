package top.dogtcc.message.zookeeper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "dog.zookeeper")
public class ZookeeperConfig {

    @Value("${poolsize:2}")
    private int poolsize;

    public int getPoolsize() {
        return poolsize;
    }

    public void setPoolsize(int poolsize) {
        this.poolsize = poolsize;
    }

    @Value("${dogpath:/dog}")
    private String path;

    public String getPath() {

            return path;

    }

    public void setPath(String path) {
        this.path = path;
    }

    @Value("${connectString:127.0.0.1:2181}")
    private String connectString;

    public String getConnectString() {
        return connectString;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    @Value("${connectString:4000}")
    private int sessionTimeout;


    public int getRecoveryperiod() {
        return recoveryperiod;
    }

    public void setRecoveryperiod(int recoveryperiod) {
        this.recoveryperiod = recoveryperiod;
    }

    public int getInitialdeplay() {
        return initialdeplay;
    }

    public void setInitialdeplay(int initialdeplay) {
        this.initialdeplay = initialdeplay;
    }


    @Value("${recoveryperiod:120}")
    private  int recoveryperiod;


    @Value("${initialdeplay:10}")
    private int initialdeplay;

}


