package com.hust.hui.doraemon.client.trigger;

import com.hust.hui.doraemon.client.trigger.base.DefaultThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yihui on 2017/11/30.
 */
public class TriggerThreadFactory {

    private static Map<String, Boolean> registGroupHistory = new ConcurrentHashMap<>();
    private static Lock registLock = new ReentrantLock();


    private static volatile AtomicInteger groupCount = new AtomicInteger();


    // 定时校验分组配置是否发生变更的线程池
    // 原则上，每次创建一个MetaClient， 这个计数+1
    private static ScheduledExecutorService scheduledExecutorService
            = Executors.newScheduledThreadPool(10, new DefaultThreadFactory("schedule-verifyConf-task"));


    public static void registerGroupTrigger(String group) {
        if (!registGroupHistory.containsKey(group)) {
            try {
                registLock.lock();
                registGroupHistory.put(group, true);

                groupCount.addAndGet(1);

                scheduledExecutorService.scheduleAtFixedRate(new TriggleTask(group), 1, 1, TimeUnit.MILLISECONDS);
            } finally {
                registLock.unlock();
            }
        }
    }
}
