package org.dog.core.common;

import org.dog.core.entry.TccContext;
import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ContextBuffer {

    private  final Map<DogTcc, List<Pair<DogCall, TccContext>>> buffer = new ConcurrentHashMap<>();

    private  final  List<Pair<DogCall, TccContext>>  nomodify = Collections.unmodifiableList(new ArrayList<Pair<DogCall, TccContext>>());

    public void put(DogTcc tranPath, DogCall server, TccContext context){

        if(buffer.containsKey(tranPath)){

            buffer.get(tranPath).add(new Pair<DogCall, TccContext>(server,context));

        }else{

            List<Pair<DogCall, TccContext>>  servers = new ArrayList<Pair<DogCall, TccContext>>();

            servers.add(new Pair<DogCall, TccContext>(server,context));

            buffer.put(tranPath,servers);
        }

    }

    public List<Pair<DogCall, TccContext>> find(DogTcc tranPath){

        return  buffer.getOrDefault(tranPath,nomodify);

    }

    public void clear(DogTcc tranPath){

        buffer.remove(tranPath);

    }

}
