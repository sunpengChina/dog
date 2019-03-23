package org.dog.test.server2.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "server3",url = "127.0.0.1:8083")
public interface Server3Client {

    @RequestMapping("/test")
    String test() throws Exception;

    @RequestMapping("/tran")
    String tran(TranD tranD) throws Exception;

}
