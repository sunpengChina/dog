package org.dog.database.core.buffer;

import org.dog.core.entry.TccLock;

import java.util.List;

public interface IDataBuffer {

    void buffData(TccLock lock, Object object);

    Object getData(TccLock lock);

    void clearData(TccLock lock);

}
