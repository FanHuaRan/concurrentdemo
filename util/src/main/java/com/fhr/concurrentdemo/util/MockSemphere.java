package com.fhr.concurrentdemo.util;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * @author FanHuaran
 * @description 模拟信号量实现，基于锁和状态变量
 * @create 2018-04-01 15:56
 **/
@ThreadSafe
public class MockSemphere {
    private final Object locker = new Object();
    @GuardedBy("locker")
    private int availablePerimits;

    public MockSemphere(int availablePerimits) {
        this.availablePerimits = availablePerimits;
    }

    public void accire() throws InterruptedException {
        synchronized (locker) {
            while (availablePerimits <= 0) {
                locker.wait();
            }
            availablePerimits--;
            locker.notifyAll();
        }
    }

    public void release() {
        synchronized (locker) {
            availablePerimits++;
            locker.notifyAll();
        }
    }
}
