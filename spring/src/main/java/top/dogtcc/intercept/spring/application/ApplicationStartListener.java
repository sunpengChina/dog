package top.dogtcc.intercept.spring.application;

import top.dogtcc.core.jms.exception.ConnectException;
import top.dogtcc.core.common.IServer;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import top.dogtcc.core.jms.exception.TccNotExsitException;

public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = Logger.getLogger(ApplicationStartListener.class);

    /**
     * onApplicationEvent会被多次调用，但只有第一次的时候启动服务
     */
    private boolean firstEvent = true;

    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(!firstEvent){

            return;

        }else {

            firstEvent = false;

            IServer center =  event.getApplicationContext().getBean(IServer.class);

            try {

                center.connect();

            }catch (ConnectException |InterruptedException e){

                logger.error(e);
            }

        }

    }

}

