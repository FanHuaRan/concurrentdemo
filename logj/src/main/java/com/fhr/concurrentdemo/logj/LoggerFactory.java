package com.fhr.concurrentdemo.logj;

/**
 * @description:
 * @author:
 * @create: 2018-03-24 12:15
 **/
public class LoggerFactory {

    public  static  final ILogger getLogger(String name){
        return  new LoggerClass(Inner.instance,name);
    }

    public  static  final ILogger getLogger(Class<?> classIns){
        return  new LoggerClass(Inner.instance,classIns.getName());
    }

    private static  class  Inner{
        private static  final  ILoggerService instance = new LoggerServiceClass();
    }

}
