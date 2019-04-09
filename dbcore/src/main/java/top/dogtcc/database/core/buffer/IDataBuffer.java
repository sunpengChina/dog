package top.dogtcc.database.core.buffer;

import top.dogtcc.core.entry.TccLock;

public interface IDataBuffer {

    void buffData(TccLock lock, Object object);

    Object getData(TccLock lock);

    void clearData(TccLock lock);

}
