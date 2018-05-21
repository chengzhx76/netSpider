package com.github.chengzhx76;

import com.github.chengzhx76.selector.Selectable;
import com.github.chengzhx76.util.HttpConstant;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

public class Response implements Serializable {

    private Selectable url;

    private byte[] bytes;

    private Selectable rawText;

    private boolean requestSuccess = true;
    // 是否是流
    private boolean streaming = false;
    // 资源名
    private String mediaName;
    // 资源文件名
    private String mediaFileName;

    private int statusCode = HttpConstant.StatusCode.CODE_200;

    private Set<Cookie> cookies = new HashSet<>();

    private Map<String, List<String>> headers;

    private ResultItems resultItems = new ResultItems();

    private List<Request> targetRequests = new ArrayList<>();

    public static Response fail() {
        Response response = new Response();
        response.setRequestSuccess(false);
        return response;
    }

    public Response setSkip(boolean skip) {
        resultItems.setSkip(skip);
        return this;

    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
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

    public boolean isStreaming() {
        return streaming;
    }

    public Response setStreaming(boolean streaming) {
        this.streaming = streaming;
        return this;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public Response setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
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
        return resultItems.getRequest();
    }

    public void setRequest(Request request) {
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
        resultItems.put(key, field);
    }

    public List<Request> getTargetRequests() {
        return targetRequests;
    }

    public void setTargetRequests(List<Request> targetRequests) {
        this.targetRequests = targetRequests;
    }

    // ------------------------文本源添加 Start------------------------------
    public void addTargetUrls(List<String> requestUrls) {
        for (String url : requestUrls) {
            addTargetUrl(url);
        }
    }

    public void addTargetPriorityUrls(List<String> requestUrls, long priority) {
        for (String url : requestUrls) {
            addTargetPriorityUrl(url, priority);
        }
    }

    public void addTargetExtraUrls(List<String> requestUrls, Map<String, Object> extra) {
        for (String url : requestUrls) {
            addTargetExtrasUrl(url, extra);
        }
    }

    public void addTargetUrl(String requestUrl) {
        addTargetExtrasUrl(requestUrl, null);
    }

    public void addTargetPriorityUrl(String requestUrl, long priority) {
        addTargetPriorityAndExtrasUrl(requestUrl, priority, null);
    }

    public void addTargetExtrasUrl(String requestUrl, Map<String, Object> extras) {
        addTargetPriorityAndExtrasUrl(requestUrl, 0L, extras);
    }


    public void addTargetPriorityAndExtrasUrl(String requestUrl, long priority, Map<String, Object> extras) {
        if (checkLegalUrl(requestUrl)) {
            return;
        }
        if (priority != 0L && extras != null) {
            addTargetRequest(Request.createGetRequest(requestUrl).setPriority(priority).setExtra(extras));
        } else if (extras != null){
            addTargetRequest(Request.createGetRequest(requestUrl).setExtra(extras));
        } else if (priority != 0L){
            addTargetRequest(Request.createGetRequest(requestUrl).setPriority(priority));
        } else {
            addTargetRequest(Request.createGetRequest(requestUrl));
        }
    }

    // ------------------------文本源添加 End------------------------------

    public void addTargetRequest(List<Request> requests) {
        for (Request request : requests) {
            addTargetRequest(request);
        }
    }

    /**
     * 添加抓取的请求，可以在需要传递附加信息时使用
     * @param request
     */
    public void addTargetRequest(Request request) {
        targetRequests.add(request);
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
        if (bytes == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < bytes.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(bytes[i]);
            sb.append(']');
        }
        sb.append(", rawText=").append(rawText);
        sb.append(", requestSuccess=").append(requestSuccess);
        sb.append(", statusCode=").append(statusCode);
        sb.append(", cookies=").append(cookies);
        sb.append(", headers=").append(headers);
        sb.append(", resultItems=").append(resultItems);
        sb.append(", targetRequests=").append(targetRequests);
        sb.append('}');
        return sb.toString();
    }
}
