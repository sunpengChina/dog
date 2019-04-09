package top.dogtcc.core.event;

import top.dogtcc.core.entry.DogTcc;

public class TccAchievementEvent extends TccEvent {


    public TccAchievementEvent(DogTcc source) {
        super(source);
    }

    @Override
    public DogTcc getSource() {
        return super.getSource();
    }

}