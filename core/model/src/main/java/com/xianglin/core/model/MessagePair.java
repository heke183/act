package com.xianglin.core.model;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-02-06 16:40
 */
public class MessagePair<A, B> implements Delayed {

    private long delay;

    private A a;

    private B b;


    public MessagePair(A a, B b) {
        this(a, b, 10000);
    }

    public MessagePair(A a, B b, Date endDate, long delayMillis) {
        this.a = a;
        this.b = b;
        this.delay = endDate.getTime() + delayMillis;
    }

    public MessagePair(A a, B b, long timeMillis) {
        this.a = a;
        this.b = b;
        this.delay = System.currentTimeMillis() + timeMillis;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}
