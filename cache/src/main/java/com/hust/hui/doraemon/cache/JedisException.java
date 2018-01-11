package com.hust.hui.doraemon.cache;

/**
 * Created by yihui on 2017/11/28.
 */
public class JedisException extends RuntimeException {
    public JedisException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
