package com.github.chengzhx76.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.chengzhx76.*;
import com.github.chengzhx76.util.Constant;
import com.github.chengzhx76.util.Constant.Charset;
import com.github.chengzhx76.util.HttpConstant;
import com.github.chengzhx76.util.HttpConstant.Header;
import com.github.chengzhx76.util.HttpConstant.UserAgent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author: 光灿
 * Date: 2018/5/19
 */
public class TimeMp3Processor implements Processor {

    private Site site;

    //private long priority = 1L;

    @Override
    public void process(Response response) {
        try {
            if (!response.isStreaming()) {

                String result = response.getRawText().toString();
                JSONObject resultJson = JSON.parseObject(result);
                JSONObject data = resultJson.getJSONObject("data");

                // 1.先获取列表
                // 2.解析文章列表，获取文章ID
                JSONArray sectionList = data.getJSONArray("list");
                if (sectionList != null && !sectionList.isEmpty()) {

                    //long articleCtime = 0L;

                    List<Request> requests = new ArrayList<>();
                    for (int i = 0; i < sectionList.size(); i++) {
                        JSONObject section = sectionList.getJSONObject(i);
                        int id = section.getIntValue("id");
                        Request request = Request.createPostRequest("https://time.geekbang.org/serv/v1/article")
                                .setRequestBody(HttpRequestBody.json("{\"id\":"+id+"}", Charset.UTF_8))
                                .addHeaders(Header.REFERER, "https://time.geekbang.org/column/article/"+id)
                                .setPriority(0L);

                        response.putField("article_title", section.getString("article_title"));
                        response.putField("params", new String(request.getRequestBody().getBody(), Charset.UTF_8));

                        //articleCtime = section.getLongValue("article_ctime");
                        requests.add(request);
                    }

                    //boolean nextPage = data.getJSONObject("page").getBoolean("more");
                    //if (nextPage) {
                    //    Request request = Request.createPostRequest("https://time.geekbang.org/serv/v1/column/articles")
                    //            .setRequestBody(HttpRequestBody.json("{\"cid\":\"76\",\"size\":20,\"prev\":"+articleCtime+",\"order\":\"newest\"}", Charset.UTF_8));
                    //    requests.add(request);
                    //}
                    //
                    response.addTargetRequest(requests);
                }


                // 3.解析文章，获取mp3地址，4.开始下载
                String audioDownloadUrl = data.getString("audio_download_url");
                if (StringUtils.isNotBlank(audioDownloadUrl)) {
                    Request request = Request.createMediaGetRequest(audioDownloadUrl)
                            .putExtra(Constant.MEDIA_NAME, data.getString("article_title"))
                            .setAddSiteHeader(false)
                            .setAddSiteCookie(false);

                    response.putField(data.getString("article_title"), audioDownloadUrl);

                    response.addTargetRequest(request);
                }
            }
            // 5.获取资源 开始保存
            else {
                FileOutputStream outputStream = new FileOutputStream(new File(site.getMediaDirectory()+response.getRequest().getSubdires().substring(0, 4).trim()+".mp3"));
                IOUtils.write(response.getBytes(), outputStream);
                System.out.println("------------"+response.getRequest().getExtra(Constant.MEDIA_NAME)+"下载完成------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me()
                    .setDomain("time.geekbang.org")
                    .setUserAgent(UserAgent.CHROME)
                    .setCharset(Charset.UTF_8)
                    .addCookie("GCID", "0c0d8a0-6ad9cb7-9448e40-18da930")
                    .addCookie("SERVERID", "fe79ab1762e8fabea8cbf989406ba8f4|1526751086|1526744366")
                    .setHeaders(Header.ACCEPT, HttpConstant.HeaderValue.APPLICATION_JSON_TEXT)
                    .setHeaders(Header.ACCEPT_ENCODING, HttpConstant.HeaderValue.ENCODING)
                    .setHeaders(Header.ACCEPT_LANGUAGE, HttpConstant.HeaderValue.LANGUAGE)
                    .setHeaders(Header.CONNECTION, HttpConstant.HeaderValue.KEEP_ALIVE)
                    .setHeaders(Header.HOST, "time.geekbang.org")
                    .setHeaders(Header.ORIGIN, "https://time.geekbang.org")
                    .setHeaders(Header.REFERER, "https://time.geekbang.org/column/76")
                    .setSleepTime(1000)
                    .setTimeOut(5 * 60 * 1000)
                    .setMediaDirectory("d:\\time\\");
        }
        return site;
    }

    public static void main(String[] args) {
        Request request = Request.createPostRequest("https://time.geekbang.org/serv/v1/column/articles")
                .setRequestBody(HttpRequestBody.json("{\"cid\":\"76\",\"size\":30,\"prev\":0,\"order\":\"newest\"}", Charset.UTF_8));
        //Request request = Request.createJsonPostRequest("https://time.geekbang.org/serv/v1/article")
        //        .setRequestBody(HttpRequestBody.json("{\"id\":\"7276\"}", Charset.UTF_8));
        // Request request = Request.createMediaGetRequest("https://res001.geekbang.org/resource/audio/e6/6e/e6f1176c7e0c4759fd6bbeaa288b6c6e.mp3");

        NetSpider.create(new TimeMp3Processor())
                //.addPipeline(new ConsolePipeline())
                //.setScheduler(new PriorityScheduler())
                .startRequest(request)
                .start();
    }
}
