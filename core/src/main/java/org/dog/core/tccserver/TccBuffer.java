package org.dog.core.tccserver;

import org.dog.core.entry.BytePack;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TccBuffer {

    private  final Map<DogTcc, List<Pair<DogCall, BytePack>>> localServerIndex = new ConcurrentHashMap<>();

    private  final  List<Pair<DogCall, BytePack>>  nomodify = Collections.unmodifiableList(new ArrayList<Pair<DogCall, BytePack>>());

    public void addCall(DogTcc tranPath, DogCall server, BytePack dataPack){

        if(localServerIndex.containsKey(tranPath)){

            localServerIndex.get(tranPath).add(new Pair<DogCall,BytePack>(server,dataPack));

        }else{

            List<Pair<DogCall, BytePack>>  servers = new ArrayList<Pair<DogCall, BytePack>>();

            servers.add(new Pair<DogCall,BytePack>(server,dataPack));

            localServerIndex.put(tranPath,servers);
        }

    }

    public List<Pair<DogCall, BytePack>> searchCalls(DogTcc tranPath){

        return  localServerIndex.getOrDefault(tranPath,nomodify);

    }

    public void deletTry(DogTcc tranPath){

        localServerIndex.remove(tranPath);

    }

}
