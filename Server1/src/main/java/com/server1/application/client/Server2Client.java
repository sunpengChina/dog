package com.server1.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "client2",url = "127.0.0.1:8082")
public interface Server2Client {
    @RequestMapping("/test")
    String test() throws Exception;

    @RequestMapping("/tran")
    String tran() throws Exception;
}
