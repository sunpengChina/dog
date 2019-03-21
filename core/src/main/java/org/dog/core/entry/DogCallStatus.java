package org.dog.core.entry;

import java.io.Serializable;

/**
 *  TRY            执行中
 *  CONFIRM        已经确认结果
 */
public enum DogCallStatus implements Serializable {

    TRY("TRY"),CONFIRM("CONFIRM");

    private byte[] value;

    private DogCallStatus(String value) {
        this.value = value.getBytes();
    }

    public byte[] getBytes() {
        return value;
    }

    @Override
    public String toString() {
        return new String(value);
    }
}
