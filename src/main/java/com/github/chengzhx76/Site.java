package com.github.chengzhx76;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/3/25
 */
public class Site {

    private String domain;

    private String userAgent;

    private String charset;

    // 默认全部站点设置
    private Map<String, String> defaultCookies = new HashMap<>();

    // 所有站点设置
    private Map<String, Map<String, String>> cookies = new HashMap<>();

    // 头信息全局设置
    private Map<String, String> headers = new HashMap<>();

    // 全局的下载休息时间 ms
    private long sleepTime = 3000L;

    // 重试的次数
    private int retryTimes = 0;

    // 循环重试次数
    private int cycleRetryTimes = 0;

    // 重试休息时间
    private long retrySleepTime = 1000;

    private int timeOut = 5000;

    // 媒体资源下载路径
    private String mediaDirectory;

    // 禁止全局Cookie管理
    private boolean disableCookieManagement = false;

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

    static {
        DEFAULT_STATUS_CODE_SET.add(200);
    }

    /**
     * 创建一个Site对象，等价于new Site()
     *
     * @return 新建的对象
     */
    public static Site me() {
        return new Site();
    }

    /**
     * 获取已设置的Domain
     * @return
     */
    public String getDomain() {
        return domain;
    }
    /**
     * 设置这个站点所在域名。<br>
     * 目前不支持多个域名的抓取。抓取多个域名请新建一个Spider。
     *
     * @param domain 爬虫会抓取的域名
     * @return this
     */
    public Site setDomain(String domain) {
        this.domain = domain;
        return this;
    }
    /**
     * 获取已设置的user-agent
     *
     * @return 已设置的user-agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 为这个站点设置user-agent，很多网站都对user-agent进行了限制，不设置此选项可能会得到期望之外的结果。
     *
     * @param userAgent userAgent
     * @return this
     */
    public Site setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * 获取已设置的编码
     *
     * @return 已设置的domain
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置页面编码，若不设置则自动根据Html meta信息获取。<br>
     * 一般无需设置encoding，如果发现下载的结果是乱码，则可以设置此项。<br>
     *
     * @param charset 编码格式，主要是"utf-8"、"gbk"两种
     * @return this
     */
    public Site setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * 为这个站点添加一个cookie，可用于抓取某些需要登录访问的站点。这个cookie的域名与{@link #getDomain()}是一致的
     *
     * @param name  cookie的名称
     * @param value cookie的值
     * @return this
     */
    public Site addCookie(String name, String value) {
        this.defaultCookies.put(name, value);
        return this;
    }

    public Site addCookies(String domain, String name, String value) {
        if (!cookies.containsKey(domain)){
            cookies.put(domain,new HashMap<String, String>());
        }
        cookies.get(domain).put(name, value);
        return this;
    }


    /**
     * 获取已经设置的所有cookie
     *
     * @return 已经设置的所有cookie
     */
    public Map<String, String> getCookies() {
        return defaultCookies;
    }

    public Map<String, Map<String, String>> getAllCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Site setHeaders(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * 获取两次抓取之间的间隔
     *
     * @return 两次抓取之间的间隔，单位毫秒
     */
    public long getSleepTime() {
        return sleepTime;
    }


    /**
     * 设置两次抓取之间的间隔，避免对目标站点压力过大(或者避免被防火墙屏蔽...)。
     *
     * @param sleepTime 单位毫秒
     * @return this
     */
    public Site setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    /**
     * 获取重新下载的次数，默认为0
     *
     * @return 重新下载的次数
     */
    public int getRetryTimes() {
        return retryTimes;
    }

    /**
     * 设置获取重新下载的次数，默认为0
     *
     * @return this
     */
    public Site setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public Site setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
        return this;
    }

    public long getRetrySleepTime() {
        return retrySleepTime;
    }

    public Site setRetrySleepTime(long retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public Site setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public boolean isDisableCookieManagement() {
        return disableCookieManagement;
    }

    public Site setDisableCookieManagement(boolean disableCookieManagement) {
        this.disableCookieManagement = disableCookieManagement;
        return this;
    }

    /**
     * 设置下载的资源文件路径
     * @param mediaDirectory
     * @return
     */
    public Site setMediaDirectory(String mediaDirectory) {
        this.mediaDirectory = mediaDirectory;
        return this;
    }

    /**
     * 获取下载资源路径
     * @return
     */
    public String getMediaDirectory() {
        return mediaDirectory;
    }

    /**
     * 获取可接受的状态码
     *
     * @return 可接受的状态码
     */
    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }

    /**
     * 设置可接受的http状态码，仅当状态码在这个集合中时，才会读取页面内容。<br>
     * 默认为200，正常情况下，无须设置此项。<br>
     * 某些站点会错误的返回状态码，此时可以对这个选项进行设置。<br>
     *
     * @param acceptStatCode 可接受的状态码
     * @return this
     */
    public Site setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
        return this;
    }
}
