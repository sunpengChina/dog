package top.dogtcc.core.common;

import top.dogtcc.core.entry.TccContext;

public interface IBytePackConvert {
     byte[] objectToByteArray(TccContext obj);
     TccContext byteArrayToObject(byte[] bytes);
}
