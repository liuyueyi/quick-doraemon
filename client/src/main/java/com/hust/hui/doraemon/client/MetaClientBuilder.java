package com.hust.hui.doraemon.client;

import com.hust.hui.doraemon.client.entity.ClientRegisterConfig;
import com.hust.hui.doraemon.client.holder.MetaClientHolder;
import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;

import java.util.ArrayList;

/**
 * Created by yihui on 2017/11/29.
 */
public class MetaClientBuilder {

   private ClientRegisterConfig registerConfig;

    private MetaClientBuilder(String group) {
        registerConfig = new ClientRegisterConfig();
        registerConfig.setGroup(group);
        registerConfig.setSingleChangeListenerList(new ArrayList<>());
        registerConfig.setGlobalChangeListenerList(new ArrayList<>());
    }


    public MetaClient createMetaClient() {
        return MetaClient.client(this.registerConfig);
    }


    public MetaClientBuilder addGlobalListener(IGlobalChangeListener globalChangeListener) {
        this.registerConfig.getGlobalChangeListenerList().add(globalChangeListener);
        return this;
    }


    public MetaClientBuilder addSingleListener(ISingleChangeListener singleChangeListener) {
        this.registerConfig.getSingleChangeListenerList().add(singleChangeListener);
        return this;
    }


    public static MetaClientBuilder with(String group) {
        return new MetaClientBuilder(group);
    }
}
