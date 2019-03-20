package org.dog.core.entry;

import java.io.Serializable;

public class DogCall implements Serializable {
    @Override
    public String toString() {
        return "[LocalServer:    name:"+name+"   key:"+key+"]";
    }

    private String key;

    private String name;

    private DogCallStatus serverStatus;

    public DogCallStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(DogCallStatus serverStatus) {
        this.serverStatus = serverStatus;
    }


    public DogCall(String name) {
        this.name = name;
        this.key = String.valueOf(this.hashCode());
        this.serverStatus = DogCallStatus.TRY;

    }

    public DogCall(String name, String key) {
        this.name = name;
        this.key = key;
        this.serverStatus = DogCallStatus.TRY;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
