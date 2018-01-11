package com.hust.hui.doraemon.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class NumUtil {

    public static int str2int(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            log.error("parse str2int error! s:{}, defaultValue:{}, e:{}", s, defaultValue, e);
            return defaultValue;
        }
    }

}
