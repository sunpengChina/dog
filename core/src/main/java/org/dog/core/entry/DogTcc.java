package org.dog.core.entry;

import java.io.Serializable;

public class DogTcc implements Serializable {

    @Override
    public int hashCode() {

        String hashStr = application + key + name;

        return hashStr.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof DogTcc) {

            DogTcc other= (DogTcc) obj;

            return application.equals(other.application)&&name.equals(other.name)&& key.equals(other.key);

        }

        return false;
    }

    @Override
    public String toString() {
        return "[DogTran:  application:"+application+"    name:"+name+"   key:"+key+" status"+status+"]";
    }

    public final  static  String NameHeader = "DogName";

    public final  static  String KeyHeader = "DogKey";

    public  final  static  String ApplicationHeader = "DogApplication";

    private String application;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DogTccStatus getStatus() {
        return status;
    }

    public void setStatus(DogTccStatus status) {
        this.status = status;
    }

    private String key;

    private DogTccStatus status;

    private String name;

    public DogTcc(String application, String name) {
        this.application = application;
        this.name = name;
        this.key = String.valueOf(super.hashCode());
        this.status = DogTccStatus.TRY;
    }

    public DogTcc(String application, String name, String key) {
        this.application = application;
        this.name = name;
        this.key = key;
        this.status = DogTccStatus.TRY;
    }

    public String getName() {
        return name;
    }

    public boolean isSuccess(){

        return this.getStatus().equals(DogTccStatus.CONFIRM);
    }

}
