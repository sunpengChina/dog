package org.dog.core.util;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.TccContext;


public class ThreadManager {

    /**
     * 本地线程保存事务信息
     */
    private static final ThreadLocal<Pair<DogTcc, Pair<DogCall,TccContext>>> transactionWithThread = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new Pair<DogTcc, Pair<DogCall,TccContext>>(null,new Pair<>(null,null));
        }
    };


    public  static void clearTransaction(){
        transactionWithThread.remove();
    }


    public  static void clearCallAndContext(){

        setCallAndContext(new Pair<DogCall,TccContext>(null,null));

    }

    public static void setTransaction(DogTcc tran){
        transactionWithThread.set(new Pair<DogTcc, Pair<DogCall,TccContext>>(tran,new Pair<>(null,null)));
    }

    public static void setCallAndContext(Pair<DogCall,TccContext> callAndContext){

        Pair<DogTcc, Pair<DogCall,TccContext>> newPair = new Pair<>(transactionWithThread.get().getKey(),callAndContext);

        transactionWithThread.remove();

        transactionWithThread.set(newPair);

    }

    public  static TccContext getTccContext(){

        return  transactionWithThread.get().getValue().getValue();

    }

    public  static DogCall getDogCall(){

        return  transactionWithThread.get().getValue().getKey();

    }

    public static DogTcc getTransaction(){

        return  transactionWithThread.get().getKey();

    }

    public static boolean exsit(){
        return  getTransaction() != null;
    }

}
