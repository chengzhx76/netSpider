package com.github.chengzhx76.pipeline;


import com.github.chengzhx76.ResultItems;
import com.github.chengzhx76.Task;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/3/25
 */
public interface Pipeline {

    void process(ResultItems items, Task task);
}
