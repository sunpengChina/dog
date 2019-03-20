package org.dog.test.server1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication(scanBasePackages = {"org.dog.core","org.dog.intercept","org.dog.message","org.dog.test.server1"})
@EnableFeignClients
@EnableAspectJAutoProxy
@Configuration
@EnableDiscoveryClient
public class Server1Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Server1Application.class, args);

    }
}