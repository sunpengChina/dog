package org.dog.serialize.protostuff;

import org.dog.core.entry.BytePack;
import org.dog.core.util.IBytePackConvert;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.stereotype.Component;

@Component
public class ProtostuffUtils implements IBytePackConvert {

    static {

        System.out.println("use ProtostuffUtils!");

    }

    private static  Schema<BytePack>  schema =  RuntimeSchema.getSchema(BytePack.class);

    @Override
    public byte[] objectToByteArray(BytePack obj) {

        byte[] data;

        try {

            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer.get());

        } finally {

            buffer.get().clear();
        }

        return data;
    }

    @Override
    public BytePack byteArrayToObject(byte[] bytes) {

        BytePack obj = schema.newMessage();

        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);

        return obj;

    }

    private  ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>(){
        public LinkedBuffer initialValue() {

            return  LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        }
    };

}
