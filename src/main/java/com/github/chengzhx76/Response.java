package com.github.chengzhx76;

import com.github.chengzhx76.selector.PlainText;
import com.github.chengzhx76.selector.Selectable;
import com.github.chengzhx76.util.Constant;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Response implements Serializable {

    private byte[] content;

    private Selectable rawText;

    private Selectable url;

    private boolean requestSuccess;

    private int statusCode;

    private Set<Cookie> cookies = new HashSet<>();

    private Request request;

    private ResultItems items = new ResultItems();

    private List<Request> targetRequest = new ArrayList<>();

    private List<Request> targetMediaRequest = new ArrayList<>();


    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Selectable getRawText() {
        try {
            setRawText(new PlainText(new String(content, "UTF-8")));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return rawText;
    }

    public void setRawText(Selectable rawText) {
        this.rawText = rawText;
    }

    public boolean isRequestSuccess() {
        return requestSuccess;
    }

    public Response setRequestSuccess(boolean requestSuccess) {
        this.requestSuccess = requestSuccess;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Response setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public Response addCookie(String name, String value, String domain, String path) {
        cookies.add(new Cookie(name, value, domain, path));
        return this;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public Selectable getUrl() {
        return url;
    }

    public void setUrl(Selectable url) {
        this.url = url;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public ResultItems getItems() {
        return items;
    }

    public void setItems(ResultItems items) {
        this.items = items;
    }

    public List<Request> getTargetRequest() {
        return targetRequest;
    }

    public void setTargetRequest(List<Request> targetRequest) {
        this.targetRequest = targetRequest;
    }

    public List<Request> getTargetMediaRequest() {
        return targetMediaRequest;
    }

    public void setTargetMediaRequest(List<Request> targetMediaRequest) {
        this.targetMediaRequest = targetMediaRequest;
    }

    // ------------------------媒体资源添加 Start------------------------------

    public void addTargetMediaRequest(List<String> targetUrls) {
        addTargetMediaSubdirRequest(targetUrls, null);
    }

    public void addTargetMediaSubdirRequest(List<String> targetUrls, String subdires) {
        addTargetMediaSubdirTitleRequest(targetUrls, subdires, null);
    }

    public void addTargetMediaTitleRequest(List<String> targetUrls, String title) {
        addTargetMediaSubdirTitleRequest(targetUrls, null, title);
    }

    public void addTargetMediaSubdirTitleRequest(List<String> targetUrls, String subdires, String title) {
        for (String url : targetUrls) {
            addTargetMediaSubdirTitleRequest(url, subdires, title);
        }
    }

    public void addTargetMediaRequest(String targetUrl) {
        addTargetMediaSubdirRequest(targetUrl, null);
    }

    public void addTargetMediaTitleRequest(String targetUrl, String title) {
        addTargetMediaSubdirTitleRequest(targetUrl, null, title);
    }

    public void addTargetMediaSubdirRequest(String targetUrl, String subdires) {
        addTargetMediaSubdirTitleRequest(targetUrl, subdires, null);
    }

    public void addTargetMediaSubdirTitleRequest(String targetUrl, String subdires, String title) {
        if (!StringUtils.isBlank(targetUrl)) {
            if (!StringUtils.isBlank(subdires) && !StringUtils.isBlank(title)) {
                addTargetMediaRequest(Request.createMediaRequest(targetUrl).setSubdires(subdires).putExtra(Constant.TITLE, title));
            } else if (!StringUtils.isBlank(subdires)) {
                addTargetMediaRequest(Request.createMediaRequest(targetUrl).setSubdires(subdires));
            } else if (!StringUtils.isBlank(title)) {
                addTargetMediaRequest(Request.createMediaRequest(targetUrl).putExtra(Constant.TITLE, title));
            } else {
                addTargetMediaRequest(Request.createMediaRequest(targetUrl));
            }
        }
    }

    // ------------------------媒体资源添加 End------------------------------

    // ------------------------文本源添加 Start------------------------------

    public void addTargetRequest(List<String> requestUrls) {
        for (String url : requestUrls) {
            addTargetRequest(url);
        }
    }

    public void addTargetPriorityRequest(List<String> requestUrls, long priority) {
        for (String url : requestUrls) {
            addTargetPriorityRequest(url, priority);
        }
    }

    public void addTargetExtraRequest(List<String> requestUrls, Map<String, Object> extra) {
        for (String url : requestUrls) {
            addTargetExtrasRequest(url, extra);
        }
    }

    public void addTargetRequest(String requestUrl) {
        addTargetExtrasRequest(requestUrl, null);
    }

    public void addTargetPriorityRequest(String requestUrl, long priority) {
        addTargetPriorityAndExtrasRequest(requestUrl, priority, null);
    }

    public void addTargetExtrasRequest(String requestUrl, Map<String, Object> extras) {
        addTargetPriorityAndExtrasRequest(requestUrl, 0, extras);
    }


    public void addTargetPriorityAndExtrasRequest(String requestUrl, long priority, Map<String, Object> extras) {
        if (checkLegalUrl(requestUrl)) {
            return;
        }
        if (priority != 0L && extras != null) {
            addTargetRequest(Request.createHtmlRequest(requestUrl).setPriority(priority).setExtra(extras));
        } else if (extras != null){
            addTargetRequest(Request.createHtmlRequest(requestUrl).setExtra(extras));
        } else if (priority != 0L){
            addTargetRequest(Request.createHtmlRequest(requestUrl).setPriority(priority));
        } else {
            addTargetRequest(Request.createHtmlRequest(requestUrl));
        }
    }

    // ------------------------文本源添加 End------------------------------
    /**
     * 添加抓取的请求，可以在需要传递附加信息时使用
     * @param request
     */
    private void addTargetRequest(Request request) {
        targetRequest.add(request);
    }
    /**
     * 添加抓取的请求，可以在需要传递附加信息时使用
     * @param request
     */
    private void addTargetMediaRequest(Request request) {
        targetMediaRequest.add(request);
    }

    private boolean checkLegalUrl(String addr) {
        return StringUtils.isBlank(addr)
                || addr.equals("#")
                || addr.startsWith("javascript:");
    }

}
