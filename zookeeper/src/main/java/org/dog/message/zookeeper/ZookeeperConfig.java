package org.dog.message.zookeeper;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:application.properties"})
@ConfigurationProperties(prefix = "dog.zookeeper")
public class ZookeeperConfig {

    private String path;

    public String getPath() {

            return path;

    }

    public void setPath(String path) {
        this.path = path;
    }

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

    private  int recoveryperiod;

    private int initialdeplay;

}


