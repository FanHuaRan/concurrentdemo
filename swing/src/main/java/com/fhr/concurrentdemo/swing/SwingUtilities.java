package com.fhr.concurrentdemo.swing;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author FanHuaran
 * @description Swing单线程操作组件
 * @create 2018-04-02 00:07
 **/

public class SwingUtilities {
    /**
     * swingthread需要保证可见性
     */
    private static volatile Thread swingThread;

    /**
     * 单线程执行器
     */
    private static final ExecutorService exec = Executors.newSingleThreadExecutor(runnable -> {
        swingThread = new Thread(runnable);
        return swingThread;
    });

    /**
     * 判断当前线程是否是事件分发线程
     * @return
     */
    public static  boolean isEventDispatcherThread(){
        return  Thread.currentThread() == swingThread;
    }

    /**
     * 加入事件分发线程工作队列，稍后执行
     * @param task
     */
    public static  void invokeLater(Runnable task){
        exec.execute(task);
    }

    /**
     * 加入事件分发线程工作队列，稍后执行并阻塞等待完成
     * @param task
     * @throws Throwable
     */
    public  static  void invokeAndWait(Runnable task) throws Throwable {
        Future future = exec.submit(task);
        try {
            future.get();
        }catch (ExecutionException e){
            throw  e.getCause();
        }
    }

}
