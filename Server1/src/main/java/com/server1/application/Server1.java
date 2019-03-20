package com.server1.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com", "org.dog"})
@Configuration
public class Server1 {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Server1.class, args);
    }
}