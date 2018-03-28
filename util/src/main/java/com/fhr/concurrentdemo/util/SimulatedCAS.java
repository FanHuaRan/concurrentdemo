package com.fhr.concurrentdemo.util;

/**
 * @author FanHuaran
 * @description 通过锁模拟CAS操作，
 * 实际上CAS操作的原子性是靠硬件支持的,速度比锁快得多
 * @create 2018-03-28 23:17
 **/
@ThreadSafe
public class SimulatedCAS {
    /**
     * CAS操作的变量，通过volatile修饰可以保证初始化的安全性
     */

    private volatile int value;

    public SimulatedCAS(int value) {
        this.value = value;
    }

    public synchronized int get() {
        return this.value;
    }

    public synchronized int compareAndSwap(int expectedValue, int newValue) {
        int oldValue = value;
        if (oldValue == expectedValue) {
            value = newValue;
        }
        return oldValue;
    }

    public synchronized boolean compareAndSet(int expectedValue, int newValue) {
        return expectedValue == compareAndSwap(expectedValue, newValue);
    }
}
