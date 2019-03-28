package org.dog.core.entry;

import java.io.Serializable;

public class TccLock implements Serializable {

    private String key;

    public TccLock(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof TccLock) {

            TccLock other= (TccLock) obj;

            return this.key.equals(other.getKey());

        }

        return false;
    }
}
