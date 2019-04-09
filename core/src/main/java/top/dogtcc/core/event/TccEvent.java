package top.dogtcc.core.event;

import top.dogtcc.core.entry.DogTcc;

import java.util.EventObject;

public class TccEvent  extends EventObject {

    public TccEvent(DogTcc source) {
        super(source);
    }

    @Override
    public DogTcc getSource() {
        return (DogTcc)super.getSource();
    }
}
