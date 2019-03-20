package org.dog.core.util;

import org.dog.core.entry.DogTcc;


public class ThreadManager {

    /**
     * 本地线程保存事务信息
     */
    private static final ThreadLocal<DogTcc> transactionWithThread = new ThreadLocal();

    public  static void clearTransaction(){
        transactionWithThread.remove();
    }

    public static void setTransaction(DogTcc tran){
        transactionWithThread.set(tran);
    }

    public static DogTcc getTransaction(){
        return  transactionWithThread.get();
    }

    public static boolean exsit(){
        return  getTransaction() != null;
    }

}
