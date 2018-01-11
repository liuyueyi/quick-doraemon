package com.hust.hui.doraemon.cache.builder;

import com.hust.hui.doraemon.cache.CacheWrapper;

/**
 * Created by yihui on 2017/11/29.
 */

public class CacheWrapperBuild {

    private String masterConf;
    private String slaveConf;
    private int maxIdle;
    private int minIdle;
    private int maxTotal;
    private int timeout;
    private int database;
    
    
    public static CacheWrapperBuild builder() {
        return new CacheWrapperBuild();
    }


    public CacheWrapperBuild setMasterConf(String masterConf) {
        this.masterConf = masterConf;
        return this;
    }

    public CacheWrapperBuild setSlaveConf(String slaveConf) {
        this.slaveConf = slaveConf;
        return this;
    }

    public CacheWrapperBuild setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public CacheWrapperBuild setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public CacheWrapperBuild setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public CacheWrapperBuild setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public CacheWrapperBuild setDatabase(int database) {
        this.database = database;
        return this;
    }

    public CacheWrapper build() {
        CacheWrapper cacheWrapper = new CacheWrapper();
        cacheWrapper.setMasterConf(masterConf);
        cacheWrapper.setSlaveConf(slaveConf);
        cacheWrapper.setTimeout(timeout);
        cacheWrapper.setDatabase(database);
        cacheWrapper.setMaxIdle(maxIdle);
        cacheWrapper.setMinIdle(minIdle);
        cacheWrapper.setMaxTotal(maxTotal);
        cacheWrapper.init();
        return cacheWrapper;
    }
}
