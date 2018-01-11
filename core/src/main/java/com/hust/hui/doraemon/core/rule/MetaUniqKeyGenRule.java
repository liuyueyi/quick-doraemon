package com.hust.hui.doraemon.core.rule;

/**
 * 配置文件的唯一标识解析类
 * <p>
 * Created by yihui on 2017/11/28.
 */
public class MetaUniqKeyGenRule {

    private static final String PREFIX = "[MetaConf]";

    /**
     * 通过配置分组 + 配置key，可以唯一确定对应的配置信息
     *
     * @param group   配置分组
     * @param confKey 配置key
     * @return 返回唯一的索引
     */
    public static String genConfUniqKey(String group, String confKey) {
        return PREFIX + group + "_" + confKey;
    }

}
