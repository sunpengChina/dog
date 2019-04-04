package org.dog.database.core.aop;

import org.dog.core.entry.TccLock;

public class DBLock extends TccLock {

    public DBLock(DBLockBuilder builder) {
        super("");
        builder.toString();

    }
}
