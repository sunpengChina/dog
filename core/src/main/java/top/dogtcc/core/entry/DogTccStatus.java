package top.dogtcc.core.entry;

import java.io.Serializable;

/**
 *  TRY            执行中
 *  CONFIRM            执行成功
 *  CANCEL             执行失败
 */
public enum DogTccStatus implements Serializable {

    TRY("TRY"),CONFIRM("CONFIRM"),CANCEL("CANCEL"),UNKNOWN("UNKNOWN");

    private byte[] value;

    private DogTccStatus(String value) {
        this.value = value.getBytes();
    }

    public byte[] getBytes() {
        return value;
    }

    public  static DogTccStatus getInstance(byte[] value){

        String str = new String(value);

        if(str.toString().equals("TRY")){

            return DogTccStatus.TRY;

        }else if (str.toString().equals("CONFIRM")){

            return DogTccStatus.CONFIRM;

        }else if (str.toString().equals("CANCEL")) {

            return  DogTccStatus.CANCEL;
        }else {

            return  DogTccStatus.UNKNOWN;
        }
    }


    @Override
    public String toString() {
        return new String(value);
    }
}
