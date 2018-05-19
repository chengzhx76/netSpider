package com.github.chengzhx76;


import com.github.chengzhx76.util.Constant.*;
import com.github.chengzhx76.util.HttpConstant.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/3/25
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    private String url;

    private String method;

    private HttpRequestBody requestBody;

    // 下载的二级目录
    private String subdires;

    // 扩展信息
    private Map<String, Object> extra;

    // 优先级
    private long priority;

    // 类型 html,media,json
    private String type = Type.HTML;

    // 针对每次请求的头信息
    private Map<String, String> headers = new HashMap<>();

    // 针对每次请求的cookie信息
    private List<Cookie> cookies = new ArrayList<>();

    // 是否禁用Cookie管理
    private boolean disableCookie = false;

    // 该请求的编码
    private String charset;

    public Request() {
    }

    public Request(String url, String method, String type) {
        this.url = url;
        this.method = method;
        this.type = type;
    }

    public Request(String url) {
        this.url = url;
    }

    public static Request createTypeRequest(String url, String method, String type) {
        return new Request(url, method, type);
    }

    public static Request createMediaRequest(String url) {
        return createTypeRequest(url, Method.GET, Type.MEDIA);
    }

    public static Request createHtmlGetRequest(String url) {
        return createTypeRequest(url, Method.GET, Type.HTML);
    }

    public static Request createJsonGetRequest(String url) {
        return createTypeRequest(url, Method.GET, Type.JSON);
    }

    public static Request createJsonPostRequest(String url) {
        return createTypeRequest(url, Method.POST, Type.JSON);
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public Object getExtra(String key) {
        if (extra == null) {
            return null;
        }
        return extra.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
        return this;
    }

    public Request setExtra(Map<String, Object> extra) {
        this.extra = extra;
        return this;
    }

    public long getPriority() {
        return priority;
    }

    public Request setPriority(long priority) {
        this.priority = priority;
        return this;
    }
    public Request setSubdires(String subdires) {
        this.subdires = subdires;
        return this;
    }

    public String getSubdires() {
        return subdires;
    }

    public Request setType(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    public Request setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public Request setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Request addHeaders(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public Request addCookies(String name, String value) {
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    public Request addCookies(String name, String value, String domain) {
        this.cookies.add(new Cookie(name, value, domain));
        return this;
    }

    public Request setDisableCookie(boolean disableCookie) {
        this.disableCookie = disableCookie;
        return this;
    }

    public boolean isDisableCookie() {
        return disableCookie;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Request{");
        sb.append("url='").append(url).append('\'');
        sb.append(", subdires='").append(subdires).append('\'');
        sb.append(", extra=").append(extra);
        sb.append(", priority=").append(priority);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
