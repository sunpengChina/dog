package org.dog.database.core.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.database.core.annotation.DogTable;
import org.dog.database.core.annotation.QueryArg;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {

    public static Annotation[][] getParameterAnnotations(ProceedingJoinPoint pjp) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        MethodInvocationProceedingJoinPoint methodJoinPoint = (MethodInvocationProceedingJoinPoint) pjp;

        Field methodInvocationfield = methodJoinPoint.getClass().getDeclaredField("methodInvocation");

        methodInvocationfield.setAccessible(true);

        Annotation[][] argAnnotations = ((ReflectiveMethodInvocation) methodInvocationfield.get(methodJoinPoint)).getMethod().getParameterAnnotations();

        return argAnnotations;
    }

    public  static boolean iterable(Object object){

         return  Iterable.class.isAssignableFrom(object.getClass());
    }

    public static List<Object> getFields(Object object,Class<? extends Annotation> clazzAnnotationClazz,Class<? extends Annotation> fieldAnnotationClazz) throws IllegalArgumentException, IllegalAccessException {

        List<Object> argObjs = new ArrayList<>();

        if (object.getClass().getAnnotation(clazzAnnotationClazz) != null) {

            for (Field field : object.getClass().getDeclaredFields()) {

                Annotation fieldAnnotation = field.getAnnotation(fieldAnnotationClazz);

                if (fieldAnnotation != null) {

                    field.setAccessible(true);

                    argObjs.add(field.get(object));

                }
            }
        }

        return argObjs;
    }

    public static boolean allIterable(Class<?>[] clazzs){

        for(Class<?> clazz : clazzs){

            if(!Iterable.class.isAssignableFrom(clazz)){

                return  false;

            }
        }
        return  true;
    }

    public static boolean noneIterable(Class<?>[] clazzs){

        for(Class<?> clazz : clazzs){

            if(Iterable.class.isAssignableFrom(clazz)){

                return  false;

            }
        }
        return  true;
    }

    public  static Method getMethod(Class<?> clazz, String methodStr) throws NoSuchMethodException {

        Method[] fullMethod = new Method[clazz.getMethods().length + clazz.getDeclaredMethods().length];

        System.arraycopy(clazz.getMethods(), 0, fullMethod, 0, clazz.getMethods().length);

        System.arraycopy(clazz.getDeclaredMethods(), 0, fullMethod,  clazz.getMethods().length, clazz.getDeclaredMethods().length);

        for (Method method : clazz.getMethods()) {

            if (method.getName().equals(methodStr)) {

                method.setAccessible(true);

                return method;

            }
        }

        throw new NoSuchMethodException(methodStr);
    }

    public static List<Method> getMethods(Class<?>  clazz ,String methodStr) {

        List<Method> methods = new ArrayList<>();

        Method[] fullMethod = new Method[clazz.getMethods().length + clazz.getDeclaredMethods().length];

        System.arraycopy(clazz.getMethods(), 0, fullMethod, 0, clazz.getMethods().length);

        System.arraycopy(clazz.getDeclaredMethods(), 0, fullMethod,  clazz.getMethods().length, clazz.getDeclaredMethods().length);

        for (Method method : fullMethod) {

            if (method.getName().equals(methodStr)) {

                method.setAccessible(true);

                methods.add(method);

            }
        }

        return methods;
    }

    public static List<Method> getMethods(Class<?>  clazz ,String methodStr,int parameterNum) throws NoSuchMethodException {

        List<Method> methods = getMethods(clazz,methodStr);

        List<Method> filteredMethods = new ArrayList<>();

        for(Method method:methods){

            if(method.getParameters().length == parameterNum){

                filteredMethods.add(method);
            }
        }

        return filteredMethods;
    }

}
