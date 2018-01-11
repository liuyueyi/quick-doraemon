package com.hust.hui.doraemon.client.trigger;

import com.hust.hui.doraemon.client.listener.IGlobalChangeListener;
import com.hust.hui.doraemon.client.trigger.base.DefaultThreadFactory;
import com.hust.hui.doraemon.core.entity.MetaConf;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yihui on 2017/11/30.
 */
@Slf4j
public class ConfChangeListenerProcessFactory {

    private static ExecutorService executorService = new ThreadPoolExecutor(4,
            10,
            2,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10),
            new DefaultThreadFactory("conf-changeListener-process"),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public static void submit(IGlobalChangeListener globalChangeListener, MetaConf metaConf) {
        executorService.submit(() -> {
                    if (log.isDebugEnabled()) {
                        log.debug("ConfChangeListener:{} Trigger! metaConf: {}", globalChangeListener.getClass().getName(), metaConf);
                    }

                    globalChangeListener.configChanged(metaConf);
                }
        );
    }

}
