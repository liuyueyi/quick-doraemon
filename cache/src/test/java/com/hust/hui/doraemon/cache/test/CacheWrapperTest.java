package com.hust.hui.doraemon.cache.test;

import com.hust.hui.doraemon.cache.CacheWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class CacheWrapperTest {

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
    public void testSetAndGet() {
        String key = "test123";
        String value = "hello , world!";

        boolean ans = cacheWrapper.set(key, value, 100);
        log.info("put into cache res: {}", ans);


        String result = cacheWrapper.get(key);
        Assert.assertTrue(value.equals(result));
    }



    @Test
    public void testQueue() {
        String qKey = "queueKey";

        cacheWrapper.push(qKey, "1234");

        String val = cacheWrapper.pop(qKey);
        System.out.println(val);
        val = cacheWrapper.pop(qKey);
        System.out.println(val);
    }

}
