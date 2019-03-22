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


    @DogTccAnnotation(name = "chainTcc")
    public  String chainTcc()throws Exception{

        client2.tran(new TranD("abc","efg"));

        client3.tran(new TranD("hig","lmn"));

        return  "OK";

    }


    @DogTccAnnotation(name = "singleTcc")
    public  String singleTcc()throws Exception{

        client2.tran(new TranD("abc","efg"));

        return  "OK";

   }


    public  String noTcc()throws Exception{

        client2.tran(new TranD("abc","efg"));

        return  "OK";

    }

}
