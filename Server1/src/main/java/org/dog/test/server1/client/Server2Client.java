package org.dog.test.server1.client;
import org.dog.test.server1.ReturnOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "server2",url = "127.0.0.1:8082")
public interface Server2Client {

    @RequestMapping("/test")
    String test() throws Exception;

    @RequestMapping("/tran")
    String tran(TranD tranD) throws Exception;

    @RequestMapping("/returnOrder")
    String returnOrder(ReturnOrder returnOrder) throws Exception;
}
