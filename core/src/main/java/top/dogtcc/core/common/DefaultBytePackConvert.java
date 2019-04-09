package top.dogtcc.core.common;

import top.dogtcc.core.entry.TccContext;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

public class DefaultBytePackConvert implements IBytePackConvert {

    private static Logger logger = Logger.getLogger(DefaultBytePackConvert.class);

    @Override
    public byte[] objectToByteArray(TccContext obj) {

       return SerializationUtils.serialize(obj);
    }

    @Override
    public TccContext byteArrayToObject(byte[] bytes) {

       return SerializationUtils.deserialize(bytes);

    }
}
