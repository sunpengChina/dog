package top.dogtcc.intercept.spring;

import top.dogtcc.intercept.spring.application.ApplicationStartListener;
import top.dogtcc.intercept.spring.feign.FeignInterceptor;
import top.dogtcc.intercept.spring.web.WebInterceptor;
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
