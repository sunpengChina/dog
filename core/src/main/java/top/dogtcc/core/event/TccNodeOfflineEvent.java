package top.dogtcc.core.event;

import top.dogtcc.core.entry.DogTcc;

public class TccNodeOfflineEvent extends TccEvent {



    public TccNodeOfflineEvent(DogTcc source) {
        super(source);
    }

    @Override
    public DogTcc getSource() {
        return super.getSource();
    }

}
