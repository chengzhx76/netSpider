package com.github.chengzhx76.pipeline;

import com.github.chengzhx76.ResultItems;
import com.github.chengzhx76.Task;

import java.util.Map;

/**
 * Desc: 控制台输出测试结果
 * Author: 光灿
 * Date: 2017/3/26
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems items, Task task) {
        System.out.println("------------------------------------------------");
        System.out.println("get "+items.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : items.getAll().entrySet()) {
            System.out.println(entry.getKey()+":\t"+entry.getValue());
        }
    }
}
