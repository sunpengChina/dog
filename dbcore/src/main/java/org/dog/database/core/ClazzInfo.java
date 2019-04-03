package org.dog.database.core;

import org.dog.database.core.annotation.OperationType;
import org.dog.database.core.util.ReflectUtil;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ClazzInfo implements Serializable {
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getMethodString() {
        return methodString;
    }

    public void setMethodString(String methodString) {
        this.methodString = methodString;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    private Class<?> clazz;

    private String methodString;

    private OperationType operationType;


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof ClazzInfo) {

            ClazzInfo other= (ClazzInfo) obj;

            return clazz.equals(other.clazz)&& methodString.equals(other.methodString)&& operationType.equals(other.operationType);

        }

        return false;
    }

    public ClazzInfo() {

    }

    public ClazzInfo(Class<?> clazz, String method, OperationType operationType) {
        this.clazz = clazz;
        this.methodString = method;
        this.operationType = operationType;
    }

    public Method method()throws NoSuchMethodException {

        return  ReflectUtil.getMethod(clazz,methodString);
    }








}
