package top.dogtcc.serialize.protostuff;

import top.dogtcc.core.entry.TccContext;
import top.dogtcc.core.common.IBytePackConvert;
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

    private static  Schema<TccContext>  schema =  RuntimeSchema.getSchema(TccContext.class);

    @Override
    public byte[] objectToByteArray(TccContext obj) {

        byte[] data;

        try {

            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer.get());

        } finally {

            buffer.get().clear();
        }

        return data;
    }

    @Override
    public TccContext byteArrayToObject(byte[] bytes) {

        TccContext obj = schema.newMessage();

        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);

        return obj;

    }

    private  ThreadLocal<LinkedBuffer> buffer = new ThreadLocal<LinkedBuffer>(){
        public LinkedBuffer initialValue() {

            return  LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        }
    };

}
