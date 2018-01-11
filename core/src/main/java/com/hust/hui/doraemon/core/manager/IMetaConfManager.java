package com.hust.hui.doraemon.core.manager;

import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.entity.SourceConf;

/**
 * Created by yihui on 2017/11/28.
 */
public interface IMetaConfManager {

    MetaResponse<MetaConf> getConfig(String group, String key);

    MetaResponse<Boolean> addConfig(String group, String key, String value, String desc);

    MetaResponse<Boolean> updateConfig(String group, String key, String value);

    MetaResponse<Boolean> updateConfig(String group, String key, String value, String desc);

    MetaResponse<Boolean> updateOrAddConfigIfNo(String group, String key, String value);

    MetaResponse<Boolean> updateOrAddConfigIfNo(String group, String key, String value, String desc);

    MetaResponse<Boolean> pushChange(String group, String key, String value);

    MetaResponse<MetaConf>  popChange(String group);
}
