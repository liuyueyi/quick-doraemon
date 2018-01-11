package com.hust.hui.doraemon.core.loader;

import com.hust.hui.doraemon.cache.CacheWrapper;
import com.hust.hui.doraemon.cache.JedisException;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.entity.SourceConf;
import com.hust.hui.doraemon.core.entity.Status;
import com.hust.hui.doraemon.core.parse.ConfChangeQueueParse;
import com.hust.hui.doraemon.core.parse.SourceConfParser;
import com.hust.hui.doraemon.core.rule.ConfChangeQueueKeyGenRule;
import com.hust.hui.doraemon.core.rule.MetaUniqKeyGenRule;
import com.hust.hui.doraemon.core.util.IpUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 从缓存中获取对应的配置信息
 * <p>
 * Created by yihui on 2017/11/28.
 */
public class CacheMetaLoader implements ILoader {

    @Getter
    @Setter
    private CacheWrapper cacheWrapper;

    public CacheMetaLoader() {
    }

    public CacheMetaLoader(CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }


    private String loadConf(String group, String key) {
        String uniqKey = MetaUniqKeyGenRule.genConfUniqKey(group, key);
        return cacheWrapper.get(uniqKey);
    }


    /**
     * 从缓存中获取配置信息
     *
     * @param group
     * @param key
     * @return
     */
    public MetaResponse<MetaConf> load(String group, String key) {
        String cacheConf = loadConf(group, key);
        if (cacheConf == null) {
            return MetaResponse.buildFail(Status.FAIL_NOT_EXISTS);
        }


        return MetaResponse.buildSuccess(SourceConfParser.parseMetaConf(group, key, cacheConf));
    }


    /**
     * 单纯的写入， 如果存在，则失败
     *
     * @param sourceConf
     * @return
     */
    @Override
    public MetaResponse<Boolean> dump(SourceConf sourceConf) {
        String key = MetaUniqKeyGenRule.genConfUniqKey(sourceConf.getGroup(), sourceConf.getKey());
        String value = sourceConf.getSourceValue();

        try {
            boolean ans = cacheWrapper.setnx(key, value);
            if (ans) {
                return MetaResponse.buildSuccess(ans);
            } else {
                return MetaResponse.buildFail(Status.FAIL_ALREADY_EXISTS);
            }
        } catch (JedisException e) {
            return MetaResponse.buildFail(Status.ERROR_CACHE);
        }
    }


    @Override
    public MetaResponse<Boolean> update(SourceConf conf) {
        String cacheConf = loadConf(conf.getGroup(), conf.getKey());

        if (cacheConf == null) {
            return MetaResponse.buildFail(Status.FAIL_NOT_EXISTS);
        }


        SourceConf source = SourceConfParser.parseSourceConf(conf.getGroup(), conf.getKey(), cacheConf);
        if (conf.getDesc() == null) { // 用原来的描述覆盖
            conf.setDesc(source.getDesc());
        }

        if (conf.getVersion() == null) {
            conf.setVersion(source.getVersion());
        }


        String newCacheConf = conf.getSourceValue();
        String key = MetaUniqKeyGenRule.genConfUniqKey(conf.getGroup(), conf.getKey());

        try {
            return MetaResponse.buildSuccess(cacheWrapper.set(key, newCacheConf));
        } catch (JedisException e) {
            return MetaResponse.buildFail(Status.ERROR_CACHE);
        }
    }



    /**
     * 存在则更新配置
     * 不存在则新加配置到缓存
     *
     * @param conf
     * @return
     */
    public MetaResponse<Boolean> dumpUpdateIfExists(SourceConf conf) {
        String cacheConf = loadConf(conf.getGroup(), conf.getKey());

        if (cacheConf == null) {
            conf.setVersion(1);
            if (conf.getDesc() == null) {
                conf.setDesc("");
            }
        } else {
            SourceConf source = SourceConfParser.parseSourceConf(conf.getGroup(), conf.getKey(), cacheConf);
            if (conf.getDesc() == null) { // 用原来的描述覆盖
                conf.setDesc(source.getDesc());
            }

            if (conf.getVersion() == null) {
                conf.setVersion(source.getVersion());
            }
        }


        String newCacheConf = conf.getSourceValue();
        String key = MetaUniqKeyGenRule.genConfUniqKey(conf.getGroup(), conf.getKey());
        try {
            return MetaResponse.buildSuccess(cacheWrapper.set(key, newCacheConf));
        } catch (JedisException e) {
            return MetaResponse.buildFail(Status.ERROR_CACHE);
        }
    }


    /**
     * 更新/新增时，主动触发
     * @param sourceConf
     * @return
     */
    public MetaResponse<Boolean> pushChange(SourceConf sourceConf) {
        String queueKey = ConfChangeQueueKeyGenRule.genQueueKey(sourceConf.getGroup(), IpUtils.getLocalIpByNetcard());
        String conf = ConfChangeQueueParse.parseSourceToStrConf(sourceConf);

        try {
            return MetaResponse.buildSuccess(cacheWrapper.push(queueKey, conf));
        } catch (JedisException e) {
            return MetaResponse.buildFail(Status.ERROR_CACHE);
        }
    }



    public MetaResponse<MetaConf> popChange(String group) {
        String queueKey = ConfChangeQueueKeyGenRule.genQueueKey(group, IpUtils.getLocalIpByNetcard());

        try {
            String changeConf = cacheWrapper.pop(queueKey);
            if (changeConf == null) {
                return MetaResponse.buildSuccess(null);
            }

            return MetaResponse.buildSuccess(ConfChangeQueueParse.parseStrConfToMeta(group, changeConf));
        } catch (JedisException e) {
            return MetaResponse.buildFail(Status.ERROR_CACHE);
        }
    }
}
