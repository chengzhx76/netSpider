package com.github.chengzhx76;

import com.alibaba.fastjson.JSON;
import com.github.chengzhx76.download.Downloader;
import com.github.chengzhx76.download.HttpClientDownloader;
import com.github.chengzhx76.pipeline.ConsolePipeline;
import com.github.chengzhx76.pipeline.Pipeline;
import com.github.chengzhx76.processor.Processor;
import com.github.chengzhx76.scheduler.QueueScheduler;
import com.github.chengzhx76.scheduler.Scheduler;
import com.github.chengzhx76.thread.CountableThreadPool;
import com.github.chengzhx76.util.UrlUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Desc:
 * Author: 光灿
 * Date: 2018/5/19
 */
public class NetSpider implements Runnable, Task {

    private static final Logger LOG = LoggerFactory.getLogger(NetSpider.class);

    private Site site;

    private String uuid;

    // 下载器
    private Downloader downloader;

    // 解析处理器
    private Processor processor;

    // 输出管理
    private List<Pipeline> pipelines = new ArrayList<>();

    // 队列处理器
    private Scheduler scheduler = new QueueScheduler();;

    private List<Request> startRequests;

    private CountableThreadPool threadPool;

    // 线程池
    private ExecutorService executorService;

    // 线程数
    private int threadNum = 1;

    // 初始化状态
    private final static int STATE_INIT = 0;
    // 运行状态
    private final static int STATE_RUNNING = 1;
    // 停止状态
    private final static int STATE_STOPPED = 2;
    // 维护运行状态
    private AtomicInteger state = new AtomicInteger(STATE_INIT);

    // 退出时清理资源
    private boolean destroyWhenExit = true;

    private boolean exitWhenComplete = true;

    private boolean spawnUrl = true;

    private ReentrantLock newUrlLock = new ReentrantLock();

    private Condition newUrlCondition = newUrlLock.newCondition();

    // 解析URL个数
    private final AtomicLong count = new AtomicLong(0);

    // 开始时间
    private Date startTime;

    // 队列中没url等待的时间
    private long emptySleepTime = 30000L;


    public NetSpider(Processor processor) {
        this.processor = processor;
        this.site = processor.getSite();
    }

    /**
     * 创建一个爬虫
     * @param processor
     * @return
     */
    public static NetSpider create(Processor processor) {
        return new NetSpider(processor);
    }

    // 添加url 可以运行时添加
    public NetSpider addUrl(String... urls) {
        for (String url : urls) {
            addRequest(new Request(url));
        }
        signalNewUrl();
        return this;
    }

