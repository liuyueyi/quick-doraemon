package com.hust.hui.doraemon.core.rule;

/**
 * 配置变更时，会维护一个变更的队列，此处用于生成该队列的key
 * <p>
 * Created by yihui on 2017/11/29.
 */
public class ConfChangeQueueKeyGenRule {

    private static final String PREFIX = "[ChangeQueue]";


    /**
     * 每台机器，每个分组，都有一个独立的变更queue；
     *
     * @param group    应用注册的配置分组
     * @param consumer 应用所在机器 （建议是ip:port）
     * @return
     */
    public static String genQueueKey(String group, String consumer) {
        return PREFIX + group + "_" + consumer;
    }

}
