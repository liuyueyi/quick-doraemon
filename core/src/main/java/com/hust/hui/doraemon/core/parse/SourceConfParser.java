package com.hust.hui.doraemon.core.parse;

import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.SourceConf;
import com.hust.hui.doraemon.core.util.NumUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yihui on 2017/11/28.
 */
public class SourceConfParser {

    public static MetaConf parseMetaConf(String group, String key, String conf) {
        MetaConf metaConf = new MetaConf();
        metaConf.setGroup(group);
        metaConf.setKey(key);

        int index = conf.indexOf(SourceConf.SPLIT_TAG);
        if(index > 0) {
            metaConf.setValue(conf.substring(0, index));
        } else {
            metaConf.setValue(conf);
        }
        return metaConf;
    }


    public static SourceConf parseSourceConf(String group, String key, String conf) {
        SourceConf sourceConf = new SourceConf();
        sourceConf.setGroup(group);
        sourceConf.setKey(key);

        String[] cacheConfs = StringUtils.split(conf, SourceConf.SPLIT_TAG);
        if (cacheConfs.length > 1) {
            sourceConf.setValue(cacheConfs[0]);
        } else {
            sourceConf.setValue("");
        }

        if (cacheConfs.length > 2) {
            sourceConf.setDesc(cacheConfs[1]);
        } else {
            sourceConf.setDesc("");
        }

        if (cacheConfs.length == 3) {
            sourceConf.setVersion(NumUtil.str2int(cacheConfs[2], 1));
        } else {
            sourceConf.setVersion(1);
        }

        return sourceConf;
    }
}
