package com.fhr.concurrentdemo.logj;

/**
 * @description:
 * @author:
 * @create: 2018-03-24 12:11
 **/
public class LoggerClass implements  ILogger{

    private final ILoggerService loggerService;

    private final  String  name;

    public LoggerClass(ILoggerService loggerService, String name) {
        this.loggerService = loggerService;
        this.name = name;
    }

    @Override
    public void info(String msg) {
        loggerService.log(msg);
    }

    @Override
    public void debug(String msg) {
        loggerService.log(msg);
    }

    @Override
    public void warining(String msg) {
        loggerService.log(msg);
    }

    @Override
    public void error(String msg, Throwable throwable) {
        loggerService.log(msg);
    }
}
