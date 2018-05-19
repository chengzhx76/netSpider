package com.github.chengzhx76.download;


import com.github.chengzhx76.Request;
import com.github.chengzhx76.Response;
import com.github.chengzhx76.Site;
import com.github.chengzhx76.selector.PlainText;
import com.github.chengzhx76.util.HttpClientUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpClientDownloader implements Downloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

    private HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();

    private boolean responseHeader = true;


    public void setHttpUriRequestConverter(HttpUriRequestConverter httpUriRequestConverter) {
        this.httpUriRequestConverter = httpUriRequestConverter;
    }


    private CloseableHttpClient getHttpClient(Site site) {
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Response request(Request request, Site site) {
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = getHttpClient(site);
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(request, site);
        Response response = Response.fail();
        try {
            httpResponse = httpClient.execute(requestContext.getHttpUriRequest(), requestContext.getHttpClientContext());
            response = handleResponse(httpResponse, site, request);
            onSuccess(request);
            logger.info("downloading page success {}", request.getUrl());
            return response;
        } catch (IOException e) {
            logger.warn("download page error", e);
            onError(request);
            return response;
        } finally {
            if (httpResponse != null) {
                //ensure the connection is released back to pool
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    private Response handleResponse(HttpResponse httpResponse, Site site, Request request) throws IOException {
        Response response = new Response();
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        // TODO判断是否是资源

        response.setContent(bytes);
        response.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        response.setRawText(new PlainText(new String(bytes, request.getCharset() != null ? request.getCharset() : site.getCharset())));
        response.setRequestSuccess(true);
        response.setRequest(request);
        if (responseHeader) {
            response.setHeaders(HttpClientUtils.convertHeaders(httpResponse.getAllHeaders()));
        }
        return response;
    }


    protected void onSuccess(Request request) {
    }

    protected void onError(Request request) {
    }
}
