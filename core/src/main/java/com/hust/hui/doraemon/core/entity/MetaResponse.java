package com.hust.hui.doraemon.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by yihui on 2017/11/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaResponse<T> implements Serializable {
    private static final long serialVersionUID = 2055724583014864616L;

    private Status status;

    private T data;


    public boolean isSuccess() {
        return status.getCode() == Status.SUCCESS.getCode();
    }


    public  static <T> MetaResponse<T> buildSuccess(T data) {
        return new MetaResponse<>(Status.SUCCESS, data);
    }


    public static <T> MetaResponse<T> buildFail(Status status) {
        MetaResponse res =  new MetaResponse<>();
        res.setStatus(status);
        return res;
    }
}
