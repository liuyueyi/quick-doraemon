package com.hust.hui.doraemon.core;

import com.hust.hui.doraemon.cache.CacheWrapper;
import com.hust.hui.doraemon.cache.builder.CacheWrapperBuild;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.manager.CacheMetaManager;
import com.hust.hui.doraemon.core.manager.IMetaConfManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class CacheMetaManagerTest {

    private CacheWrapper cacheWrapper;

    private IMetaConfManager cacheMetaManager;


    @Before
    public void init() {
        // 非spring方式启动
        cacheWrapper = CacheWrapperBuild.builder()
                .setMasterConf("127.0.0.1:6379")
                .setSlaveConf("127.0.0.1:6379")
                .setTimeout(60)
                .setDatabase(5)
                .setMaxIdle(4)
                .setMinIdle(2)
                .setMaxTotal(10)
                .build();


        // spring 方式使用
//        ApplicationContext apc = new ClassPathXmlApplicationContext("classpath:spring/doraemon-core.xml");
//        cacheWrapper = apc.getBean("cacheWrapper", CacheWrapper.class);

        cacheMetaManager = new CacheMetaManager(cacheWrapper);


    }


    private final String group = "testGroup";
    private final String key = "testKey";
    private final String value = "{\"test\":\"abcd\", \"num\": 345678991298}";
    private final String desc = "描述文案";


    @Test
    public void testSetConfig() {
        // 添加配置
        MetaResponse<Boolean> response = cacheMetaManager.addConfig(group, key, value, desc);
        System.out.println(response);


        // 查询配置
        MetaResponse<MetaConf> cacheConf = cacheMetaManager.getConfig(group, key);
        System.out.println(cacheConf);


        // 更新配置
        response = cacheMetaManager.updateConfig(group, key, "new value");
        System.out.println(response);



        cacheConf = cacheMetaManager.getConfig(group, key);
        System.out.println(cacheConf);
    }

}
