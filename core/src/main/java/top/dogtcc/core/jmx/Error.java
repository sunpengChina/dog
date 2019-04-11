package top.dogtcc.core.jmx;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.entry.TccLock;

import java.util.List;
import java.util.Set;

public class Error {

    public Set<TccLock> getLockList() {
        return lockList;
    }

    public void setLockList(Set<TccLock> lockList) {
        this.lockList = lockList;
    }

    public String getArgString() {
        return argString;
    }

    public void setArgString(String argString) {
        this.argString = argString;
    }

    public Error(DogTcc tcc, DogCall call, Set<TccLock>  lockList, String argString) {

        this.tcc = tcc;

        this.call = call;

        this.lockList = lockList;

        this.argString = argString;

    }

    String argString;

    DogTcc tcc;

    DogCall call;

    Set<TccLock> lockList;


    public DogTcc getTcc() {
        return tcc;
    }

    public void setTcc(DogTcc tcc) {
        this.tcc = tcc;
    }

    public DogCall getCall() {
        return call;
    }

    public void setCall(DogCall call) {
        this.call = call;
    }


}
