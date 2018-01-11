package com.hust.hui.doraemon.client.test;

import com.hust.hui.doraemon.client.trigger.base.DefaultThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yihui on 2017/11/30.
 */
public class ScheduleTest {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10, new DefaultThreadFactory("-test-"));
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + " now: " + System.currentTimeMillis());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);


        Thread.sleep(10000);
    }

}
