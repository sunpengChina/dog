package org.dog.core.event;

import org.dog.core.entry.DogTcc;

public class TccAchievementEvent extends TccEvent {


    public TccAchievementEvent(DogTcc source) {
        super(source);
    }

    @Override
    public DogTcc getSource() {
        return super.getSource();
    }

}