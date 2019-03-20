package org.dog.core.util;

import org.dog.core.entry.BytePack;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

public class DefaultBytePackConvert implements IBytePackConvert {

    private static Logger logger = Logger.getLogger(DefaultBytePackConvert.class);

    @Override
    public byte[] objectToByteArray(BytePack obj) {

       return SerializationUtils.serialize(obj);
    }

    @Override
    public BytePack byteArrayToObject(byte[] bytes) {

       return SerializationUtils.deserialize(bytes);

    }
}
