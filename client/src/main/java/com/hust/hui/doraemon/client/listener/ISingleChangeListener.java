package com.hust.hui.doraemon.client.listener;

import com.hust.hui.doraemon.core.entity.MetaConf;

/**
 * 监听单个key变动的事件
 * <p>
 * Created by yihui on 2017/11/29.
 */
public interface ISingleChangeListener extends IGlobalChangeListener {

    String getListenerKey();

    void configChanged(String value);

    default void configChanged(MetaConf metaConf) {
        configChanged(metaConf.getValue());
    }
}
