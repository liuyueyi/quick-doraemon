//package com.hust.hui.doraemon.client.holder;
//
//import com.hust.hui.doraemon.client.MetaClientManager;
//import com.hust.hui.doraemon.core.manager.CacheMetaManager;
//import com.hust.hui.doraemon.core.manager.IMetaConfManager;
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.SmartApplicationListener;
//
//import java.util.Map;
//
///**
// * Created by yihui on 2017/11/29.
// */
//public class ApplicationContextHolder implements SmartApplicationListener {
//    private volatile boolean isLoaded = false;
//
//
//    @Override
//    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
//        return eventType == ContextRefreshedEvent.class;
//    }
//
//    @Override
//    public boolean supportsSourceType(Class<?> aClass) {
//        return true;
//    }
//
//    @Override
//    public void onApplicationEvent(ApplicationEvent applicationEvent) {
//        if (isLoaded) {
//            return;
//        }
//
//        // 扫描所有的同步信息回调处理类，初始化 SynMapperContext 容器
//        ContextRefreshedEvent event = (ContextRefreshedEvent) applicationEvent;
//
//
//        initMetaClientManager(event);
//
//
//        isLoaded = true;
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//
//
//
//    private void initMetaClientManager(ContextRefreshedEvent event) {
//        Map<String, IMetaConfManager> map = event.getApplicationContext().getBeansOfType(IMetaConfManager.class);
//        for (IMetaConfManager manager: map.values()) {
//            // 暂时只用缓存作为数据源的场景，后面可以考虑新增配置数据源，通过配置来选择
//            if (manager instanceof CacheMetaManager) {
//                MetaClientManager.getInstance().setMetaConfManager(manager);
//                return;
//            }
//        }
//    }
//}
