package com.fhr.concurrentdemo.logj;

/**
 * @description:
 * @author:
 * @create: 2018-03-24 12:10
 **/
public interface ILogger {

    void info(String msg);

    void debug(String msg);

    void warining(String msg);

    void error(String msg,Throwable throwable);

}
