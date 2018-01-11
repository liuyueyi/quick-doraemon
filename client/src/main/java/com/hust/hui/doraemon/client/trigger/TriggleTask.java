package com.hust.hui.doraemon.client.trigger;

import com.hust.hui.doraemon.client.MetaClientManager;
import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;

/**
 * 触发器的任务，定时从变更队列中捞去变更配置，并提交触发器任务执行
 *
 * Created by yihui on 2017/11/30.
 */
public final class TriggleTask implements Runnable {

    private final String group;

    public TriggleTask(String group) {
        this.group = group;
    }


    private volatile int lastSleepTime = 1;

    /**
     * 查询变更队列，判断是否发生变动
     */
    @Override
    public void run() {
        MetaResponse<MetaConf> response = MetaClientManager.getInstance().getChangeConfig(this.group);
        if (response.isSuccess() && response.getData() != null) {
            // 发生了变更， 需要回调触发器
            ConfChangeTrigger.getInstance().notifyConfigChanged(response.getData());
            System.out.println("config changed! res: " + response);

            lastSleepTime = 1;
        } else {
            // 无变更， 延长校对时间
            lastSleepTime = lastSleepTime << 1;
            if (lastSleepTime > 1000) {
                lastSleepTime = 1;
            }
        }

        try {
            Thread.sleep(lastSleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
