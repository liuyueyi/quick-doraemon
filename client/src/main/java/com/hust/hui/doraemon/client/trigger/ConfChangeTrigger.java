package com.hust.hui.doraemon.client.trigger;

import com.hust.hui.doraemon.client.MetaClient;
import com.hust.hui.doraemon.client.holder.MetaClientHolder;
import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;
import com.hust.hui.doraemon.core.entity.MetaConf;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by yihui on 2017/11/30.
 */
@Slf4j
public class ConfChangeTrigger {

    private static  final ConfChangeTrigger instance = new ConfChangeTrigger();

    private ConfChangeTrigger() {
    }


    public static ConfChangeTrigger getInstance() {
        return instance;
    }

    /**
     * 配置发生变更， 更新内存， 执行所有的监听器
     * @param config
     */
    public void notifyConfigChanged(MetaConf config) {
        MetaClient metaClient = MetaClientHolder.instance.getClient(config.getGroup());
        if (metaClient == null) {
            log.warn("no metaclient listen this config change! config: {}", config);
            return;
        }


        // 没有访问过这个配置，则直接过滤
        if(!metaClient.containMemConfig(config.getKey())) {
            return;
        }


        // 失效缓存中的value，并重新加载一下最新的配置
        metaClient.clearAndResetMemConfig(config.getKey());


        // 触发全局监听器
        List<IGlobalChangeListener> gListener = MetaClientHolder.instance.loadAllGroupChangeListener(config.getGroup());
        if (gListener != null) {
            gListener.forEach(l -> ConfChangeListenerProcessFactory.submit(l, config));
        }


        // 触发所有单key监听器
        List<ISingleChangeListener> sListener = MetaClientHolder.instance.loadAllSingleChangeListener(config.getGroup(), config.getKey());
        if (sListener != null) {
            sListener.forEach(l -> ConfChangeListenerProcessFactory.submit(l, config));
        }
    }

}
