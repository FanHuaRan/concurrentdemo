package com.fhr.concurrentdemo.logj;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author:
 * @create: 2018-03-24 11:49
 **/
public class LoggerServiceClass implements  ILoggerService{

    private static  final  int DEFAULT_TIMEOUT = 200;

    private static  final TimeUnit DEFAULT_CLOSE_UNIT = TimeUnit.MICROSECONDS;

    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    private final Writer writer = null;

    protected  LoggerServiceClass(){

    }

    @Override
    public void start() {
        //nothing
    }

    @Override
    public void log(String msg) {
        exec.submit(()->{
            // write log to file
        });
    }

    @Override
    public void stop() throws  InterruptedException {
        stop(DEFAULT_TIMEOUT,DEFAULT_CLOSE_UNIT);
    }

    @Override
    public void stop(int timeOut, TimeUnit unit) throws  InterruptedException {
        try{
            exec.shutdown();
            exec.awaitTermination(timeOut,unit);
        }finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
