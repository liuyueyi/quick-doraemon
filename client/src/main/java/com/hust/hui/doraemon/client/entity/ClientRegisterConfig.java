package com.hust.hui.doraemon.client.entity;

import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yihui on 2017/11/29.
 */
@Data
@EqualsAndHashCode
public class ClientRegisterConfig implements Serializable {

    private static final long serialVersionUID = 173186893859351302L;

    private String group;

    private List<ISingleChangeListener> singleChangeListenerList;

    private List<IGlobalChangeListener> globalChangeListenerList;

}
