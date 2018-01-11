package com.hust.hui.doraemon.cache.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.UUID;

/**
 * Created by yihui on 2018/1/11.
 */
public class DistributeLock {

    private static final Long OUT_TIME = 30L;

    public static String tryLock(Jedis jedis, String key) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        while (true) {
            String value = threadName + "_" + UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
            Long ans = jedis.setnx(key, value);
            if (ans != null && ans == 1) { // 获取锁成功
                return value;
            }

            // 锁获取失败, 判断是否超时
            String oldLock = jedis.get(key);
            if (oldLock == null) {
                continue;
            }

            long oldTime = Long.parseLong(oldLock.substring(oldLock.lastIndexOf("_") + 1));
            long now = System.currentTimeMillis();
            if (now - oldTime < OUT_TIME) { // 没有超时
                continue;
            }

            // 强制使所有的线程都可以到这一步
            Thread.sleep(50);
            System.out.println(threadName + " in getSet!");


            Transaction transaction = jedis.multi();
            jedis.watch(key);

            Response<String> getsetOldVal = transaction.getSet(key, value);
            System.out.println(threadName + " set redis value: " + value);

            // 人工接入，确保t1 获取到锁， t2 获取的是t1设置的内容， t3获取的是t2设置的内容
            if ("t1".equalsIgnoreCase(threadName)) {
                Thread.sleep(10);
            }else if ("t2".equalsIgnoreCase(threadName)) {
                Thread.sleep(20);
            } else if ("t3".equalsIgnoreCase(threadName)) {
                Thread.sleep(40);
            }

            transaction.exec();

            System.out.println("res: " + getsetOldVal.get());
            if (getsetOldVal.get() == null) { // 表示其他线程修改了value, 本次修改事务回滚
                continue;
            }
            if ("t1".equalsIgnoreCase(threadName)) {
                Thread.sleep(40);
            }
            System.out.println(threadName + " getLock!");
            jedis.unwatch();
            return value;
        }
    }

    public static void tryUnLock(Jedis jedis, String key, String uuid) {
        String ov = jedis.get(key);
        if (uuid.equals(ov)) { // 只释放自己的锁
            jedis.del(key);
            System.out.println(Thread.currentThread() +" del lock success!");
        } else {
            System.out.println(Thread.currentThread() +" del lock fail!");
        }
    }
}
