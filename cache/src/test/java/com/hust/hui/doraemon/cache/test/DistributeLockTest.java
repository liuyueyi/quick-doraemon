package com.hust.hui.doraemon.cache.test;

import com.hust.hui.doraemon.cache.CacheWrapper;
import com.hust.hui.doraemon.cache.lock.DistributeLock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by yihui on 2018/1/11.
 */
public class DistributeLockTest {

    private CacheWrapper cacheWrapper;

    @Before
    public void init() throws Exception {
        // 1. build方式创建
//        cacheWrapper = CacheWrapperBuild.builder()
//                .setMasterConf("127.0.0.1:6379")
//                .setSlaveConf("127.0.0.1:6379")
//                .setTimeout(60)
//                .setDatabase(5)
//                .setMaxIdle(4)
//                .setMinIdle(2)
//                .setMaxTotal(10)
//                .build();

        // 2. 如果用spring方式启动， 可以直接引入 test-cache.xml, 使用@Autowired加载
        ApplicationContext apc = new ClassPathXmlApplicationContext("classpath:spring/test-cache.xml");
        cacheWrapper = apc.getBean("cacheWrapper", CacheWrapper.class);
    }


    @Test
    public void testLock() throws InterruptedException {
        JedisPool jedisPool = cacheWrapper.getJedisPool(0);
        Jedis jedis = jedisPool.getResource();
        String lockKey = "lock_test";


        String old = DistributeLock.tryLock(jedis, lockKey);
        System.out.println("old lock: " + old);

        // 确保锁超时
        Thread.sleep(40);


        // 创建三个线程
        Thread t1 = new Thread(() -> {
            try {
                Jedis j =jedisPool.getResource();
                DistributeLock.tryLock(j, lockKey);
                System.out.println("t1 >>>> " + j.get(lockKey));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            try {
                Jedis j =jedisPool.getResource();
                DistributeLock.tryLock(j, lockKey);
                System.out.println("t2 >>>>> " + j.get(lockKey));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");
        Thread t3 = new Thread(() -> {
            try {
                Jedis j =jedisPool.getResource();
                DistributeLock.tryLock(j, lockKey);
                System.out.println("t3 >>>>> " + j.get(lockKey));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t3");


        t1.start();
        t2.start();
        t3.start();


        Thread.sleep(10000);
    };


}
