package org.dog.test.server1.service;
import org.dog.test.server1.client.Server2Client;
import org.dog.core.annotation.DogTccAnnotation;
import org.dog.test.server1.client.Server3Client;
import org.dog.test.server1.client.TranD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TransServer {

    @Autowired
    Server2Client client2;

    @Autowired
    Server3Client client3;

    @DogTccAnnotation(name = "tccfromserver1")
    public  String tccTran()throws Exception{

        client2.tran(new TranD("abc","efg"));

//        client3.tran(new TranD("hig","lmn"));

        return  "OK";

   }


    public  String nottccTran()throws Exception{

        client2.tran(new TranD("abc","efg"));

//        client3.tran(new TranD("hig","lmn"));

        return  "OK";

    }

}
