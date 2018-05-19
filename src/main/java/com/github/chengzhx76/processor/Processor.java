package com.github.chengzhx76.processor;

import com.github.chengzhx76.Response;
import com.github.chengzhx76.Site;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/3/26
 */
public interface Processor {
    /**
     * 定义处理页面的规则
     * @param response
     */
    void process(Response response);

    /**
     * 定义站点信息
     * @return
     */
    Site getSite();
}
