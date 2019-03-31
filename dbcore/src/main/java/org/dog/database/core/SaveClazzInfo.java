package org.dog.database.core;

import java.io.Serializable;
import java.lang.reflect.Method;

public class SaveClazzInfo implements Serializable {

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof SaveClazzInfo) {

            SaveClazzInfo other= (SaveClazzInfo) obj;

            return clazz.equals(other.clazz)&& saveMethod.equals(other.saveMethod);

        }

        return false;
    }

    public SaveClazzInfo() {

    }


    public SaveClazzInfo(Class<?> clazz, String saveMethod) {
        this.clazz = clazz;
        this.saveMethod = saveMethod;
    }

    private Class<?> clazz;

    private String saveMethod;

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
