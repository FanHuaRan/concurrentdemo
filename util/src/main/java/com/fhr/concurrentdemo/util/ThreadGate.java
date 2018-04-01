package com.fhr.concurrentdemo.util;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * @author FanHuaran
 * @description 可重用的一个闭锁, 基于代和内置锁
 * @create 2018-04-01 16:11
 **/
@ThreadSafe
public class ThreadGate {
    private final Object locker = new Object();
    //条件微词 (isOpen||generation>n)
    @GuardedBy("locker")
    private boolean isOpen;
    @GuardedBy("locker")
    private int generation;

    public void close() {
        synchronized (locker) {
            isOpen = false;
        }
    }

    public void open() {
        synchronized (locker) {
            generation++;
            isOpen = true;
            locker.notifyAll();
        }
    }

    public void await() throws InterruptedException {
        synchronized (locker) {
            int arrivalGeneration = generation;
            while (!isOpen || arrivalGeneration == generation) {
                locker.wait();
            }
        }
    }

}
