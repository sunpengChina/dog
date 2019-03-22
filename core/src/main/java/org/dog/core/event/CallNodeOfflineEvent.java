package org.dog.core.event;

import org.dog.core.entry.DogCall;
import org.dog.core.entry.DogTcc;
import org.dog.core.util.Pair;


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
