package com.hust.hui.doraemon.core.loader;

import com.hust.hui.doraemon.core.entity.MetaConf;
import com.hust.hui.doraemon.core.entity.MetaResponse;
import com.hust.hui.doraemon.core.entity.SourceConf;

/**
 * Created by yihui on 2017/11/28.
 */
public interface ILoader {


    MetaResponse<MetaConf> load(String group, String key);


    /**
     * 新增配置到缓存
     * <p>
     * 若配置已存在，会返回失败
     * 配支持不存在，则写配置到数据源
     *
     * @param sourceConf 配置信息
     * @return 数据源不存在配置，且写入成功，返回成功状态；若配置已存在，则返回配置已存在状态码
     */
    MetaResponse<Boolean> dump(SourceConf sourceConf);


    /**
     * 更新数据源的配置
     * <p>
     * 配置不存在，返回配置不存在的失败状态
     * 配置存在，则采用增量更新 （value值替换， desc为null时，不替换，version+1）
     *
     * @param sourceConf 配置信息
     * @return
     */
    MetaResponse<Boolean> update(SourceConf sourceConf);


    /**
     * 更新配置，若不存在时，新增
     *
     * @param conf
     * @return
     */
    MetaResponse<Boolean> dumpUpdateIfExists(SourceConf conf);

    MetaResponse<Boolean> pushChange(SourceConf sourceConf);

    MetaResponse<MetaConf> popChange(String group);


}
