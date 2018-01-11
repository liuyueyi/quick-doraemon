package com.hust.hui.doraemon.client;

import com.hust.hui.doraemon.client.entity.ClientRegisterConfig;
import com.hust.hui.doraemon.client.holder.MetaClientHolder;
import com.hust.hui.doraemon.client.trigger.TriggerThreadFactory;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class MetaClient {

    private ClientRegisterConfig registerConfig;

    public static MetaClient client(ClientRegisterConfig registerConfig) {
        // 首先判断Client是否已经注册，不是的则创建一个新的实例；
        MetaClient client = MetaClientHolder.instance.getClient(registerConfig.getGroup());
        if (client != null) {
            return client;
        }


        return new MetaClient(registerConfig);
    }

    private MetaClient(ClientRegisterConfig registerConfig) {
        this.registerConfig = registerConfig;
        registerMetaClient();
        registerConfChangeTrigger();
    }


    // 注册配置client, 保证应用中，一个分组只会存在一个MetaClient分组
    private void registerMetaClient() {
        MetaClientHolder.instance.registerClient(this.registerConfig.getGroup(), this);
    }


    // 注册监听回调触发器
    private void registerConfChangeTrigger() {

        // 全局监听器注册
        registerConfig.getGlobalChangeListenerList()
                .forEach(
                        k -> {
                            MetaClientHolder.instance.registerGlobalListener(registerConfig.getGroup(), k);
                            if (log.isDebugEnabled()) {
                                log.debug("MetaClient init! register global conf change listener success! " +
                                                "group:{} globalListener {}", registerConfig.getGroup(),
                                        k.getClass().getName());
                            }
                        }
                );


        // 单个key监听注册
        registerConfig.getSingleChangeListenerList()
                .forEach(
                        k -> {
                            MetaClientHolder.instance.registerSingleListener(registerConfig.getGroup(), k);
                            if (log.isDebugEnabled()) {
                                log.debug("MetaClient init! register single conf change listener success! " +
                                                "group:{} key:{} globalListener {}", registerConfig.getGroup(),
                                        k.getListenerKey(),
                                        k.getClass().getName());
                            }
                        }
                );


        // 注册轮询监听配置变动的触发器
        TriggerThreadFactory.registerGroupTrigger(this.registerConfig.getGroup());
    }


    /**
     * 从内存中获取配置新
     *
     * @param key
     * @return
     */
    private MetaConf getMemMetaConf(String key) {
        return MetaClientHolder.instance.getConfig(this.registerConfig.getGroup(), key);
    }


    /**
     * 将配置加载到内存中
     *
     * @param metaConf
     */
    private void loadMemMetaConf(MetaConf metaConf) {
        MetaClientHolder.instance.loadConfig(this.registerConfig.getGroup(), metaConf);
    }


    /**
     * 判断内存中是否包含配置
     *
     * @param key
     * @return
     */
    public boolean containMemConfig(String key) {
        MetaConf metaConf = getMemMetaConf(key);
        return metaConf != null;
    }


    /**
     * 删除内存中的配置信息
     *
     * @param key
     */
    private void clearMemConfig(String key) {
        MetaClientHolder.instance.removeConfig(this.registerConfig.getGroup(), key);
    }


    /**
     * 删除并重设内存中的配置信息
     *
     * @param key
     */
    public void clearAndResetMemConfig(String key) {
        clearMemConfig(key);
        getConfiguration(key);
    }


    public String getConfiguration(String key) {
        // 优先从内存中获取配置信息
        MetaConf metaConf = getMemMetaConf(key);
        if (metaConf == null) {
            // 内存不存在时，从数据源捞去配置；并更新内存
            MetaResponse<MetaConf> response = MetaClientManager.getInstance().getConfiguration(this.registerConfig.getGroup(), key);
            if (response.isSuccess()) {
                metaConf = response.getData();
                loadMemMetaConf(metaConf);
            }
        }


        if (metaConf == null) { // 从数据源中加载配置失败 1. 配值不存在场景， 2. 数据源异常
            return null;
        }


        return metaConf.getValue();
    }


    /**
     * 更新配置信息，不主动失效内存中的配置，
     * 由配置变动监听触发器来实现内存的刷新，这样可以保证所有的client端的内存都会被更新
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setConfiguration(String key, String value) {
        MetaResponse<Boolean> response = MetaClientManager.getInstance().setConfiguration(this.registerConfig.getGroup(), key, value);
        return response.isSuccess() && BooleanUtils.isTrue(response.getData());
    }


    public boolean setOrAddConfiguration(String key, String value) {
        MetaResponse<Boolean> response = MetaClientManager.getInstance().setOrAddConfiguration(this.registerConfig.getGroup(), key, value);
        return response.isSuccess() && BooleanUtils.isTrue(response.getData());
    }

}
