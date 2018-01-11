package com.hust.hui.doraemon.core.parse;

import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.SourceConf;
import com.hust.hui.doraemon.core.util.NumUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yihui on 2017/11/30.
 */
public class ConfChangeQueueParse {

    public static String parseSourceToStrConf(SourceConf sourceConf) {
        return sourceConf.getKey() + SourceConf.SPLIT_TAG + sourceConf.getValue() + SourceConf.SPLIT_TAG + sourceConf.getVersion();
    }


    public static SourceConf parseStrConfToSource(String group, String conf) {
        String[] strs = StringUtils.split(conf, SourceConf.SPLIT_TAG);
        SourceConf sourceConf = new SourceConf();
        sourceConf.setGroup(group);
        sourceConf.setKey(strs[0]);
        sourceConf.setValue(strs[1]);
        sourceConf.setVersion(NumUtil.str2int(strs[2], 1));
        return sourceConf;
    }

    public static MetaConf parseStrConfToMeta(String group, String conf) {
        String[] strs = StringUtils.split(conf, SourceConf.SPLIT_TAG);
        MetaConf metaConf = new MetaConf();
        metaConf.setGroup(group);
        metaConf.setKey(strs[0]);
        metaConf.setValue(strs[1]);
        return metaConf;
    }
}
