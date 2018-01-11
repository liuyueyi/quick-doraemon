package com.hust.hui.doraemon.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by yihui on 2017/11/28.
 */
@Data
@NoArgsConstructor
public class MetaConf implements Serializable {

    private static final long serialVersionUID = -3990916282201260626L;

    private String group;

    private String key;

    private String value;
}
