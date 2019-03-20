package org.dog.test.server1.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "server2")
public interface Server2Client {

    @RequestMapping("/test")
    String test() throws Exception;

    @RequestMapping("/tran")
    String tran(TranD tranD) throws Exception;

}
