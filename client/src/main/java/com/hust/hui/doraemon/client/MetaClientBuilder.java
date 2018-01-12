package com.hust.hui.doraemon.client;

import com.hust.hui.doraemon.api.exception.DaoWrapperNotInitException;
import com.hust.hui.doraemon.client.entity.ClientRegisterConfig;
import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.listener.ISingleChangeListener;
import com.hust.hui.doraemon.core.manager.IMetaConfManager;

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


    public MetaClient createMetaClient() throws DaoWrapperNotInitException {
        check();
        return MetaClient.client(this.registerConfig);
    }


    private void check() throws DaoWrapperNotInitException {
        if (MetaClientManager.getInstance().getMetaConfManager() == null) {
            throw new DaoWrapperNotInitException("IMetaConfManager not init exception!");
        }
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



    public  MetaClientBuilder initMetaConf(IMetaConfManager manager) {
        if (manager != null) {
            MetaClientManager.getInstance().setMetaConfManager(manager);
        }
        return this;
    }
}
