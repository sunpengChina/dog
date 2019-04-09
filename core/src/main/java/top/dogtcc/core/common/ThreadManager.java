package top.dogtcc.core.common;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;


public class ThreadManager {

    /**
     * 本地线程保存事务信息
     */
    private static final ThreadLocal<Pair<DogTcc, Pair<DogCall, TccContext>>> transactionWithThread = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new Pair<DogTcc, Pair<DogCall,TccContext>>(null,new Pair<>(null,null));
        }
    };


    public  static void clearTcc(){
        transactionWithThread.remove();
    }


    public  static void clearCall(){

        setCall(new Pair<DogCall,TccContext>(null,null));

    }

    public static void setTcc(DogTcc tran){
        transactionWithThread.set(new Pair<DogTcc, Pair<DogCall,TccContext>>(tran,new Pair<>(null,null)));
    }

    public static void setCall(Pair<DogCall,TccContext> callAndContext){

        Pair<DogTcc, Pair<DogCall,TccContext>> newPair = new Pair<>(transactionWithThread.get().getKey(),callAndContext);

        transactionWithThread.remove();

        transactionWithThread.set(newPair);

    }

    public  static TccContext getContext(){

        return  transactionWithThread.get().getValue().getValue();

    }

    public  static DogCall currentCall(){

        return  transactionWithThread.get().getValue().getKey();

    }

    public static DogTcc currentTcc(){

        return  transactionWithThread.get().getKey();

    }

    public static boolean inTcc(){
        return  currentTcc() != null;
    }

}
