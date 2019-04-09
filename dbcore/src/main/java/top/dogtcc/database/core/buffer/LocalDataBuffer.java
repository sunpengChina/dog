package top.dogtcc.database.core.buffer;

import org.apache.commons.lang3.SerializationUtils;
import top.dogtcc.core.entry.TccLock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LocalDataBuffer implements IDataBuffer {

    private Map<TccLock,Object> buff = new HashMap<>();

    @Override
    public void buffData(TccLock lock, Object object) {

        buff.putIfAbsent(lock, SerializationUtils.deserialize(SerializationUtils.serialize((Serializable) object)));

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
