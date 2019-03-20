package org.dog.core.event;

import org.dog.core.entry.DogTcc;

public class TccNodeOfflineEvent extends TccEvent {



    public TccNodeOfflineEvent(DogTcc source) {
        super(source);
    }

    @Override
    public DogTcc getSource() {
        return super.getSource();
    }

}
