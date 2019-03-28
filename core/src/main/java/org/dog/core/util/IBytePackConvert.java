package org.dog.core.util;

import org.dog.core.entry.TccContext;

public interface IBytePackConvert {
     byte[] objectToByteArray(TccContext obj);
     TccContext byteArrayToObject(byte[] bytes);
}
