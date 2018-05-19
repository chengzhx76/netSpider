package com.github.chengzhx76.download;


import com.github.chengzhx76.Cookie;
import com.github.chengzhx76.Request;
import com.github.chengzhx76.Site;
import com.github.chengzhx76.util.HttpConstant;
import com.github.chengzhx76.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Map;

public class HttpUriRequestConverter {

    public HttpClientRequestContext convert(Request request, Site site) {
        HttpClientRequestContext httpClientRequestContext = new HttpClientRequestContext();
        httpClientRequestContext.setHttpClientContext(convertHttpClientContext(request, site));
        httpClientRequestContext.setHttpUriRequest(convertHttpUriRequest(request, site));
        return httpClientRequestContext;
    }

    private HttpClientContext convertHttpClientContext(Request request, Site site) {
        HttpClientContext httpContext = new HttpClientContext();
        // 先判断全局
        if (site.isDisableCookieManagement()) {
            return httpContext;
        }

        // 在判断单个请求
        if (request.isDisableCookie()) {
            CookieStore store = httpContext.getCookieStore();
            if (store != null) {
                store.clear();
            }
            return httpContext;
        }

        // 设置该请求的Cookie
        if (request.getCookies() != null && !request.getCookies().isEmpty()) {
            CookieStore cookieStore = new BasicCookieStore();
            for (Cookie cookieEntry : request.getCookies()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getName(), cookieEntry.getValue());
                if (StringUtils.isNotBlank(cookieEntry.getDomain())) {
                    cookie.setDomain(cookieEntry.getDomain());
                } else {
                    cookie.setDomain(UrlUtils.getDomain(request.getUrl()));
                }
                if (StringUtils.isNotBlank(cookieEntry.getPath())) {
                    cookie.setPath(cookieEntry.getPath());
                }
                cookieStore.addCookie(cookie);
            }
            httpContext.setCookieStore(cookieStore);
        }
        return httpContext;
    }

    private HttpUriRequest convertHttpUriRequest(Request request, Site site) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());

        if (site.getHeaders() != null) {
            for (Map.Entry<String, String> headerEntry : site.getHeaders().entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        if (site != null) {
            requestConfigBuilder.setConnectionRequestTimeout(site.getTimeOut())
                    .setSocketTimeout(site.getTimeOut())
                    .setConnectTimeout(site.getTimeOut())
                    .setCookieSpec(CookieSpecs.STANDARD);
        }

        requestBuilder.setConfig(requestConfigBuilder.build());
        HttpUriRequest httpUriRequest = requestBuilder.build();
        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                httpUriRequest.addHeader(header.getKey(), header.getValue());
            }
        }
        return httpUriRequest;
    }

    private RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            return addFormParams(RequestBuilder.post(), request);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return addFormParams(RequestBuilder.put(), request);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    private RequestBuilder addFormParams(RequestBuilder requestBuilder, Request request) {
        if (request.getRequestBody() != null) {
            ByteArrayEntity entity = new ByteArrayEntity(request.getRequestBody().getBody());
            entity.setContentType(request.getRequestBody().getContentType());
            requestBuilder.setEntity(entity);
        }
        return requestBuilder;
    }

}
