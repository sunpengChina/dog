package top.dogtcc.test.server2.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import top.dogtcc.test.server2.dao.TestEntry;

@FeignClient(name = "goodsserver",url = "127.0.0.1:8083")
//@FeignClient(name = "orderserver",url = "127.0.0.1:8082")
public interface CommonClient {

    @RequestMapping("/test")
    String test(TestEntry entry) throws Exception;

}
