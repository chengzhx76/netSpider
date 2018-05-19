package com.github.chengzhx76.scheduler;


import com.github.chengzhx76.Request;
import com.github.chengzhx76.Task;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/3/26
 */
public class QueueScheduler implements Scheduler {

    private BlockingQueue<Request> queue = new LinkedBlockingDeque<>();

    private Set<String> urls = new HashSet<>();

    @Override
    public synchronized void push(Request request, Task task) {
        if (urls.add(request.getUrl())) {
            queue.add(request);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        return queue.poll();
    }
}
