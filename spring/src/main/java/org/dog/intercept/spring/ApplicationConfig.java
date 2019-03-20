package org.dog.intercept.spring;

import org.dog.intercept.spring.application.ApplicationStartListener;
import org.dog.intercept.spring.feign.FeignInterceptor;
import org.dog.intercept.spring.web.WebInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Bean
    public ApplicationStartListener applicationStartListener(){

        return new ApplicationStartListener();

    }

    @Bean
    public RequestInterceptor headerInterceptor() {

        return  new FeignInterceptor();

    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebInterceptor());
    }

}
