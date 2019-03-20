package org.dog.core.log;


import org.dog.core.entry.DogTcc;

public interface IHistoryLog {

    void log(DogTcc transaction);

}
