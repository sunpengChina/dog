package com.server1.application.service;

import com.server1.application.client.Server2Client;
import org.dog.core.annotation.DogTccAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Server {

    @Autowired
    Server2Client client2;

    @DogTccAnnotation(name = "getValueFromServer2")
   public  String getValueFromServer2()throws Exception{

        return    client2.tran() ;

   }

}
