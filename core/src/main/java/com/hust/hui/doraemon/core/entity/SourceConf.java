package com.hust.hui.doraemon.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yihui on 2017/11/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceConf {
    private static final long serialVersionUID = 1668474036966019111L;

    public static final String SPLIT_TAG = "$#$";

    private String group;

    private String key;

    private String value;

    private String desc;

    private Integer version;



    // 数据源中，保存的规则
    public String getSourceValue() {
        return value + SPLIT_TAG + desc + SPLIT_TAG + version;
    }
}
