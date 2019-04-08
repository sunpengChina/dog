package org.dog.core.entry;

import java.io.Serializable;


public class DogCall implements Serializable {

    @Override
    public int hashCode() {

        return (UUID + applicationName).hashCode();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DogCall) {

            DogCall other= (DogCall) obj;

            return   applicationName.equals(other.applicationName) && UUID.equals(other.UUID);

        }

        return false;
    }


    @Override
    public String toString() {
        return "[LocalServer:    Name:"+ UUID +"   applicationName:" + applicationName;
    }

    private String applicationName;

    private String UUID;


    public DogCall(String uuid, String applicationName) {
        this.UUID = uuid.replace("-","");
        this.applicationName = applicationName;
    }


    public String getUUID() {
        return UUID;
    }




}
