package com.github.chengzhx76;

import com.github.chengzhx76.selector.Selectable;
import com.github.chengzhx76.util.Constant;
import com.github.chengzhx76.util.HttpConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

public class Response implements Serializable {

    private Selectable url;

    private byte[] content;

    private Selectable rawText;

    private boolean requestSuccess = true;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private Set<Cookie> cookies = new HashSet<>();

    private Map<String,List<String>> headers;

    private Request request;

    private ResultItems resultItems = new ResultItems();

    private List<Request> targetRequest = new ArrayList<>();

    private List<Request> targetMediaRequest = new ArrayList<>();


    public static Response fail() {
        Response response = new Response();
        response.setRequestSuccess(false);
        return response;
    }

    public Response setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Selectable getRawText() {
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
        this.resultItems.setRequest(request);
    }


    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Response setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public ResultItems getResultItems() {
        return resultItems;
    }

    public Response setResultItems(ResultItems resultItems) {
        this.resultItems = resultItems;
        return this;
    }

    public void putField(String key, Object field) {
        resultItems.putField(key, field);
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Response{");
        sb.append("url=").append(url);
        sb.append(", content=");
        if (content == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < content.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(content[i]);
            sb.append(']');
        }
        sb.append(", rawText=").append(rawText);
        sb.append(", requestSuccess=").append(requestSuccess);
        sb.append(", statusCode=").append(statusCode);
        sb.append(", cookies=").append(cookies);
        sb.append(", headers=").append(headers);
        sb.append(", request=").append(request);
        sb.append(", resultItems=").append(resultItems);
        sb.append(", targetRequest=").append(targetRequest);
        sb.append(", targetMediaRequest=").append(targetMediaRequest);
        sb.append('}');
        return sb.toString();
    }
}
