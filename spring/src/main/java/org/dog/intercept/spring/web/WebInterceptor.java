package org.dog.intercept.spring.web;

import org.dog.core.entry.DogTcc;
import org.dog.core.common.ThreadManager;
import org.apache.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebInterceptor implements HandlerInterceptor {


    private static Logger logger = Logger.getLogger(WebInterceptor.class);

    public boolean isInTransaction() {
        return inTransaction;
    }

    public void setInTransaction(boolean inTransaction) {
        this.inTransaction = inTransaction;
    }

    private  boolean  inTransaction = false;

    private DogTcc getTransaction(HttpServletRequest request){

        String tranHeader= request.getHeader(DogTcc.NameHeader);

        String tranKey= request.getHeader(DogTcc.KeyHeader);

        String tranApplication = request.getHeader(DogTcc.ApplicationHeader);

        if(tranHeader!=null){

            return  new DogTcc(tranApplication,tranHeader,tranKey);

        }else {

            return  null;
        }
    }



    public   boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /**
         * 清理线程
         */
        ThreadManager.clearTcc();

        DogTcc transaction = getTransaction(request);

        /**
         *  该服务被事务框架管理
         */
        if(transaction != null){

            setInTransaction(true);

            ThreadManager.setTcc(transaction);

            logger.info("事务性调用："+ transaction.toString());

       }else {

            setInTransaction(false);

            logger.info("非事务性调用");
        }


        return true;
    }

    public  void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }



    public  void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        /**
         * 清理线程
         */
        ThreadManager.clearTcc();

    }


}