    public void addRequest(Request request) {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request, this);
    }

    // 添加url 只能在运行前添加
    public NetSpider startUrl(String... startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(Arrays.asList(startUrls));
        return this;
    }

    public NetSpider startRequest(Request request) {
        checkIfRunning();
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        return startRequest(requests);
    }
    public NetSpider startRequest(List<Request> requests) {
        checkIfRunning();
        this.startRequests = requests;
        return this;
    }

    public NetSpider setScheduler(Scheduler scheduler) {
        checkIfRunning();
        Scheduler oldScheduler = this.scheduler;
        this.scheduler = scheduler;
        if (oldScheduler != null) {
            Request request;
            while ((request = oldScheduler.poll(this)) != null) {
                this.scheduler.push(request, this);
            }
        }
        return this;
    }

    public NetSpider addPipeline(Pipeline pipeline) {
        checkIfRunning();
        this.pipelines.add(pipeline);
        return this;
    }

    public NetSpider setPipelines(List<Pipeline> pipelines) {
        checkIfRunning();
        this.pipelines = pipelines;
        return this;
    }

    public NetSpider clearPipeline() {
        pipelines = new ArrayList<Pipeline>();
        return this;
    }

    public NetSpider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }

    public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    public void start() {
        runAsync();
    }

    public NetSpider thread(int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        return this;
    }

    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }

    public NetSpider thread(ExecutorService executorService, int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than one!");
        }
        this.executorService = executorService;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    // 初始化组件 资源
    private void initComponent() {
        // 下载组件
        if (downloader == null) {
            this.downloader = new HttpClientDownloader();
        }
        // 输出组件
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }
        // 下载最大线程
        downloader.setThread(threadNum);
        // 初始化线程池
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        // 添加请求到队列
        if (startRequests != null) {
            for (Request request : startRequests) {
                addRequest(request);
            }
            startRequests.clear();
        }
        // 记录开始时间
        startTime = new Date();
    }

    @Override
    public void run() {
        checkRunningState();
        initComponent();
        LOG.info("Spider {} started!",getUUID());
        while (!Thread.currentThread().isInterrupted() && state.get() == STATE_RUNNING) {
            final Request request = scheduler.poll(this);
            if (request == null) {
                if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                    break;
                }
                // wait until new url added
                waitNewUrl();
            } else {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processRequest(request);
                            onSuccess(request);
                        } catch (Exception e) {
                            onError(request);
                            LOG.error("process request " + request + " error", e);
                        } finally {
                            count.incrementAndGet();
                            signalNewUrl();
                        }
                    }
                });
            }
        }
        state.set(STATE_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
        LOG.info("Spider {} closed! {} pages downloaded.", getUUID(), count.get());
    }

    private void processRequest(Request request) {
        Response response = downloader.request(request, site);
        if (response.isRequestSuccess()){
            onDownloadSuccess(request, response);
        } else {
            onDownloaderFail(request);
        }
    }

    private void onDownloadSuccess(Request request, Response response) {
        if (site.getAcceptStatCode().contains(response.getStatusCode())){
            processor.process(response);
            extractAndAddRequests(response, spawnUrl);
            if (!response.getResultItems().isSkip()) {
                for (Pipeline pipeline : pipelines) {
                    pipeline.process(response.getResultItems(), this);
                }
            }
        } else {
            LOG.info("page status code error, page {} , code: {}", request.getUrl(), response.getStatusCode());
        }
        sleep(site.getSleepTime());
    }

    private void onDownloaderFail(Request request) {
        if (site.getCycleRetryTimes() == 0) {
            sleep(site.getSleepTime());
        } else {
            // for cycle retry
            doCycleRetry(request);
        }
    }

    private void doCycleRetry(Request request) {
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes < site.getCycleRetryTimes()) {
                addRequest(SerializationUtils.clone(request).setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
            }
        }
        sleep(site.getRetrySleepTime());
    }

    protected void extractAndAddRequests(Response response, boolean spawnUrl) {
        if (spawnUrl && !response.getTargetRequests().isEmpty()) {
            for (Request request : response.getTargetRequests()) {
                addRequest(request);
            }
        }
    }

    private void signalNewUrl() {
        try {
            newUrlLock.lock();
            newUrlCondition.signalAll();
        } finally {
            newUrlLock.unlock();
        }
    }

    private void onError(Request request) {
        System.out.println("onError");
    }

    private void onSuccess(Request request) {
        System.out.println("onSuccess");
    }

    private void waitNewUrl() {
        newUrlLock.lock();
        try {
            //double check
            if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warn("waitNewUrl - interrupted, error {}", e);
        } finally {
            newUrlLock.unlock();
        }
    }

    private void close() {
        destroyEach(downloader);
        destroyEach(processor);
        destroyEach(scheduler);
        for (Pipeline pipeline : pipelines) {
            destroyEach(pipeline);
        }
        threadPool.shutdown();
    }

    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    // 针对不同启动爬虫的标识
    @Override
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        if (site != null) {
            return site.getDomain();
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }

    private void checkIfRunning() {
        if (state.get() == STATE_RUNNING) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    private void checkRunningState() {
        while (true) {
            int stateNow = state.get();
            if (stateNow == STATE_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (state.compareAndSet(stateNow, STATE_RUNNING)) {
                break;
            }
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            LOG.error("Thread interrupted when sleep",e);
        }
    }
}
