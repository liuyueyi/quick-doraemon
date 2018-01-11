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
public class Status implements Serializable {
    private static final long serialVersionUID = -2499090592069623853L;

    private int code;

    private String msg;


    public transient static final Status SUCCESS = new Status(200, "success");
    public transient static final Status ERROR = new Status(300, "inner error!");
    public transient static final Status ERROR_CACHE = new Status(301, "redis set/put error!");


    public transient static final Status FAIL_ALREADY_EXISTS = new Status(501, "key already exists!");
    public transient static final Status FAIL_NOT_EXISTS = new Status(502, "key already not exists!");

}
