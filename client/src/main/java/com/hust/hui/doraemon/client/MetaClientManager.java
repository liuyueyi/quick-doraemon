package com.hust.hui.doraemon.client;

import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.manager.IMetaConfManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class MetaClientManager {

    @Getter
    @Setter
    private IMetaConfManager metaConfManager;


    private static class InnerClz {
        static MetaClientManager manager = new MetaClientManager();
    }

    public static MetaClientManager getInstance() {
        return InnerClz.manager;
    }


    /**
     * 获取实际的配置信息
     *
     * @param group
     * @param key
     * @return
     */
    public MetaResponse<MetaConf> getConfiguration(String group, String key) {
        return metaConfManager.getConfig(group, key);
    }


    /**
     * 设置一个已经存在的配置向的value
     *
     * @param group
     * @param key
     * @param value
     * @return
     */
    public MetaResponse<Boolean> setConfiguration(String group, String key, String value) {
        return metaConfManager.updateConfig(group, key, value);
    }


    /**
     * 设置 or 新增一个配置项的信息
     * @param group
     * @param key
     * @param value
     * @return
     */
    public MetaResponse<Boolean> setOrAddConfiguration(String group, String key, String value) {
        return metaConfManager.updateOrAddConfigIfNo(group, key, value);
    }


    /**
     * 获取发生变更的配置信息
     *
     * @param group
     * @return
     */
    public MetaResponse<MetaConf> getChangeConfig(String group) {
        return metaConfManager.popChange(group);
    }

}
