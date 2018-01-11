package com.hust.hui.doraemon.client.listener;

import com.hust.hui.doraemon.core.entity.MetaConf;

/**
 * Created by yihui on 2017/11/29.
 */
public interface IGlobalChangeListener extends IConfChangeListener {

    void configChanged(MetaConf metaConf);

}
