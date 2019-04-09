package top.dogtcc.core.event;

import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.common.Pair;


import java.util.List;

public class CallNodeOfflineEvent extends TccEvent{


    private List<Pair<DogCall,byte[]>> callPairs;

    public CallNodeOfflineEvent(DogTcc source, List<Pair<DogCall,byte[]>>  data) {
        super(source);
        this.callPairs = data;
    }

    @Override
    public DogTcc getSource() {
        return super.getSource();
    }

    public DogTcc getTcc() {
        return super.getSource();
    }

    public List<Pair<DogCall,byte[]>>  callPairs(){return  callPairs;}
}
