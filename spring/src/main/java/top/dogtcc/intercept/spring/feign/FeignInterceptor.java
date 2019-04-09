package top.dogtcc.intercept.spring.feign;

import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.common.ThreadManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.log4j.Logger;

public class FeignInterceptor implements RequestInterceptor {

    private static Logger logger = Logger.getLogger(FeignInterceptor.class);

    /**
     * 事务性的feign服务，需要将事务透传到下一个服务中
     * @param requestTemplate
     */
    public void apply(RequestTemplate requestTemplate) {

        /**
         *   获取线程中的事务传递给下一个服务
         */
        DogTcc transaction = ThreadManager.currentTcc();

        /**
         *   若无事务说明非事务性远程调用
         */
        if(transaction != null){

            logger.info("事务型远程调用:" + transaction.toString());

            requestTemplate.header(DogTcc.ApplicationHeader, transaction.getApplication());

            requestTemplate.header(DogTcc.NameHeader, transaction.getName());

            requestTemplate.header(DogTcc.KeyHeader, transaction.getKey());

        }else{

            logger.info("非事务型远程调用:" );

        }

    }


}
