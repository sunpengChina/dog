package org.dog.database.core;

import java.io.Serializable;

public class ClazzInfo implements Serializable {

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ClazzInfo) {

            ClazzInfo other= (ClazzInfo) obj;

            return clazz.equals(other.clazz)&& saveMethod.equals(other.saveMethod);

        }

        return false;
    }

    public ClazzInfo() {

    }

    public ClazzInfo(Class<?> clazz, String saveMethod,String deleteMethod) {
        this.clazz = clazz;
        this.saveMethod = saveMethod;
        this.deleteMethod = deleteMethod;
    }

    private Class<?> clazz;

    private String saveMethod;

    private String deleteMethod;

    public String getDeleteMethod() {
        return deleteMethod;
    }

    public void setDeleteMethod(String deleteMethod) {
        this.deleteMethod = deleteMethod;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getSaveMethod() {
        return saveMethod;
    }

    public void setSaveMethod(String saveMethod) {
        this.saveMethod = saveMethod;
    }
}
