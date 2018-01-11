package com.hust.hui.doraemon.core.manager;

import com.hust.hui.doraemon.cache.CacheWrapper;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.entity.SourceConf;
import com.hust.hui.doraemon.core.loader.CacheMetaLoader;
import com.hust.hui.doraemon.core.loader.ILoader;
import lombok.Setter;

/**
 * 负责从数据源中读取配置信息；
 * 向数据源中写入配置信息
 * 更新数据源中的配置信息
 * <p>
 * Created by yihui on 2017/11/28.
 */
public class CacheMetaManager implements IMetaConfManager {

    @Setter
    private ILoader cacheMetaLoader;

    public CacheMetaManager() {
    }

    public CacheMetaManager(CacheWrapper cacheWrapper) {
        cacheMetaLoader = new CacheMetaLoader(cacheWrapper);
    }

    public MetaResponse<MetaConf> getConfig(String group, String key) {
        return cacheMetaLoader.load(group, key);
    }


    public MetaResponse<Boolean> addConfig(String group, String key, String value, String desc) {
        MetaResponse<Boolean> res = cacheMetaLoader.dump(new SourceConf(group, key, value, desc, 1));
        autoPushChange(group, key, value, res);
        return res;
    }


    public MetaResponse<Boolean> updateConfig(String group, String key, String value) {
        return updateConfig(group, key, value, null);
    }


    public MetaResponse<Boolean> updateConfig(String group, String key, String value, String desc) {
        MetaResponse<Boolean> res = cacheMetaLoader.update(new SourceConf(group, key, value, desc, null));
        autoPushChange(group, key, value, res);
        return res;
    }


    public MetaResponse<Boolean> updateOrAddConfigIfNo(String group, String key, String value) {
        return this.updateOrAddConfigIfNo(group, key, value, null);
    }


    public MetaResponse<Boolean> updateOrAddConfigIfNo(String group, String key, String value, String desc) {
        MetaResponse<Boolean> res = cacheMetaLoader.dumpUpdateIfExists(new SourceConf(group, key, value, desc, null));
        autoPushChange(group, key, value, res);
        return res;
    }


    private void autoPushChange(String group, String key, String value, MetaResponse<Boolean> res) {
        if (res.isSuccess()) {
            pushChange(group, key, value);
        }
    }

    @Override
    public MetaResponse<Boolean> pushChange(String group, String key, String value) {
        return cacheMetaLoader.pushChange(new SourceConf(group, key, value, null, null));
    }

    @Override
    public MetaResponse<MetaConf> popChange(String group) {
        return cacheMetaLoader.popChange(group);
    }
}
