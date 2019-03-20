package com.server2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = {"org.dog","com"})
@Configuration
public class Server2 {


    public static void main(String[] args) {

        SpringApplication.run(Server2.class, args);

    }


}