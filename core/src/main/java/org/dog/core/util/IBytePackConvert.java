package org.dog.core.util;

import org.dog.core.entry.BytePack;

public interface IBytePackConvert {
     byte[] objectToByteArray(BytePack obj);
     BytePack byteArrayToObject(byte[] bytes);
}
