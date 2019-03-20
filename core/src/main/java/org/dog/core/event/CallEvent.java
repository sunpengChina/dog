package org.dog.core.event;

import org.dog.core.entry.DogCall;

import java.util.EventObject;

public class CallEvent extends EventObject {

    public CallEvent(DogCall source) {
        super(source);
    }

    @Override
    public DogCall getSource() {
        return (DogCall)super.getSource();
    }
}
