package org.dog.database.core.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectUtil {


    public  static  Annotation[][]  getMethodArgAnnotations(ProceedingJoinPoint pjp) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{

        MethodInvocationProceedingJoinPoint methodJoinPoint = (MethodInvocationProceedingJoinPoint) pjp;

        Field methodInvocationfield = methodJoinPoint.getClass().getDeclaredField("methodInvocation");

        methodInvocationfield.setAccessible(true);

        //1参数的标注调用反射类
        Annotation[][] argAnnotations = ((ReflectiveMethodInvocation)methodInvocationfield.get(methodJoinPoint)).getMethod().getParameterAnnotations();

        return  argAnnotations;
    }


}
