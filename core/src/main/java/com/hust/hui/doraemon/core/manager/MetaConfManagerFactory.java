package com.hust.hui.doraemon.core.manager;

import com.hust.hui.doraemon.api.IDaoWrapper;
import com.hust.hui.doraemon.cache.CacheWrapper;

/**
 * Created by yihui on 2018/1/12.
 */
public class MetaConfManagerFactory {

    public static IMetaConfManager createConfManager(IDaoWrapper cacheWrapper) {
        if(cacheWrapper instanceof  CacheWrapper) {
            return new CacheMetaManager((CacheWrapper) cacheWrapper);
        } else {
            return null;
        }
    }


}
