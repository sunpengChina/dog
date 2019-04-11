package top.dogtcc.test.server2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dogtcc.core.annotation.DogCallAnnotation;
import top.dogtcc.core.annotation.TccHandler;
import top.dogtcc.test.server2.client.CommonClient;
import top.dogtcc.test.server2.dao.TestEntry;

@RestController
public class CommonController extends TccHandler {

    @Autowired
    CommonClient commonClient;

    @RequestMapping("/test")
    @DogCallAnnotation(TccHandlerClass = CommonController.class)
    public String test(@RequestBody TestEntry good) throws Exception{

         if(good.getNext()!=null){

             commonClient.test(good.getNext());
         }

         if(!good.isSuccess()){

             throw  new Exception("error!");
         }

         return  "OK";
    }

}
