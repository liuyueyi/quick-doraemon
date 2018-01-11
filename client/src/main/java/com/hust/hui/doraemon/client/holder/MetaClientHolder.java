package com.hust.hui.doraemon.client.holder;

import com.hust.hui.doraemon.client.MetaClient;
import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;
import com.hust.hui.doraemon.core.entity.MetaConf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 持有应用中所有分组对应的MetaClient
 * <p>
 * Created by yihui on 2017/11/29.
 */
public class MetaClientHolder {

    public static final MetaClientHolder instance = new MetaClientHolder();


    /**
     * 内存中加载的配置信息
     * key: 分组名
     * value:
     * key : 配置key
     * value : 配置信息
     */
    private Map<String /* group */, Map<String /* configKey */, MetaConf>> groupConfMemCache = new ConcurrentHashMap<>();
    private volatile Lock groupConfLock = new ReentrantLock();


    private Map<String /* group */, MetaClient> clientCache = new ConcurrentHashMap<>();


    /**
     * 全局配置变更监听器
     */
    private Map<String /* group */, List<IGlobalChangeListener>> groupGlobalChangeCache = new ConcurrentHashMap<>();
    private volatile Lock globalRegisterLock = new ReentrantLock();


    /**
     * 耽搁key配置变更监听器
     */
    private Map<String /* group */, Map<String /* configKey*/, List<ISingleChangeListener>>> groupSingleChangeCache = new ConcurrentHashMap<>();
    private volatile Lock singleRegisterLock = new ReentrantLock();


    public MetaClient getClient(String group) {
        return clientCache.get(group);
    }

    public void registerClient(String group, MetaClient client) {
        clientCache.put(group, client);
    }


    public MetaConf getConfig(String group, String key) {
        Map<String, MetaConf> confMap = groupConfMemCache.get(group);
        if (confMap == null) { // 内存中没有
            return null;
        }

        return confMap.get(key);
    }


    public MetaConf removeConfig(String group, String key) {
        Map<String, MetaConf> confMap = groupConfMemCache.get(group);
        if(confMap == null) {
            return null;
        }

        return confMap.remove(key);
    }


    /**
     * 将配置加载到内存中
     *
     * @param group
     * @param conf
     */
    public void loadConfig(String group, MetaConf conf) {
        Map<String, MetaConf> confMap = groupConfMemCache.get(group);
        if (confMap == null) {
            try {
                groupConfLock.lock();
                confMap = groupConfMemCache.computeIfAbsent(group, k -> new ConcurrentHashMap<>());
            } finally {
                groupConfLock.unlock();
            }
        }
        confMap.put(conf.getKey(), conf);
    }


    public void registerGlobalListener(String group, IGlobalChangeListener globalChangeListener) {
        List<IGlobalChangeListener> list = groupGlobalChangeCache.get(group);
        if (list == null) {
            // 直接这么添加会有并发问题， 如同一时刻，两个线程同时初始化列表，并写入map；则会导致丢掉一个全局变更的注册
//            list = new CopyOnWriteArrayList<>();
//            groupGlobalChangeCache.put(group, list);

            try {
                globalRegisterLock.lock();
                list = groupGlobalChangeCache.computeIfAbsent(group, k -> new CopyOnWriteArrayList<>());
            } finally {
                globalRegisterLock.unlock();
            }

        }

        list.add(globalChangeListener);
    }


    public List<IGlobalChangeListener> loadAllGroupChangeListener(String group) {
        List<IGlobalChangeListener> list = groupGlobalChangeCache.get(group);
        return list == null ? Collections.emptyList() : list;

    }


    public void registerSingleListener(String group, ISingleChangeListener singleChangeListener) {
        Map<String, List<ISingleChangeListener>> singleKeyMap = groupSingleChangeCache.get(group);
        if (singleKeyMap == null) {
            try {
                singleRegisterLock.lock();
                singleKeyMap = groupSingleChangeCache.computeIfAbsent(group, k -> new ConcurrentHashMap<>());
            } finally {
                singleRegisterLock.unlock();
            }
        }

        List<ISingleChangeListener> changeList = singleKeyMap.get(singleChangeListener.getListenerKey());
        if (changeList == null) {
            try {
                singleRegisterLock.lock();
                changeList = singleKeyMap.computeIfAbsent(singleChangeListener.getListenerKey(), k -> new CopyOnWriteArrayList<>());
            } finally {
                singleRegisterLock.unlock();
            }
        }

        changeList.add(singleChangeListener);
    }


    public List<ISingleChangeListener> loadAllSingleChangeListener(String group, String key) {
        Map<String, List<ISingleChangeListener>> map = groupSingleChangeCache.get(group);
        if (map == null) {
            return Collections.emptyList();
        }

        List<ISingleChangeListener> list = map.get(key);
        return list == null ? Collections.emptyList() : list;

    }

}
