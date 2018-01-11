package com.hust.hui.doraemon.client.listener;

/**
 * Created by yihui on 2017/11/29.
 */
public interface IConfChangeListener {
    default int getOrder() {
        return 10;
    }
}
