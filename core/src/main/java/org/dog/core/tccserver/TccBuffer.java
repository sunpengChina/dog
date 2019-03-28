package org.dog.core.tccserver;

import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import org.dog.core.util.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TccBuffer {

    private  final Map<DogTcc, List<Pair<DogCall, TccContext>>> localServerIndex = new ConcurrentHashMap<>();

    private  final  List<Pair<DogCall, TccContext>>  nomodify = Collections.unmodifiableList(new ArrayList<Pair<DogCall, TccContext>>());

    public void addCall(DogTcc tranPath, DogCall server, TccContext dataPack){

        if(localServerIndex.containsKey(tranPath)){

            localServerIndex.get(tranPath).add(new Pair<DogCall, TccContext>(server,dataPack));

        }else{

            List<Pair<DogCall, TccContext>>  servers = new ArrayList<Pair<DogCall, TccContext>>();

            servers.add(new Pair<DogCall, TccContext>(server,dataPack));

            localServerIndex.put(tranPath,servers);
        }

    }

    public List<Pair<DogCall, TccContext>> searchCalls(DogTcc tranPath){

        return  localServerIndex.getOrDefault(tranPath,nomodify);

    }

    public void deletTry(DogTcc tranPath){

        localServerIndex.remove(tranPath);

    }

}
