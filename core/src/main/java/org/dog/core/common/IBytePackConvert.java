package org.dog.core.common;

import org.dog.core.entry.TccContext;

public interface IBytePackConvert {
     byte[] objectToByteArray(TccContext obj);
     TccContext byteArrayToObject(byte[] bytes);
}
