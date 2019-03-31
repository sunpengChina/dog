package org.dog.database.core.buffer;

import org.dog.core.entry.TccLock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDataBuffer implements IDataBuffer {

    private Map<TccLock,Object> buff = new HashMap<>();

    @Override
    public void buffData(TccLock lock,Object object) {

        buff.putIfAbsent(lock,object);

    }

    @Override
    public  Object getData(TccLock lock) {
        return buff.get(lock);
    }

    @Override
    public void clearData(TccLock lock) {
        buff.remove(lock);
    }


}
