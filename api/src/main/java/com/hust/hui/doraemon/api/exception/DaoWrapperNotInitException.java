package com.hust.hui.doraemon.api.exception;

/**
 * Created by yihui on 2018/1/12.
 */
public class DaoWrapperNotInitException extends Exception {

    private static final long serialVersionUID = 2005526362699272582L;

    public DaoWrapperNotInitException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
