package com.hust.hui.doraemon.client.test;

import com.hust.hui.doraemon.cache.CacheWrapper;
import com.hust.hui.doraemon.cache.builder.CacheWrapperBuild;
import com.hust.hui.doraemon.client.MetaClient;
import com.hust.hui.doraemon.client.MetaClientBuilder;
import com.hust.hui.doraemon.client.MetaClientManager;
import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.manager.CacheMetaManager;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by yihui on 2017/11/29.
 */
public class ClientTest {

    private MetaClient metaClient;

    private static final String GROUP = "testGrop";
    private static final String LISTEN_KEY = "testKey";

    @Before
    public void init() {
        // 非spring方式启动, cacheMetaManager 必须优先设置
        CacheWrapper cacheWrapper = CacheWrapperBuild.builder()
                .setMasterConf("127.0.0.1:6379")
                .setSlaveConf("127.0.0.1:6379")
                .setTimeout(60)
                .setDatabase(5)
                .setMaxIdle(4)
                .setMinIdle(2)
                .setMaxTotal(10)
                .build();
        CacheMetaManager cacheMetaManager = new CacheMetaManager(cacheWrapper);
        MetaClientManager.getInstance().setMetaConfManager(cacheMetaManager);


        metaClient = MetaClientBuilder.with(GROUP)
                .addGlobalListener(new IGlobalChangeListener() {
                    @Override
                    public void configChanged(MetaConf metaConf) {
                        System.out.println("global change! metaConf: " + metaConf);
                    }
                }).addSingleListener(new ISingleChangeListener() {
                    @Override
                    public String getListenerKey() {
                        return LISTEN_KEY;
                    }

                    @Override
                    public void configChanged(String metaConf) {
                        System.out.println("single change! key: " + getListenerKey() + " conf: " + metaConf);
                    }
                }).createMetaClient();
    }


    private void changeKey() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ans = false;
                while (true) {
                    if (ans) {
                        metaClient.setConfiguration(LISTEN_KEY, "hello world" + System.currentTimeMillis());
                        ans = !ans;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void printKey() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ans = false;
                while (true) {
                    if (ans) {
                        String value = metaClient.getConfiguration(LISTEN_KEY);
                        System.out.printf("value: " + value);
                        ans = !ans;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Test
    public void testClient() throws InterruptedException {
        // 起一个异步任务，用于debug更新配置值
        changeKey();


        // 起另一个异步任务，用于获取配置
        printKey();

        Thread.sleep(1000 * 3600);
    }



    @Test
    public void testConfigGet() {
        String value = metaClient.getConfiguration(LISTEN_KEY);
        System.out.println("value: " + value);


        // 更新value
        metaClient.setConfiguration(LISTEN_KEY, "now" + System.currentTimeMillis());


        String nvalue = metaClient.getConfiguration(LISTEN_KEY);
        System.out.println("nval: " + nvalue);
    }

}
