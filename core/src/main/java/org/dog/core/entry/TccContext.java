package org.dog.core.entry;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class TccContext implements Serializable {

    private Set<TccLock> lockList;

    public Set<TccLock> getLockList() {
        return lockList;
    }

    public void setLockList(Set<TccLock> lockList) {
        this.lockList = lockList;
    }

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

    public TccContext(String className, Object[] args){

        this.className = className;

        this.args = args;

    }

    public TccContext(){


    }



}
