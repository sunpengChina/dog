package org.dog.core.entry;

import java.io.Serializable;

public class BytePack implements Serializable {

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    String className = "";

    Object[] args = {};

    public BytePack(String className,Object[] args){

        this.className = className;

        this.args = args;

    }

    public BytePack(){


    }



}
