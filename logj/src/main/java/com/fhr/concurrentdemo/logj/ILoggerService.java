package com.fhr.concurrentdemo.logj;


import java.util.concurrent.TimeUnit;

public interface ILoggerService {

    void start();

    void log(String msg);

    void stop() throws  InterruptedException;

    void stop(int timeOut, TimeUnit unit) throws  InterruptedException;
}
